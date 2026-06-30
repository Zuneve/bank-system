package ru.gusev.request.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request for creating a bank account")
public record CreateAccountRequest(
        @Schema(description = "Identifier of the account owner", example = "c473bd07-9add-4274-8ad9-163718b2999f")
        @NotNull
        UUID userId
) {
}
