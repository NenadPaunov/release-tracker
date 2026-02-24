package ch.neon.releasetracker.common.exception;

public class ReleaseValidationException extends RuntimeException {
  public ReleaseValidationException(String message) {
    super(message);
  }
}
