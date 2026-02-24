package ch.neon.releasetracker.release.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Filter parameters for searching releases")
public record ReleaseSearchParams(
    @Schema(
            description = "Search by name or description (case-insensitive)",
            example = "v1.2.0-Spring-Cleanup")
        String searchTerm,
    @Schema(description = "Filter releases from this date onwards", example = "2024-01-01")
        LocalDate dateFrom,
    @Schema(description = "Filter releases up to this date", example = "2024-12-31")
        LocalDate dateTo,
    @Schema(description = "List of statuses to filter by", example = "[\"ON_PROD\", \"DONE\"]")
        List<String> statuses) {}
