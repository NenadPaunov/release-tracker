package ch.neon.releasetracker.release.application.request;

import ch.neon.releasetracker.release.domain.enums.ReleaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Schema(description = "Payload for creating or updating a release")
public record ReleaseRequest(
    @Schema(
            description = "Unique name of the release",
            example = "v1.2.0-Spring-Cleanup",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100)
        @NotBlank(message = "Name is mandatory")
        @Size(max = 100, message = "Name can not have more then 100 characters")
        String name,
    @Schema(
            description = "Detailed description of what this release includes",
            example = "Fixes for login bug and new dashboard widgets",
            maxLength = 2000)
        @Size(max = 2000, message = "Description can not have more then 2000 characters")
        String description,
    @Schema(
            implementation = ReleaseStatus.class,
            description = "Initial status of the release",
            example = "On PROD",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Status is mandatory")
        ReleaseStatus status,
    @Schema(
            type = "string",
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            description =
                "Planned date for the release. If empty, current server time will be used.",
            example = "2026-03-01T10:00:00")
        LocalDateTime releaseDate,
    @Schema(
            description =
                "Version for optimistic locking (Required for UPDATE, ignored for CREATE)",
            example = "1",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Long version) {}
