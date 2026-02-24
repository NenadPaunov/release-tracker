package ch.neon.releasetracker.release.adapter.in;

import ch.neon.releasetracker.common.openapi.CommonApiResponses;
import ch.neon.releasetracker.common.openapi.NotFoundApiResponse;
import ch.neon.releasetracker.common.openapi.OpenApiConstants;
import ch.neon.releasetracker.release.adapter.out.ReleaseAuditService;
import ch.neon.releasetracker.release.application.response.ReleaseAuditResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/releases")
@RequiredArgsConstructor
@Tag(name = "Release History API")
public class ReleaseAuditController {

  private final ReleaseAuditService releaseAuditService;

  @Operation(
      summary = "Get audit history for a specific release",
      description =
          "Returns a chronological list of all changes made to a release. Restricted to ADMIN users.")
  @ApiResponse(
      responseCode = OpenApiConstants.OK_CODE,
      description = "Audit history retrieved successfully",
      content =
          @Content(
              array = @ArraySchema(schema = @Schema(implementation = ReleaseAuditResponse.class))))
  @NotFoundApiResponse
  @CommonApiResponses
  @GetMapping(value = "/{uuid}/history", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public List<ReleaseAuditResponse> getHistory(@PathVariable UUID uuid) {
    return releaseAuditService.getReleaseHistory(uuid);
  }
}
