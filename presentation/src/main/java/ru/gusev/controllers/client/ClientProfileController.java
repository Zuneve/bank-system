package ru.gusev.controllers.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.gusev.mappers.user.UserMapper;
import ru.gusev.response.user.UserResponse;
import ru.gusev.services.ClientService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/client/me")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class ClientProfileController {
    private final ClientService clientService;
    private final UserMapper userMapper;

    @Operation(summary = "Get current client profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only clients can access this endpoint"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public UserResponse getMe(Authentication authentication) {
        return userMapper.toUserResponse(clientService.getMe(authentication.getName()));
    }

    @Operation(summary = "Get current client's friends")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friends returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only clients can access this endpoint"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/friends")
    public List<UserResponse> getMyFriends(Authentication authentication) {
        return clientService.getMyFriends(authentication.getName())
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Operation(summary = "Add a user to current client's friends")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid friend identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only clients can access this endpoint"),
            @ApiResponse(responseCode = "404", description = "Friend not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/friends/{friendId}")
    public void addFriend(
            Authentication authentication,
            @PathVariable UUID friendId
    ) {
        clientService.addFriend(authentication.getName(), friendId);
    }

    @Operation(summary = "Remove a user from current client's friends")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Friend removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid friend identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only clients can access this endpoint"),
            @ApiResponse(responseCode = "404", description = "Friend not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(
            Authentication authentication,
            @PathVariable UUID friendId
    ) {
        clientService.deleteFriend(authentication.getName(), friendId);
    }
}
