package ch.neon.releasetracker.release.unit.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.neon.releasetracker.common.exception.InvalidReleaseDateException;
import ch.neon.releasetracker.release.application.ReleaseValidator;
import ch.neon.releasetracker.release.domain.Release;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReleaseValidatorTest {

  private final ReleaseValidator releaseValidator = new ReleaseValidator();

  @Test
  @DisplayName("1. validateDate - Should pass when date is null")
  void validateDate_NullDate_Success() {
    assertDoesNotThrow(() -> releaseValidator.validateDate(null));
  }

  @Test
  @DisplayName("2. validateDate - Should throw exception when date is in past")
  void validateDate_PastDate_ThrowsException() {
    LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
    assertThrows(InvalidReleaseDateException.class, () -> releaseValidator.validateDate(pastDate));
  }

  @Test
  @DisplayName("3. validateDate - Should pass when date is in future")
  void validateDate_FutureDate_Success() {
    LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
    assertDoesNotThrow(() -> releaseValidator.validateDate(futureDate));
  }

  @Test
  @DisplayName("4. validateForUpdate - Should not trigger validation if dates are identical")
  void validateForUpdate_SameDate_NoValidation() {
    LocalDateTime now = LocalDateTime.now();
    Release existing = new Release();
    existing.setReleaseDate(now);

    LocalDateTime pastDate = now.minusDays(10);
    existing.setReleaseDate(pastDate);

    assertDoesNotThrow(() -> releaseValidator.validateForUpdate(existing, pastDate));
  }

  @Test
  @DisplayName("5. validateForUpdate - Should trigger validation if date is changed to past")
  void validateForUpdate_ChangedToPast_ThrowsException() {
    Release existing = new Release();
    existing.setReleaseDate(LocalDateTime.now().plusDays(5));

    LocalDateTime newPastDate = LocalDateTime.now().minusDays(1);

    assertThrows(
        InvalidReleaseDateException.class,
        () -> releaseValidator.validateForUpdate(existing, newPastDate));
  }
}
