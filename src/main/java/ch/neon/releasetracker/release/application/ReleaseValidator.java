package ch.neon.releasetracker.release.application;

import ch.neon.releasetracker.common.exception.InvalidReleaseDateException;
import ch.neon.releasetracker.release.domain.Release;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ReleaseValidator {

  public void validateDate(LocalDateTime releaseDate) {
    Optional.ofNullable(releaseDate)
        .filter(date -> date.isBefore(LocalDateTime.now()))
        .ifPresent(
            date -> {
              throw new InvalidReleaseDateException();
            });
  }

  public void validateForUpdate(Release existingRelease, LocalDateTime newDate) {
    Optional.ofNullable(newDate)
        .filter(date -> !date.equals(existingRelease.getReleaseDate()))
        .ifPresent(this::validateDate);
  }
}
