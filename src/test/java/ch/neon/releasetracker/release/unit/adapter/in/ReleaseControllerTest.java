package ch.neon.releasetracker.release.unit.adapter.in;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.neon.releasetracker.common.exception.ReleaseNotFoundException;
import ch.neon.releasetracker.common.security.SecurityConfig;
import ch.neon.releasetracker.release.adapter.in.ReleaseController;
import ch.neon.releasetracker.release.application.ReleaseService;
import ch.neon.releasetracker.release.application.request.ReleaseRequest;
import ch.neon.releasetracker.release.application.response.ReleaseResponse;
import ch.neon.releasetracker.release.domain.enums.ReleaseStatus;
import ch.neon.releasetracker.release.unit.ReleaseTestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReleaseController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ReleaseControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private ReleaseService releaseService;
  @MockitoBean private JwtDecoder jwtDecoder;

  private final UUID uuid = UUID.randomUUID();

  @Test
  @DisplayName("1. GET /v1/releases - Should allow USER role")
  @WithMockUser(roles = "USER")
  void getReleases_UserRole_ReturnsOk() throws Exception {
    when(releaseService.getReleases(any(), any())).thenReturn(Page.empty());

    mockMvc.perform(get("/v1/releases")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("2. GET /v1/releases/{uuid} - Success returns 200")
  @WithMockUser(roles = "USER")
  void getById_Success_ReturnsOk() throws Exception {
    ReleaseResponse response = ReleaseTestData.createDefaultResponse(uuid);

    when(releaseService.getReleaseById(uuid)).thenReturn(response);

    mockMvc
        .perform(get("/v1/releases/{uuid}", uuid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(uuid.toString()))
        .andExpect(jsonPath("$.name").value("v1.0"));
  }

  @Test
  @DisplayName("3. GET /v1/releases/{uuid} - Not found returns 404")
  @WithMockUser(roles = "USER")
  void getById_NotFound_ReturnsNotFound() throws Exception {
    when(releaseService.getReleaseById(uuid)).thenThrow(new ReleaseNotFoundException(uuid));

    mockMvc.perform(get("/v1/releases/{uuid}", uuid)).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("4. POST /v1/releases - Should return 403 for USER role")
  @WithMockUser(roles = "USER")
  void create_UserRole_ReturnsForbidden() throws Exception {
    ReleaseRequest request = ReleaseTestData.createDefaultRequest();

    mockMvc
        .perform(
            post("/v1/releases")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("5. POST /v1/releases - Should return 201 for ADMIN role")
  @WithMockUser(roles = "ADMIN")
  void create_AdminRole_ReturnsCreated() throws Exception {
    ReleaseRequest request = ReleaseTestData.createDefaultRequest();
    ReleaseResponse response = ReleaseTestData.createDefaultResponse(uuid);

    when(releaseService.createRelease(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/v1/releases")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(uuid.toString()))
        .andExpect(jsonPath("$.name").value("v1.0"));
  }

  @Test
  @DisplayName("6. PUT /v1/releases/{uuid} - Success returns 200")
  @WithMockUser(roles = "ADMIN")
  void update_Success_ReturnsOk() throws Exception {
    ReleaseRequest request =
        new ReleaseRequest(
            "v1.0-updated",
            "Updated Desc",
            ReleaseStatus.ON_PROD,
            LocalDateTime.now().plusDays(1),
            1L);
    ReleaseResponse response =
        new ReleaseResponse(
            uuid, "v1.0-updated", "Updated Desc", ReleaseStatus.ON_PROD, request.releaseDate(), 2L);

    when(releaseService.updateRelease(eq(uuid), any())).thenReturn(response);

    mockMvc
        .perform(
            put("/v1/releases/{uuid}", uuid)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(uuid.toString()))
        .andExpect(jsonPath("$.name").value("v1.0-updated"))
        .andExpect(jsonPath("$.status").value("On PROD"));
  }

  @Test
  @DisplayName("7. PUT /v1/releases/{uuid} - Missing version returns 400")
  @WithMockUser(roles = "ADMIN")
  void update_MissingVersion_ReturnsBadRequest() throws Exception {
    ReleaseRequest invalidRequest =
        new ReleaseRequest("v1.0", "Desc", ReleaseStatus.CREATED, LocalDateTime.now(), null);

    mockMvc
        .perform(
            put("/v1/releases/{uuid}", uuid)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("8. PUT /v1/releases/{uuid} - Validation fail returns 400")
  @WithMockUser(roles = "ADMIN")
  void update_InvalidRequest_ReturnsBadRequest() throws Exception {
    ReleaseRequest invalidRequest =
        new ReleaseRequest("", "Desc", ReleaseStatus.CREATED, LocalDateTime.now(), 1L);

    mockMvc
        .perform(
            put("/v1/releases/{uuid}", uuid)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("9. PUT /v1/releases/{uuid} - Not found returns 404")
  @WithMockUser(roles = "ADMIN")
  void update_NotFound_ReturnsNotFound() throws Exception {
    ReleaseRequest request =
        new ReleaseRequest("v1.0", "Desc", ReleaseStatus.CREATED, LocalDateTime.now(), 1L);

    when(releaseService.updateRelease(eq(uuid), any()))
        .thenThrow(new ReleaseNotFoundException(uuid));

    mockMvc
        .perform(
            put("/v1/releases/{uuid}", uuid)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("10. PUT /v1/releases/{uuid} - Optimistic locking returns 409")
  @WithMockUser(roles = "ADMIN")
  void update_OptimisticLockingFailure_ReturnsConflict() throws Exception {
    ReleaseRequest request =
        new ReleaseRequest("v1.0", "Desc", ReleaseStatus.CREATED, LocalDateTime.now(), 1L);

    when(releaseService.updateRelease(eq(uuid), any()))
        .thenThrow(new OptimisticLockingFailureException("Version mismatch"));

    mockMvc
        .perform(
            put("/v1/releases/{uuid}", uuid)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("11. DELETE /v1/releases/{uuid} - Admin can soft delete")
  @WithMockUser(roles = "ADMIN")
  void delete_AdminRole_ReturnsNoContent() throws Exception {
    mockMvc
        .perform(delete("/v1/releases/{uuid}", uuid).with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("12. DELETE /v1/releases/{uuid} - Not found returns 404")
  @WithMockUser(roles = "ADMIN")
  void delete_NotFound_ReturnsNotFound() throws Exception {
    doThrow(new ReleaseNotFoundException(uuid)).when(releaseService).deleteRelease(uuid);

    mockMvc
        .perform(delete("/v1/releases/{uuid}", uuid).with(csrf()))
        .andExpect(status().isNotFound());
  }
}
