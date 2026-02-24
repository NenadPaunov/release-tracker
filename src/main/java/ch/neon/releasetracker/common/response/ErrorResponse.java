package ch.neon.releasetracker.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    String errorCode,
    String message,
    Integer status,
    LocalDateTime timestamp,
    @JsonInclude(JsonInclude.Include.NON_NULL) List<ValidationError> errors) {
  public ErrorResponse(String errorCode, Integer status, String message) {
    this(errorCode, message, status, LocalDateTime.now(), null);
  }

  public ErrorResponse(
      String errorCode, Integer status, String message, List<ValidationError> errors) {
    this(errorCode, message, status, LocalDateTime.now(), errors);
  }

  public record ValidationError(String field, String message) {}
}
