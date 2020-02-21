package com.example.demo.exception;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ResponseExceptionHandler 
  extends ResponseEntityExceptionHandler {
 
    @ExceptionHandler(value= { DuplicateReferencesException.class})
    protected ResponseEntity<Object> handleDuplicateReference(
      RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Duplicate References Found from Input file";
        return handleExceptionInternal(ex, bodyOfResponse, 
          new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }
    @ExceptionHandler(value= { IOException.class})
    protected ResponseEntity<Object> handleIO(
      RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Configured location not found";
        return handleExceptionInternal(ex, bodyOfResponse, 
          new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}