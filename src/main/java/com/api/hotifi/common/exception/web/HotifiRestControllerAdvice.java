package com.api.hotifi.common.exception.web;

import com.api.hotifi.common.exception.FieldErrorDetail;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.exception.error.ErrorCode;
import com.api.hotifi.common.exception.error.ErrorCodes;
import com.api.hotifi.common.exception.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class HotifiRestControllerAdvice extends ResponseEntityExceptionHandler {

    private MessageSource messageSource;

    @Autowired
    public HotifiRestControllerAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception,
            Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        // for all exceptions that are not overriden, the body is null, so we can
        // just provide new body based on error message and call super method
        var apiError = Objects.isNull(body)
                ? "Null" // <--
                : body;
        return super.handleExceptionInternal(exception, apiError, headers, status, request);
    }

    @ExceptionHandler(HotifiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleHotifiException(HotifiException exception) {
        ErrorCode errorcode = exception.getErrorCode();
        return new ResponseEntity<>(new ErrorResponse(errorcode), HttpStatus.valueOf(errorcode.getHttpStatusCode()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> anyError(Throwable exception) {
        log.error("Error occurred", exception);
        ErrorCode errorCode = ErrorCodes.INTERNAL_ERROR;
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.valueOf(errorCode.getHttpStatusCode()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        log.error("Input field validation failed");
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        List<FieldErrorDetail> fieldErrorDetails = processFieldErrors(fieldErrors);
        List<String> errorMessages = fieldErrorDetails.stream().map(FieldErrorDetail::getMessage).collect(Collectors.toList());
        ErrorCode errorCode = new ErrorCode(ErrorCode.INPUT_INVALID_CODE, errorMessages, 400);
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.valueOf(errorCode.getHttpStatusCode()));
    }

    private List<FieldErrorDetail> processFieldErrors(List<FieldError> fieldErrors) {
        List<FieldErrorDetail> errors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
            errors.add(
                    FieldErrorDetail
                            .builder()
                            .field(fieldError.getField())
                            .message(localizedErrorMessage)
                            .build()
            );
        }
        return errors;
    }

    private String resolveLocalizedErrorMessage(FieldError fieldError) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(fieldError, currentLocale);
    }
}
