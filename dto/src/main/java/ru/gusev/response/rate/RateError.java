package ru.gusev.response.rate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error returned when a currency rate cannot be provided")
public record RateError(
        @Schema(description = "Machine-readable error code", example = "CURRENCY_NOT_SUPPORTED")
        String code,
        @Schema(description = "Human-readable error description", example = "Currency GBP is not supported")
        String message
) {
}
