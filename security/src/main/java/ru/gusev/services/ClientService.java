package ru.gusev.services;

import org.springframework.security.access.AccessDeniedException;
import ru.gusev.account.Account;
import ru.gusev.request.operation.TransferRequest;
import ru.gusev.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ClientService {
    User getMe(String username) throws AccessDeniedException;

    List<User> getMyFriends(String username);

    void addFriend(String username, UUID friendId);

    void deleteFriend(String username, UUID friendId);

    List<Account> getMyAccounts(String username);

    Account getMyAccount(String username, UUID accountId);

    Account deposit(String username, UUID accountId, BigDecimal amount);

    Account withdraw(String username, UUID accountId, BigDecimal amount);

    void transfer(String username, TransferRequest request);
}
