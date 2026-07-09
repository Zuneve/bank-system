package ru.gusev.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gusev.account.Account;
import ru.gusev.account.AccountService;
import ru.gusev.auth.AuthUser;
import ru.gusev.auth.authuser_info.RoleType;
import ru.gusev.repositories.AuthUserRepository;
import ru.gusev.request.operation.TransferRequest;
import ru.gusev.user.User;
import ru.gusev.user.UserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final AuthUserRepository authUserRepository;
    private final UserService userService;
    private final AccountService accountService;

    @Override
    @Transactional(readOnly = true)
    public User getMe(String username) throws AccessDeniedException {
        return getClient(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getMyFriends(String username) {
        try {
            return getClient(username).getFriends();
        } catch (AccessDeniedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void addFriend(String username, UUID friendId) {
        User client = getClient(username);
        userService.addUserInFriends(client.getId(), friendId);
    }

    @Override
    @Transactional
    public void deleteFriend(String username, UUID friendId) {
        User client = getClient(username);
        userService.deleteUserFromFriends(client.getId(), friendId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getMyAccounts(String username) {
        User client = getClient(username);
        return accountService.getAccountsByUserId(client.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Account getMyAccount(String username, UUID accountId) {
        User client = getClient(username);
        Account account = accountService.findById(accountId);

        checkOwner(account, client);

        return account;
    }

    @Override
    @Transactional
    public Account deposit(String username, UUID accountId, BigDecimal amount) {
        User client = getClient(username);
        Account account = accountService.findById(accountId);

        checkOwner(account, client);

        accountService.deposit(accountId, amount);

        return accountService.findById(accountId);
    }

    @Override
    @Transactional
    public Account withdraw(String username, UUID accountId, BigDecimal amount) {
        User client = getClient(username);
        Account account = accountService.findById(accountId);

        checkOwner(account, client);

        accountService.withdraw(accountId, amount);

        return accountService.findById(accountId);
    }

    @Override
    @Transactional
    public void transfer(String username, TransferRequest request) {
        User client = getClient(username);
        Account sourceAccount = accountService.findById(request.sourceAccountId());

        checkOwner(sourceAccount, client);

        accountService.transfer(
                request.sourceAccountId(),
                request.targetAccountId(),
                request.amount()
        );
    }

    private User getClient(String username) throws AccessDeniedException {
        AuthUser authUser = authUserRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found"));

        if (authUser.getRoleType() != RoleType.CLIENT || authUser.getClient() == null) {
            throw new AccessDeniedException("Access denied");
        }

        return authUser.getClient();
    }

    private void checkOwner(Account account, User client) {
        if (!account.getOwner().getId().equals(client.getId())) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
