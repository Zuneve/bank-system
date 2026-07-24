package ru.gusev.request.rate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Kafka request for a currency rate")
public record RateRequestMessage(
        @Schema(description = "Three-letter currency code", example = "USD")
        String currency
) {
}
