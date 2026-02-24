package ch.neon.releasetracker.release.unit.adapter.in;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.neon.releasetracker.common.exception.ReleaseNotFoundException;
import ch.neon.releasetracker.common.security.SecurityConfig;
import ch.neon.releasetracker.release.adapter.in.ReleaseAuditController;
import ch.neon.releasetracker.release.adapter.out.ReleaseAuditService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReleaseAuditController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ReleaseAuditControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ReleaseAuditService auditService;

  @MockitoBean private JwtDecoder jwtDecoder;

  private final UUID uuid = UUID.randomUUID();

  @Test
  @DisplayName("1. Admin can access history - Should return 200 OK")
  @WithMockUser(roles = "ADMIN")
  void getHistory_AsAdmin_Success() throws Exception {
    when(auditService.getReleaseHistory(uuid)).thenReturn(List.of());

    mockMvc.perform(get("/v1/releases/{uuid}/history", uuid)).andExpect(status().isOk());
  }

  @Test
  @DisplayName("2. Ordinary user cannot access history - Should return 403 Forbidden")
  @WithMockUser(roles = "USER")
  void getHistory_AsUser_Forbidden() throws Exception {
    mockMvc.perform(get("/v1/releases/{id}/history", uuid)).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("3. Unauthenticated user cannot access history - Should return 401 Unauthorized")
  void getHistory_Anonymous_Unauthorized() throws Exception {
    mockMvc.perform(get("/v1/releases/{id}/history", uuid)).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("4. History for non-existent release - Should return 404 Not Found")
  @WithMockUser(roles = "ADMIN")
  void getHistory_NotFound() throws Exception {
    when(auditService.getReleaseHistory(uuid)).thenThrow(new ReleaseNotFoundException(uuid));

    mockMvc.perform(get("/v1/releases/{uuid}/history", uuid)).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("5. Invalid UUID format - Should return 400 Bad Request")
  @WithMockUser(roles = "ADMIN")
  void getHistory_InvalidUuid() throws Exception {
    mockMvc.perform(get("/v1/releases/not-a-uuid/history")).andExpect(status().isBadRequest());
  }
}
