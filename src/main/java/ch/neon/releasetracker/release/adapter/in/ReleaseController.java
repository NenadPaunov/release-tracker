package ch.neon.releasetracker.release.adapter.in;

import ch.neon.releasetracker.common.exception.ReleaseValidationException;
import ch.neon.releasetracker.common.openapi.BadRequestApiResponse;
import ch.neon.releasetracker.common.openapi.CommonApiResponses;
import ch.neon.releasetracker.common.openapi.NotFoundApiResponse;
import ch.neon.releasetracker.common.openapi.OpenApiConstants;
import ch.neon.releasetracker.release.application.ReleaseService;
import ch.neon.releasetracker.release.application.request.ReleaseRequest;
import ch.neon.releasetracker.release.application.response.ReleaseResponse;
import ch.neon.releasetracker.release.domain.ReleaseSearchParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/releases")
@RequiredArgsConstructor
@Tag(name = "Release API")
public class ReleaseController {
  private final ReleaseService releaseService;

  @Operation(
      summary = "Get a paginated list of releases",
      description = "Returns a page of releases based on filtering criteria and pagination.")
  @ApiResponse(responseCode = OpenApiConstants.OK_CODE, description = OpenApiConstants.OK_MESSAGE)
  @CommonApiResponses
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public Page<ReleaseResponse> getReleases(
      @ParameterObject ReleaseSearchParams params,
      @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          @ParameterObject
          Pageable pageable) {
    return releaseService.getReleases(params, pageable);
  }

  @Operation(
      summary = "Get a release by UUID",
      description = "Returns detailed information about a specific release.")
  @ApiResponse(
      responseCode = OpenApiConstants.OK_CODE,
      description = OpenApiConstants.OK_MESSAGE,
      content = @Content(schema = @Schema(implementation = ReleaseResponse.class)))
  @NotFoundApiResponse
  @CommonApiResponses
  @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ReleaseResponse getById(@PathVariable UUID uuid) {
    return releaseService.getReleaseById(uuid);
  }

  @Operation(
      summary = "Create a new release",
      description = "Creates a new release record in the system.")
  @ApiResponse(
      responseCode = OpenApiConstants.CREATED_CODE,
      description = "Release created successfully")
  @BadRequestApiResponse
  @CommonApiResponses
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  public ReleaseResponse create(@Valid @RequestBody ReleaseRequest request) {
    return releaseService.createRelease(request);
  }

  @Operation(
      summary = "Update an existing release",
      description = "Updates the details of an existing release by its UUID.")
  @ApiResponse(
      responseCode = OpenApiConstants.OK_CODE,
      description = "Release updated successfully")
  @NotFoundApiResponse
  @BadRequestApiResponse
  @CommonApiResponses
  @PutMapping(
      value = "/{uuid}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public ReleaseResponse update(
      @PathVariable UUID uuid, @Valid @RequestBody ReleaseRequest request) {

    if (request.version() == null) {
      throw new ReleaseValidationException(
          "Version is required for updates to ensure data consistency");
    }

    return releaseService.updateRelease(uuid, request);
  }

  @Operation(
      summary = "Delete a release",
      description = "Performs a soft delete on a release record.")
  @ApiResponse(
      responseCode = OpenApiConstants.NO_CONTENT,
      description = OpenApiConstants.NO_CONTENT_MESSAGE)
  @NotFoundApiResponse
  @CommonApiResponses
  @DeleteMapping("/{uuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable UUID uuid) {
    releaseService.deleteRelease(uuid);
  }
}
