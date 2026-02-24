package ch.neon.releasetracker.common.exception;

import org.springframework.http.HttpStatus;

public class ReleaseNameAlreadyExistsException extends BusinessException {
  public ReleaseNameAlreadyExistsException(String name) {
    super(
        "RELEASE_NAME_EXISTS", "Release with requested name already exists.", HttpStatus.CONFLICT);
  }
}
