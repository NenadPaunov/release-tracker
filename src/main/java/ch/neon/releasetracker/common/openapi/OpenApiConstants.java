package ch.neon.releasetracker.common.openapi;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OpenApiConstants {
  public final String OK_CODE = "200";
  public final String CREATED_CODE = "201";
  public final String NO_CONTENT = "204";
  public final String BAD_REQUEST_CODE = "400";
  public final String UNAUTHENTICATED_CODE = "401";
  public final String UNAUTHORISED_CODE = "403";
  public final String NOT_FOUND_CODE = "404";
  public final String CONFLICT_CODE = "409";
  public final String INTERNAL_SERVER_ERROR_CODE = "500";

  public final String OK_MESSAGE = "Successful operation";
  public final String NO_CONTENT_MESSAGE = "Successful operation, no content";
  public final String BAD_REQUEST_MESSAGE = "Bad Request";
  public final String UNAUTHENTICATED_MESSAGE = "Unauthenticated";
  public final String UNAUTHORISED_MESSAGE = "Forbidden";
  public final String NOT_FOUND_MESSAGE = "Not Found";
  public final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
}
