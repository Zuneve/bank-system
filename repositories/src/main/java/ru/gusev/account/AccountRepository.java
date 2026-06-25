package ru.gusev.account;

import ru.gusev.user.User;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Account create(User user);

    void save(Account account);

    Optional<Account> getAccountById(UUID accountId);

    BigDecimal getBalanceById(UUID accountId);
}
