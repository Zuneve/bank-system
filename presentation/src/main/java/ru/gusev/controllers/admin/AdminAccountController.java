package ru.gusev.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gusev.account.Account;
import ru.gusev.account.AccountService;
import ru.gusev.mappers.account.AccountMapper;
import ru.gusev.mappers.operation.OperationMapper;
import ru.gusev.operation.OperationService;
import ru.gusev.response.account.AccountResponse;
import ru.gusev.response.account.AccountWithOperationsResponse;
import ru.gusev.response.operation.OperationResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final OperationService operationService;
    private final OperationMapper operationMapper;

    @Operation(summary = "Get all accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only administrators can read all accounts"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts()
                .stream()
                .map(accountMapper::toAccountResponse)
                .toList();
    }

    @Operation(summary = "Get an account with operation history")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid account identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only administrators can read any account"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{accountId}")
    public AccountWithOperationsResponse getAccountWithOperations(@PathVariable UUID accountId) {
        Account account = accountService.findById(accountId);

        return accountMapper.toResponseWithOperations(account);
    }

    @Operation(summary = "Get operations by account identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operations returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid account identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only administrators can read account operations"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{accountId}/operations")
    public List<OperationResponse> getOperationsByAccountId(@PathVariable UUID accountId) {
        return operationService.getOperationsByFilters(accountId, null)
                .stream()
                .map(operationMapper::toOperationResponse)
                .toList();
    }
}
