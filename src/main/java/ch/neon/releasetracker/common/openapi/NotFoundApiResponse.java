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
    responseCode = OpenApiConstants.NOT_FOUND_CODE,
    description = OpenApiConstants.NOT_FOUND_MESSAGE,
    content =
        @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  value =
                      "{\"error\": \"NOT_FOUND\", \"status\": \"404\", \"message\": \"Not Found\"}")
            }))
public @interface NotFoundApiResponse {}
