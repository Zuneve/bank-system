package ru.gusev.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request for creating an administrator authentication account")
public record CreateAdminRequest(
        @Schema(description = "Username used for authentication", example = "admin2")
        @NotBlank
        String username,
        @Schema(description = "Reserved login field, currently not used by the service", example = "admin2")
        String login,
        @Schema(description = "Raw password. It is stored as a BCrypt hash", example = "adminPassword123", minLength = 8)
        @NotBlank
        @Size(min = 8)
        String password
) {
}
