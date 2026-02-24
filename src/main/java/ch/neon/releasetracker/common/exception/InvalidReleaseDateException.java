package ch.neon.releasetracker.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidReleaseDateException extends BusinessException {
  public InvalidReleaseDateException() {
    super("RELEASE_DATE_IN_PAST", "Release date cannot be in the past.", HttpStatus.BAD_REQUEST);
  }
}
