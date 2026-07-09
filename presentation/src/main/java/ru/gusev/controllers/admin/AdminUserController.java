package ru.gusev.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.gusev.account.AccountService;
import ru.gusev.exception.UserNotFoundException;
import ru.gusev.mappers.account.AccountMapper;
import ru.gusev.mappers.user.UserMapper;
import ru.gusev.request.user.CreateUserRequest;
import ru.gusev.response.account.AccountResponse;
import ru.gusev.response.user.UserResponse;
import ru.gusev.user.User;
import ru.gusev.user.UserService;
import ru.gusev.user.info.HairColor;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;
    private final AccountService accountService;
    private final UserMapper userMapper;
    private final AccountMapper accountMapper;

    @Operation(summary = "Create a banking user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only administrators can create users"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody @Valid CreateUserRequest request) {
        return userMapper.toUserResponse(userService.createUser(request));
    }

    @Operation(summary = "Get users with optional filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter value"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only administrators can read all users"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<UserResponse> getUsers(
            @RequestParam(required = false) HairColor hairColor,
            @RequestParam(required = false) Boolean male
    ) {
        return userService.getUsersByFilters(hairColor, male)
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Operation(summary = "Get a user by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only administrators can read any user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable UUID userId) {
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return userMapper.toUserResponse(user);
    }

    @Operation(summary = "Get accounts owned by a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only administrators can read any user's accounts"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}/accounts")
    public List<AccountResponse> getAccountsByUserId(@PathVariable UUID userId) {
        return accountService.getAccountsByUserId(userId)
                .stream()
                .map(accountMapper::toAccountResponse)
                .toList();
    }
}
