package ru.gusev.controllers.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.gusev.currency.CurrencyBalanceService;
import ru.gusev.mappers.account.AccountMapper;
import ru.gusev.request.operation.TransferRequest;
import ru.gusev.response.account.AccountResponse;
import ru.gusev.response.account.CurrencyBalanceResponse;
import ru.gusev.services.ClientService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/client/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class ClientAccountController {
    private final ClientService clientAccountService;
    private final AccountMapper accountMapper;
    private final CurrencyBalanceService currencyBalanceService;

    @Operation(summary = "Get current client's accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Only clients can access this endpoint"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<AccountResponse> getMyAccounts(Authentication authentication) {
        return clientAccountService.getMyAccounts(authentication.getName())
                .stream().map(accountMapper::toAccountResponse).toList();
    }

    @Operation(summary = "Get current client's account by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid account identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Account does not belong to the current client"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{accountId}")
    public AccountResponse getMyAccount(
            Authentication authentication,
            @PathVariable UUID accountId
    ) {
        return accountMapper.toAccountResponse(clientAccountService.getMyAccount(authentication.getName(), accountId));
    }

    @Operation(summary = "Get current client's account balance in the requested currency")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Converted balance returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid currency code"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Account does not belong to the current client"),
            @ApiResponse(responseCode = "404", description = "Account or currency not found"),
            @ApiResponse(responseCode = "503", description = "Rates service is unavailable")
    })
    @GetMapping(value = "/{accountId}/balance", params = "currency")
    public CurrencyBalanceResponse getMyBalanceInCurrency(
            Authentication authentication,
            @PathVariable UUID accountId,
            @RequestParam String currency
    ) {
        clientAccountService.getMyAccount(authentication.getName(), accountId);
        return currencyBalanceService.getBalance(accountId, currency);
    }

    @Operation(summary = "Deposit money into current client's account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Money deposited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount or account identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Account does not belong to the current client"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{accountId}/deposit")
    public AccountResponse deposit(
            Authentication authentication,
            @PathVariable UUID accountId,
            @Parameter(description = "Deposit amount", example = "100.00")
            BigDecimal money
    ) {
        return accountMapper.toAccountResponse(clientAccountService.deposit(authentication.getName(), accountId, money));
    }

    @Operation(summary = "Withdraw money from current client's account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Money withdrawn successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount, insufficient funds, or account identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Account does not belong to the current client"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{accountId}/withdraw")
    public AccountResponse withdraw(
            Authentication authentication,
            @PathVariable UUID accountId,
            @Parameter(description = "Withdrawal amount", example = "50.00")
            BigDecimal money
    ) {
        return accountMapper.toAccountResponse(clientAccountService.withdraw(authentication.getName(), accountId, money));
    }

    @Operation(summary = "Transfer money from current client's source account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient funds"),
            @ApiResponse(responseCode = "401", description = "Authentication is required"),
            @ApiResponse(responseCode = "403", description = "Source account does not belong to the current client"),
            @ApiResponse(responseCode = "404", description = "Source or target account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/transfer")
    public void transfer(
            Authentication authentication,
            @RequestBody @Valid TransferRequest request
    ) {
        clientAccountService.transfer(authentication.getName(), request);
    }
}
