package ch.neon.releasetracker.common.exception;

import org.springframework.http.HttpStatus;

public class ReleaseNotFoundException extends BusinessException {
  public ReleaseNotFoundException(Object id) {
    super(
        "RELEASE_NOT_FOUND",
        String.format("Release with requested id %s was not found.", id),
        HttpStatus.NOT_FOUND);
  }
}
