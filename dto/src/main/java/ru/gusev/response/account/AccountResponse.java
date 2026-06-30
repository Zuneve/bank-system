package ru.gusev.response.account;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Bank account data")
public record AccountResponse(
        @Schema(description = "Account identifier", example = "e19530c8-8d8b-4f8c-8099-c61fb0dd74fb")
        UUID accountId,
        @Schema(description = "Account owner's identifier", example = "c473bd07-9add-4274-8ad9-163718b2999f")
        UUID ownerId,
        @Schema(description = "Current account balance", example = "299.00")
        BigDecimal balance
) {
}
