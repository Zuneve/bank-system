package ru.gusev.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.gusev.mappers.admin.AdminMapper;
import ru.gusev.mappers.user.AuthUserMapper;
import ru.gusev.request.admin.CreateAdminRequest;
import ru.gusev.request.user.CreateClientRequest;
import ru.gusev.response.admin.CreateAdminResponse;
import ru.gusev.response.user.CreateClientResponse;
import ru.gusev.services.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AdminMapper adminMapper;
    private final AuthUserMapper authUserMapper;

    @Operation(summary = "Create an administrator authentication account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Administrator created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only administrators can create administrators"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAdminResponse createAdmin(@RequestBody @Valid CreateAdminRequest request) {
        return adminMapper.toCreateAdminResponse(authService.createAdmin(request));
    }

    @Operation(summary = "Create a client authentication account with linked banking user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only administrators can create clients"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/clients")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateClientResponse createClient(@RequestBody @Valid CreateClientRequest request) {
        return authUserMapper.toCreateClientResponse(authService.createClient(request));
    }
}
