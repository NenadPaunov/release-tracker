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
    responseCode = OpenApiConstants.BAD_REQUEST_CODE,
    description = OpenApiConstants.BAD_REQUEST_MESSAGE,
    content =
        @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  value =
                      "{\"error\": \"BAD_REQUEST\", \"status\": \"400\", \"message\": \"Bad request\"}")
            }))
public @interface BadRequestApiResponse {}
