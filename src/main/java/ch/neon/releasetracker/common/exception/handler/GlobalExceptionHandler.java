package ch.neon.releasetracker.common.exception.handler;

import ch.neon.releasetracker.common.exception.BusinessException;
import ch.neon.releasetracker.common.exception.ReleaseValidationException;
import ch.neon.releasetracker.common.response.ErrorResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
    log.warn(
        "Business rule violation: code={}, message={}, status={}",
        ex.getErrorCode(),
        ex.getMessage(),
        ex.getStatus());

    return buildResponse(ex.getStatus(), ex.getErrorCode(), ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    List<ErrorResponse.ValidationError> validationErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                error ->
                    new ErrorResponse.ValidationError(error.getField(), error.getDefaultMessage()))
            .toList();

    log.warn("Validation failed for {} fields", validationErrors.size());

    ErrorResponse response =
        new ErrorResponse(
            "VALIDATION_FAILED",
            HttpStatus.BAD_REQUEST.value(),
            "Input validation failed",
            validationErrors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
    log.error("Database integrity violation", ex);
    return buildResponse(
        HttpStatus.CONFLICT, "DATABASE_ERROR", "Data integrity violation occurred.");
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
    log.error("Access denied", ex);
    return buildResponse(
        HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You dont have permission to perform this action.");
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    String message =
        String.format("The parameter '%s' has an invalid value: '%s'", ex.getName(), ex.getValue());
    log.warn("Type mismatch error: {}", message);
    return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", message);
  }

  @ExceptionHandler(ReleaseValidationException.class)
  public ResponseEntity<ErrorResponse> handleReleaseValidationException(
      ReleaseValidationException ex) {
    log.error("Invalid argument", ex);
    return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", ex.getMessage());
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  public ResponseEntity<ErrorResponse> handleOptimisticLock(OptimisticLockingFailureException ex) {
    log.error("Resource was modified by another user", ex);
    return buildResponse(HttpStatus.CONFLICT, "CONFLICT", "Resource was modified by another user.");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex) {
    log.error("Internal Server Error: ", ex);
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred.");
  }

  private ResponseEntity<ErrorResponse> buildResponse(
      HttpStatus status, String errorCode, String message) {
    return ResponseEntity.status(status)
        .body(new ErrorResponse(errorCode, status.value(), message));
  }
}
