package ch.neon.releasetracker.release.application.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Detailed information about a single change in the release history")
public record ReleaseAuditResponse(
    @Schema(description = "The unique revision number", example = "42") Long revisionId,
    @Schema(
            description = "Type of operation",
            allowableValues = {"ADD", "MOD", "DEL"},
            example = "MOD")
        String revisionType,
    @Schema(description = "Username of the person who performed the change", example = "admin_user")
        String modifiedBy,
    @Schema(description = "Timestamp when the change occurred", example = "2024-02-21T14:30:00")
        LocalDateTime modifiedAt,
    @Schema(
            description = "Snapshot of the audited fields at the time of this revision",
            example = "{\"name\": \"v1.0.0\", \"status\": \"PROD\"}")
        Map<String, Object> diff) {}
