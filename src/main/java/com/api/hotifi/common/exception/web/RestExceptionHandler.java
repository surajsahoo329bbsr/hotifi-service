package com.api.hotifi.common.exception.web;

import com.api.hotifi.common.exception.errors.ErrorCodes;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @NonNull
    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException e, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        //TODO add JSON malformed error code
        return new ResponseEntity<>(ErrorCodes.INTERNAL_ERROR, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleAllExceptions(Exception e) {
        return new ResponseEntity<>(e, INTERNAL_SERVER_ERROR);
    }

    //other exception handlers below
    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<?> handleDataIntegrityViolationException(Exception e){
        return new ResponseEntity<>(e, INTERNAL_SERVER_ERROR);
    }

}
