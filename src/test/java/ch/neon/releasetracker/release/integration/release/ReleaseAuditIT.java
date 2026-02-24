package ch.neon.releasetracker.release.integration.release;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.neon.releasetracker.common.config.TestJpaConfig;
import ch.neon.releasetracker.release.application.request.ReleaseRequest;
import ch.neon.releasetracker.release.application.response.ReleaseResponse;
import ch.neon.releasetracker.release.domain.enums.ReleaseStatus;
import ch.neon.releasetracker.release.integration.BaseIntegrationTest;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestJpaConfig.class)
class ReleaseAuditIT extends BaseIntegrationTest {

  @Test
  @DisplayName("Should track history and allow ADMIN to retrieve it")
  void shouldTrackHistoryAndAllowAdminToRetrieve() throws Exception {
    LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
    ReleaseRequest createRequest =
        new ReleaseRequest("Initial", "Desc", ReleaseStatus.CREATED, futureDate, 0L);

    String createResponse =
        mockMvc
            .perform(
                post("/v1/releases")
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ReleaseResponse created = objectMapper.readValue(createResponse, ReleaseResponse.class);
    UUID releaseUuid = created.id();

    ReleaseRequest updateRequest =
        new ReleaseRequest("Updated", "Desc", ReleaseStatus.DONE, futureDate, 0L);

    mockMvc
        .perform(
            put("/v1/releases/{uuid}", releaseUuid)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            get("/v1/releases/{uuid}/history", releaseUuid)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].revisionType").value("ADD"))
        .andExpect(jsonPath("$[1].revisionType").value("MOD"))
        .andExpect(jsonPath("$[1].diff.name").value("Updated"));
  }

  @Test
  @DisplayName("Should return 403 Forbidden for history when user is ROLE_USER")
  void shouldReturnForbiddenForNonAdmin() throws Exception {
    mockMvc
        .perform(
            get("/v1/releases/{uuid}/history", UUID.randomUUID())
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
        .andExpect(status().isForbidden());
  }
}
