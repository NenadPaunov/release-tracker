package ch.neon.releasetracker.release.domain.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReleaseSearchElement {
  SEARCH_TERM("searchTerm", "name,description"),
  DATE_FROM("dateFrom", "releaseDate"),
  DATE_TO("dateTo", "releaseDate"),
  STATUSES("statuses", "status");

  private final String paramName;
  private final String fieldName;

  public static ReleaseSearchElement getFromParamName(String paramName) {
    return Arrays.stream(values())
        .filter(e -> e.getParamName().equals(paramName))
        .findFirst()
        .orElse(null);
  }
}
