package ch.neon.releasetracker.release.application.response;

import ch.neon.releasetracker.release.domain.enums.ReleaseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object representing a software release")
public record ReleaseResponse(
    @Schema(
            description = "Unique identifier of the release",
            example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,
    @Schema(description = "Name of the release", example = "v1.2.0-Spring-Cleanup") String name,
    @Schema(
            description = "Detailed description of the release changes",
            example = "Includes security patches and UI improvements")
        String description,
    @Schema(
            implementation = ReleaseStatus.class,
            description = "Current lifecycle status of the release",
            example = "ON_PROD")
        ReleaseStatus status,
    @Schema(
            type = "string",
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            description = "The scheduled or actual date of the release",
            example = "2026-03-01T10:00:00")
        LocalDateTime releaseDate,
    @Schema(description = "Current version for optimistic locking", example = "1") Long version) {}
