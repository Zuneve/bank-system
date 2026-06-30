package ru.gusev.controllers.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.gusev.mappers.user.UserMapper;
import ru.gusev.request.user.CreateUserRequest;
import ru.gusev.response.user.UserResponse;
import ru.gusev.user.UserService;
import ru.gusev.user.info.HairColor;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Get a user's friends")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friends returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/friends/{userId}")
    public List<UserResponse> getFriendsByUserId(@PathVariable UUID userId) {
        return userService.getFriendsById(userId)
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Operation(summary = "Get users filtered by hair color and gender")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter value"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<UserResponse> getUsers(
            @RequestParam(required = false)HairColor hairColor,
            @RequestParam(required = false)Boolean isMale) {

        return userService.getUsersByFilters(hairColor, isMale)
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Operation(summary = "Get a user by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable UUID userId) {
        return userMapper.toUserResponse(userService.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    @Operation(summary = "Create a user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody @Valid CreateUserRequest userRequest) {
        return userMapper.toUserResponse(userService.createUser(userRequest));
    }

    @Operation(summary = "Add a user to another user's friends")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid identifiers or friendship request"),
            @ApiResponse(responseCode = "404", description = "User or friend not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable UUID userId, @PathVariable UUID friendId) {
        userService.addUserInFriends(userId, friendId);
    }

    @Operation(summary = "Remove a user from another user's friends")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Friend removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid identifiers"),
            @ApiResponse(responseCode = "404", description = "User or friend not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{userId}/friends/{oldFriendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable UUID userId, @PathVariable UUID oldFriendId) {
        userService.deleteUserFromFriends(userId, oldFriendId);
    }
}
