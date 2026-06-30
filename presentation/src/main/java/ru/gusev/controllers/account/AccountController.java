package ru.gusev.controllers.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.gusev.account.AccountService;
import ru.gusev.mappers.account.AccountMapper;
import ru.gusev.request.account.CreateAccountRequest;
import ru.gusev.response.account.AccountResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @Operation(summary = "Get all accounts owned by a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}")
    public List<AccountResponse> getAccountsById(@PathVariable UUID userId) {
        return accountService.
                getAccountsByUserId(userId)
                .stream()
                .map(accountMapper::toAccountResponse)
                .toList();
    }

    @Operation(summary = "Get all accounts in repository")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts returned successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts()
                .stream()
                .map(accountMapper::toAccountResponse)
                .toList();
    }

    @Operation(summary = "Create an account for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Account owner not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@RequestBody @Valid CreateAccountRequest request) {
        return accountMapper.toAccountResponse(accountService.create(request));
    }

    @Operation(summary = "Get an account balance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid account identifier"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{accountId}/balance")
    public BigDecimal getBalance(@PathVariable UUID accountId) {
        return accountService.getBalanceById(accountId);
    }

    @Operation(summary = "Withdraw money from an account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Money withdrawn successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount or insufficient balance"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{accountId}/withdraw")
    public void withdraw(@PathVariable UUID accountId, @RequestParam BigDecimal amount) {
        accountService.withdraw(accountId, amount);
    }

    @Operation(summary = "Deposit money into an account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Money deposited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{accountId}/deposit")
    public void deposit(@PathVariable UUID accountId, @RequestParam BigDecimal amount) {
        accountService.deposit(accountId, amount);
    }

    @Operation(summary = "Transfer money between accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount or insufficient balance"),
            @ApiResponse(responseCode = "404", description = "Source or target account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/transfer")
    public void transfer(
            @RequestParam UUID sourceAccountId,
            @RequestParam UUID targetAccountId,
            @RequestParam BigDecimal amount) {
        accountService.transfer(sourceAccountId, targetAccountId, amount);
    }
}
