package ch.neon.releasetracker.release.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReleaseStatus {
  CREATED("Created"),
  IN_DEVELOPMENT("In Development"),
  ON_DEV("On DEV"),
  QA_DONE_ON_DEV("QA Done on DEV"),
  ON_STAGING("On staging"),
  QA_DONE_ON_STAGING("QA done on STAGING"),
  ON_PROD("On PROD"),
  DONE("Done");

  private final String displayName;

  @JsonValue
  public String getDisplayName() {
    return displayName;
  }

  @JsonCreator
  public static ReleaseStatus fromValue(String value) {
    return Stream.of(ReleaseStatus.values())
        .filter(status -> status.displayName.equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format("Invalid release status value: %s", value)));
  }
}
