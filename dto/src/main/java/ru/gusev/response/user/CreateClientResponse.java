package ru.gusev.response.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Created client authentication account")
public record CreateClientResponse(
        @Schema(description = "Authentication user identifier")
        UUID id,
        @Schema(description = "Client username used for login")
        String username,
        @Schema(description = "Linked bank user identifier")
        UUID userId
) {
}
