package ch.neon.releasetracker.release.integration.release;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.neon.releasetracker.common.config.TestJpaConfig;
import ch.neon.releasetracker.release.application.request.ReleaseRequest;
import ch.neon.releasetracker.release.domain.Release;
import ch.neon.releasetracker.release.domain.enums.ReleaseStatus;
import ch.neon.releasetracker.release.infrastructure.ReleaseRepository;
import ch.neon.releasetracker.release.integration.BaseIntegrationTest;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestJpaConfig.class)
@Transactional
public class ReleaseControllerIT extends BaseIntegrationTest {

  private static final String BASE_URL = "/v1/releases";

  @Autowired private ReleaseRepository releaseRepository;

  @Test
  void shouldCreateRelease_WhenUserIsAdmin() throws Exception {
    ReleaseRequest request = createValidRequest("Release 1.0");

    mockMvc
        .perform(
            post(BASE_URL)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Release 1.0"))
        .andExpect(jsonPath("$.version").value(1));
  }

  @Test
  void shouldReturn403_WhenUserIsNotAdmin() throws Exception {
    ReleaseRequest request = createValidRequest("Non-Admin Release");

    mockMvc
        .perform(
            post(BASE_URL)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldThrowException_WhenUpdateVersionIsNull() throws Exception {
    UUID id = UUID.randomUUID();
    ReleaseRequest request =
        new ReleaseRequest("Name", "Desc", ReleaseStatus.CREATED, LocalDateTime.now(), null);

    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", id)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message")
                .value("Version is required for updates to ensure data consistency"));
  }

  @Test
  void shouldFail_WhenReleaseNameAlreadyExists() throws Exception {
    Release release = new Release();
    release.setName("Unique Name");
    release.setDescription("1.0.0");
    release.setStatus(ReleaseStatus.CREATED);
    release.setReleaseDate(LocalDateTime.now().plusDays(1));
    releaseRepository.save(release);

    ReleaseRequest duplicateRequest = createValidRequest("Unique Name");

    mockMvc
        .perform(
            post(BASE_URL)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldGetPaginatedReleases() throws Exception {
    saveRelease("Alpha");
    saveRelease("Beta");

    mockMvc
        .perform(
            get(BASE_URL)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(greaterThanOrEqualTo(2)));
  }

  @Test
  void shouldGetReleaseById() throws Exception {
    Release release = new Release();
    release.setName("Get-Test");
    release.setDescription("1.0.0");
    release.setStatus(ReleaseStatus.CREATED);
    release.setReleaseDate(LocalDateTime.now().plusDays(1));
    Release saved = releaseRepository.save(release);

    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}", saved.getId())
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Get-Test"))
        .andExpect(jsonPath("$.id").value(saved.getId().toString()));
  }

  private ReleaseRequest createValidRequest(String name) {
    return new ReleaseRequest(
        name, "Description", ReleaseStatus.CREATED, LocalDateTime.now().plusDays(1), 0L);
  }

  private void saveRelease(String name) {
    Release r = new Release();
    r.setName(name);
    r.setStatus(ReleaseStatus.CREATED);
    r.setReleaseDate(LocalDateTime.now().plusDays(1));
    releaseRepository.save(r);
  }
}
