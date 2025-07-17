package org.flexisaf.studbud.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API error response")
public record ApiErrorResponse(Boolean success, Object data, String message, @JsonProperty("error_type") ErrorType errorType) {}
