package ru.gusev.response.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.gusev.auth.authuser_info.RoleType;

import java.util.UUID;

@Schema(description = "Created administrator authentication account")
public record CreateAdminResponse(
        @Schema(description = "Authentication user identifier")
        UUID id,
        @Schema(description = "Administrator username used for login")
        String username,
        @Schema(description = "Authentication account role")
        RoleType roleType
) {
}
