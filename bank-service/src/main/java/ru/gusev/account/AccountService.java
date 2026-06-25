package ru.gusev.account;

import ru.gusev.user.User;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountService {
    Account create(User user);

    void save(Account account);

    BigDecimal getBalanceById(UUID accountId);

    void withdraw(UUID accountId, BigDecimal amount);

    void deposit(UUID accountId, BigDecimal amount);

    void transfer(UUID sourceAccountId, UUID targetAccountId, BigDecimal amount);
}
