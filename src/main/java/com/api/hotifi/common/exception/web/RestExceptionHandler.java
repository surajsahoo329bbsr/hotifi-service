package com.api.hotifi.common.exception.web;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @NonNull
    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException e, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        //TODO add JSON malformed error code
        return new ResponseEntity<>(ErrorCodes.INVALID_PAYLOAD, INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> getErrorResponse(HttpHeaders headers, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponse(ErrorCodes.INVALID_HEADER), headers, status);
    }


    @NonNull
    protected ResponseEntity<Object> handleServletRequestBindingException(@NonNull ServletRequestBindingException ex,
                                                                          @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return getErrorResponse(headers, status);
    }


    @ExceptionHandler(HotifiException.class)
    public ResponseEntity<ErrorResponse> handleError(HotifiException exception) {
        log.error("Error occurred", exception);
        ErrorCode errorCode = exception.getErrorCode();
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.valueOf(errorCode.getHttpStatusCode()));
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status, @NonNull WebRequest request) {

        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();
        ErrorCode errorCode = new ErrorCode(ErrorCode.INPUT_INVALID_CODE, Collections.singletonList(objectErrors.toString()), 400);
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.valueOf(errorCode.getHttpStatusCode()));

    }

    /*@ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleAllExceptions(Exception e) {
        ErrorCode errorCode = new ErrorCode(NOT_FOUND.toString(), Collections.singletonList(e.getMessage()), NOT_FOUND.value());
        return new ResponseEntity<>(errorCode, HttpStatus.valueOf(errorCode.getHttpStatusCode()));
    }*/

    //other exception handlers below
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handlerConstraintViolationException(ConstraintViolationException exception) {
        List<String> errorMessages = Collections.singletonList(exception.getConstraintName());
        ErrorCode errorCode = new ErrorCode(ErrorCode.INPUT_INVALID_CODE, errorMessages, 400);
        return new ResponseEntity<>(new ErrorResponse(errorCode), HttpStatus.valueOf(errorCode.getHttpStatusCode()));
    }

}
