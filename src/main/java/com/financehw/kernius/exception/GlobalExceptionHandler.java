package com.financehw.kernius.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// TODO: useless
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericException(Exception ex) {
    LOGGER.error("Handling general exception", ex);
    return new ResponseEntity<>(
        "Unexpected error happened, please contact developers", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<String> handleApiException(ApiException ex) {
    LOGGER.error("Handling API exception", ex);
    return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
    LOGGER.error("Handling argument not valid exception", ex);
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
