package ru.gusev.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.gusev.user.info.HairColor;

import java.util.UUID;

@Schema(description = "User data")
public record UserResponse(
        @Schema(description = "User identifier", example = "c473bd07-9add-4274-8ad9-163718b2999f")
        UUID id,
        @Schema(description = "Unique user login", example = "kriper2004")
        String login,
        @Schema(description = "User's display name", example = "Anton")
        String name,
        @Schema(description = "User's age", example = "19")
        int age,
        @Schema(description = "Whether the user is male", example = "true")
        boolean isMale,
        @Schema(description = "User's hair color", example = "Black")
        HairColor hairColor

) { }
