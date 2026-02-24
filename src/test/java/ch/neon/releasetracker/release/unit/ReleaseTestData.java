package ch.neon.releasetracker.release.unit;

import ch.neon.releasetracker.release.application.request.ReleaseRequest;
import ch.neon.releasetracker.release.application.response.ReleaseResponse;
import ch.neon.releasetracker.release.domain.Release;
import ch.neon.releasetracker.release.domain.ReleaseSearchParams;
import ch.neon.releasetracker.release.domain.enums.ReleaseStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class ReleaseTestData {

  private ReleaseTestData() {}

  public static ReleaseRequest createDefaultRequest() {
    return new ReleaseRequest(
        "v1.0",
        "Default description",
        ReleaseStatus.CREATED,
        LocalDateTime.now().plusDays(7),
        null);
  }

  public static Release createDefaultRelease() {
    return createDefaultRelease(UUID.randomUUID());
  }

  public static Release createDefaultRelease(UUID id) {
    Release release = new Release();
    release.setId(id);
    release.setName("v1.0");
    release.setDescription("Default description");
    release.setStatus(ReleaseStatus.CREATED);
    release.setReleaseDate(LocalDateTime.now().plusDays(7));
    release.setVersion(1L);
    return release;
  }

  public static ReleaseResponse createDefaultResponse() {
    return createDefaultResponse(UUID.randomUUID());
  }

  public static ReleaseResponse createDefaultResponse(UUID id) {
    return new ReleaseResponse(
        id,
        "v1.0",
        "Default description",
        ReleaseStatus.CREATED,
        LocalDateTime.now().plusDays(7),
        1L);
  }

  public static ReleaseSearchParams createDefaultParams() {
    return new ReleaseSearchParams(
        "test-release",
        LocalDate.now().minusDays(7),
        LocalDate.now().plusDays(7),
        List.of(ReleaseStatus.CREATED.name(), ReleaseStatus.IN_DEVELOPMENT.name()));
  }

  public static ReleaseRequest createRequestWithName(String name) {
    return new ReleaseRequest(
        name,
        "Description for " + name,
        ReleaseStatus.CREATED,
        LocalDateTime.now().plusDays(7),
        null);
  }
}
