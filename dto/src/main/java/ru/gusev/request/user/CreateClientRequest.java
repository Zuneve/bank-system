package ru.gusev.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.gusev.user.info.HairColor;

@Schema(description = "Request for creating a client authentication account and linked banking user")
public record CreateClientRequest(
        @Schema(description = "Username used for authentication", example = "client1")
        @NotBlank
        String username,
        @Schema(description = "Raw password. It is stored as a BCrypt hash", example = "clientPassword123", minLength = 8)
        @NotBlank
        @Size(min = 8)
        String password,

        @Schema(description = "Unique banking user login", example = "client1_login")
        @NotBlank
        String login,
        @Schema(description = "Client display name", example = "Client One")
        @NotBlank
        String name,
        @Schema(description = "Client age", example = "20", minimum = "1")
        @Min(1)
        int age,
        @Schema(description = "Whether the client is male", example = "true")
        boolean male,
        @Schema(description = "Client hair color", example = "Black")
        @NotNull
        HairColor hairColor
) {}
