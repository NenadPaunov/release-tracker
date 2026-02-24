package ch.neon.releasetracker.release.unit.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import ch.neon.releasetracker.common.exception.InvalidReleaseDateException;
import ch.neon.releasetracker.common.exception.ReleaseNameAlreadyExistsException;
import ch.neon.releasetracker.common.exception.ReleaseNotFoundException;
import ch.neon.releasetracker.release.application.ReleaseService;
import ch.neon.releasetracker.release.application.ReleaseValidator;
import ch.neon.releasetracker.release.application.mapper.ReleaseMapper;
import ch.neon.releasetracker.release.application.request.ReleaseRequest;
import ch.neon.releasetracker.release.application.response.ReleaseResponse;
import ch.neon.releasetracker.release.domain.Release;
import ch.neon.releasetracker.release.domain.ReleaseSearchParams;
import ch.neon.releasetracker.release.domain.enums.ReleaseStatus;
import ch.neon.releasetracker.release.infrastructure.ReleaseRepository;
import ch.neon.releasetracker.release.unit.ReleaseTestData;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ReleaseServiceTest {

  @Mock private ReleaseRepository releaseRepository;
  @Mock private ReleaseMapper releaseMapper;
  @Mock private ReleaseValidator releaseValidator;

  @InjectMocks private ReleaseService releaseService;

  private final UUID uuid = UUID.randomUUID();

  @Nested
  @DisplayName("createRelease()")
  class CreateReleaseTests {

    @Test
    @DisplayName("Success - creates release with provided date")
    void create_Success_WithProvidedDate() {
      ReleaseRequest request = ReleaseTestData.createDefaultRequest();
      Release entity = ReleaseTestData.createDefaultRelease(uuid);
      ReleaseResponse response = ReleaseTestData.createDefaultResponse(uuid);

      when(releaseMapper.map(request)).thenReturn(entity);
      when(releaseRepository.existsByName("v1.0")).thenReturn(false);
      when(releaseRepository.save(entity)).thenReturn(entity);
      when(releaseMapper.map(entity)).thenReturn(response);

      ReleaseResponse result = releaseService.createRelease(request);

      assertThat(result.name()).isEqualTo("v1.0");
      assertThat(result.version()).isEqualTo(1L);
      verify(releaseValidator).validateDate(any(LocalDateTime.class));
      verify(releaseRepository).save(entity);
    }

    @Test
    @DisplayName("Success - sets releaseDate to now when null")
    void create_Success_NullDate_UsesNow() {
      ReleaseRequest request =
          new ReleaseRequest("v1.0", "Desc", ReleaseStatus.CREATED, null, null);
      Release entity = ReleaseTestData.createDefaultRelease();
      entity.setReleaseDate(null);

      when(releaseMapper.map(request)).thenReturn(entity);
      when(releaseRepository.existsByName("v1.0")).thenReturn(false);
      when(releaseRepository.save(any(Release.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));
      when(releaseMapper.map(any(Release.class)))
          .thenReturn(ReleaseTestData.createDefaultResponse(uuid));

      ReleaseResponse result = releaseService.createRelease(request);

      assertThat(result.releaseDate()).isNotNull();
      verify(releaseValidator).validateDate(null);
    }

    @Test
    @DisplayName("Throws ReleaseNameAlreadyExistsException when name exists")
    void create_NameExists_ThrowsException() {
      ReleaseRequest request = ReleaseTestData.createDefaultRequest();
      Release entity = ReleaseTestData.createDefaultRelease();

      when(releaseMapper.map(request)).thenReturn(entity);
      when(releaseRepository.existsByName("v1.0")).thenReturn(true);

      assertThrows(
          ReleaseNameAlreadyExistsException.class, () -> releaseService.createRelease(request));
      verify(releaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws InvalidReleaseDateException when date is in past")
    void create_PastDate_ThrowsException() {
      ReleaseRequest request =
          new ReleaseRequest(
              "v1.0", "Desc", ReleaseStatus.CREATED, LocalDateTime.now().minusDays(1), null);
      Release entity = ReleaseTestData.createDefaultRelease();
      entity.setReleaseDate(request.releaseDate());

      when(releaseMapper.map(request)).thenReturn(entity);
      doThrow(new InvalidReleaseDateException())
          .when(releaseValidator)
          .validateDate(request.releaseDate());

      assertThrows(InvalidReleaseDateException.class, () -> releaseService.createRelease(request));
      verify(releaseRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("getReleaseById()")
  class GetReleaseByIdTests {

    @Test
    @DisplayName("Success - returns mapped response")
    void getById_Success() {
      Release entity = ReleaseTestData.createDefaultRelease(uuid);
      ReleaseResponse response = ReleaseTestData.createDefaultResponse(uuid);

      when(releaseRepository.findById(uuid)).thenReturn(Optional.of(entity));
      when(releaseMapper.map(entity)).thenReturn(response);

      ReleaseResponse result = releaseService.getReleaseById(uuid);

      assertThat(result.id()).isEqualTo(uuid);
      assertThat(result.name()).isEqualTo("v1.0");
    }

    @Test
    @DisplayName("Throws ReleaseNotFoundException when ID not found")
    void getById_NotFound_ThrowsException() {
      when(releaseRepository.findById(uuid)).thenReturn(Optional.empty());

      assertThrows(ReleaseNotFoundException.class, () -> releaseService.getReleaseById(uuid));
    }
  }

  @Nested
  @DisplayName("getReleases()")
  class GetReleasesTests {

    @Test
    @DisplayName("Success - returns paginated results with specification")
    void getReleases_Success_WithParams() {
      ReleaseSearchParams params = ReleaseTestData.createDefaultParams();
      Pageable pageable = PageRequest.of(0, 10);
      Release entity = ReleaseTestData.createDefaultRelease(uuid);
      Page<Release> entityPage = new PageImpl<>(List.of(entity), pageable, 1);
      ReleaseResponse response = ReleaseTestData.createDefaultResponse(uuid);
      Page<ReleaseResponse> responsePage = new PageImpl<>(List.of(response), pageable, 1);

      when(releaseRepository.findAll(any(Specification.class), eq(pageable)))
          .thenReturn(entityPage);
      when(releaseMapper.map(entity)).thenReturn(response);

      Page<ReleaseResponse> result = releaseService.getReleases(params, pageable);

      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().getFirst().name()).isEqualTo("v1.0");
      verify(releaseRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Success - returns empty page when no results")
    void getReleases_Success_EmptyResult() {
      ReleaseSearchParams params = ReleaseTestData.createDefaultParams();
      Pageable pageable = PageRequest.of(0, 10);
      Page<Release> emptyPage = new PageImpl<>(List.of(), pageable, 0);

      when(releaseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

      Page<ReleaseResponse> result = releaseService.getReleases(params, pageable);

      assertThat(result.getContent()).isEmpty();
      assertThat(result.getTotalElements()).isZero();
    }
  }

  @Nested
  @DisplayName("updateRelease()")
  class UpdateReleaseTests {

    @Test
    @DisplayName("Success - updates all fields")
    void update_Success() {
      Release existing = ReleaseTestData.createDefaultRelease(uuid);
      existing.setName("v1.0-old");

      ReleaseRequest request =
          new ReleaseRequest(
              "v1.0-new", "New Desc", ReleaseStatus.ON_PROD, LocalDateTime.now().plusDays(1), 1L);

      when(releaseRepository.findById(uuid)).thenReturn(Optional.of(existing));
      when(releaseRepository.existsByName("v1.0-new")).thenReturn(false);
      when(releaseRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));
      when(releaseMapper.map(existing))
          .thenReturn(
              new ReleaseResponse(
                  uuid, "v1.0-new", "New Desc", ReleaseStatus.ON_PROD, request.releaseDate(), 2L));

      ReleaseResponse result = releaseService.updateRelease(uuid, request);

      assertThat(result.name()).isEqualTo("v1.0-new");
      assertThat(result.status()).isEqualTo(ReleaseStatus.ON_PROD);
      verify(releaseValidator).validateForUpdate(existing, request.releaseDate());
      verify(releaseRepository).save(existing);
    }

    @Test
    @DisplayName("Throws ReleaseNotFoundException when ID not found")
    void update_NotFound_ThrowsException() {
      ReleaseRequest request = ReleaseTestData.createDefaultRequest();
      when(releaseRepository.findById(uuid)).thenReturn(Optional.empty());

      assertThrows(
          ReleaseNotFoundException.class, () -> releaseService.updateRelease(uuid, request));
      verify(releaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Throws ReleaseNameAlreadyExistsException when new name exists")
    void update_NameConflict_ThrowsException() {
      Release existing = ReleaseTestData.createDefaultRelease(uuid);
      existing.setName("v1.0-old");

      ReleaseRequest request = ReleaseTestData.createRequestWithName("v1.0-conflict");

      when(releaseRepository.findById(uuid)).thenReturn(Optional.of(existing));
      when(releaseRepository.existsByName("v1.0-conflict")).thenReturn(true);

      assertThrows(
          ReleaseNameAlreadyExistsException.class,
          () -> releaseService.updateRelease(uuid, request));
      verify(releaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Allows update when name is unchanged even if it exists")
    void update_SameName_Allowed() {
      Release existing = ReleaseTestData.createDefaultRelease(uuid);
      existing.setName("v1.0");
      existing.setStatus(ReleaseStatus.CREATED);

      ReleaseRequest request =
          new ReleaseRequest("v1.0", "Updated Desc", ReleaseStatus.ON_DEV, LocalDateTime.now(), 1L);

      when(releaseRepository.findById(uuid)).thenReturn(Optional.of(existing));
      when(releaseRepository.save(existing)).thenReturn(existing);
      when(releaseMapper.map(existing))
          .thenReturn(
              new ReleaseResponse(
                  uuid, "v1.0", "Updated Desc", ReleaseStatus.ON_DEV, LocalDateTime.now(), 2L));

      ReleaseResponse result = releaseService.updateRelease(uuid, request);

      assertThat(result.description()).isEqualTo("Updated Desc");
      verify(releaseRepository, never()).existsByName("v1.0");
    }
  }

  @Nested
  @DisplayName("deleteRelease()")
  class DeleteReleaseTests {

    @Test
    @DisplayName("Success - soft deletes via repository")
    void delete_Success() {
      when(releaseRepository.existsById(uuid)).thenReturn(true);

      releaseService.deleteRelease(uuid);

      verify(releaseRepository).deleteById(uuid);
    }

    @Test
    @DisplayName("Throws ReleaseNotFoundException when ID does not exist")
    void delete_NotFound_ThrowsException() {
      when(releaseRepository.existsById(uuid)).thenReturn(false);

      assertThrows(ReleaseNotFoundException.class, () -> releaseService.deleteRelease(uuid));
      verify(releaseRepository, never()).deleteById(uuid);
    }
  }
}
