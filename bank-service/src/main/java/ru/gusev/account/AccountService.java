package ru.gusev.account;

import ru.gusev.request.account.CreateAccountRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountService {
    Account create(CreateAccountRequest request);

    void save(Account account);

    List<Account> getAccountsByUserId(UUID userId);

    List<Account> getAllAccounts();

    BigDecimal getBalanceById(UUID accountId);

    void withdraw(UUID accountId, BigDecimal amount);

    void deposit(UUID accountId, BigDecimal amount);

    void transfer(UUID sourceAccountId, UUID targetAccountId, BigDecimal amount);
}
