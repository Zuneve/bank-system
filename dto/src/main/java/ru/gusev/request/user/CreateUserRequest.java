package ru.gusev.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.gusev.user.info.HairColor;

@Schema(description = "Request for creating user")
public record CreateUserRequest(
        @Schema(description = "Unique user login", example = "kriper2004")
        @NotBlank
        String login,
        @Schema(description = "User's display name", example = "Anton")
        @NotBlank
        String name,
        @Schema(description = "User's age", example = "19", minimum = "1")
        @Min(1)
        int age,
        @Schema(description = "Whether the user is male", example = "true")
        @NotNull
        boolean isMale,
        @Schema(description = "User's hair color", example = "Black")
        @NotNull
        HairColor hairColor
) { }
