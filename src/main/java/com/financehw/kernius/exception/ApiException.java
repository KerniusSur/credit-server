package com.financehw.kernius.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
  private final String message;
  private final HttpStatus status;
  private final Map<String, Object> labels = new HashMap<>();

  public ApiException(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }

  public static ApiException notFound(String message) {
    return new ApiException(message, HttpStatus.NOT_FOUND);
  }

  public static ApiException bad(String message) {
    return new ApiException(message, HttpStatus.BAD_REQUEST);
  }

  public static ApiException unauthorized(String message) {
    return new ApiException(message, HttpStatus.UNAUTHORIZED);
  }

  public static ApiException conflict(String message) {
    return new ApiException(message, HttpStatus.CONFLICT);
  }

  public static ApiException reject(String message) {
    return new ApiException(message, HttpStatus.NOT_ACCEPTABLE);
  }

  public ApiException addLabel(String key, Object value) {
    labels.put(key, value);
    return this;
  }

  public HttpStatus getStatus() {
    return status;
  }

  @Override
  public String getMessage() {
    if (labels.isEmpty()) {
      return this.message;
    }
    return this.message
        + " "
        + labels.keySet().stream()
            .map(key -> key + "=" + labels.get(key))
            .collect(Collectors.joining(", "));
  }
}
