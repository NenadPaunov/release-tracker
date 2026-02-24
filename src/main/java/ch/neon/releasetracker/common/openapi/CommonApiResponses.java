package ch.neon.releasetracker.common.openapi;

import ch.neon.releasetracker.common.response.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.MediaType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@ApiResponse(
    responseCode = OpenApiConstants.UNAUTHENTICATED_CODE,
    description = OpenApiConstants.UNAUTHENTICATED_MESSAGE,
    content = @Content)
@ApiResponse(
    responseCode = OpenApiConstants.UNAUTHORISED_CODE,
    description = OpenApiConstants.UNAUTHORISED_MESSAGE,
    content = @Content)
@ApiResponse(
    responseCode = OpenApiConstants.INTERNAL_SERVER_ERROR_CODE,
    description = OpenApiConstants.INTERNAL_SERVER_ERROR_MESSAGE,
    content =
        @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  value =
                      "{\"error\": \"INTERNAL_SERVER_ERROR\", \"status\": \"500\", \"message\": \"Internal server error\"}")
            }))
public @interface CommonApiResponses {}
