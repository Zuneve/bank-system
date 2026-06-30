package ru.gusev.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gusev.operation.Operation;
import ru.gusev.operation.OperationType;
import ru.gusev.operation.OperationHistoryRepository;
import ru.gusev.request.account.CreateAccountRequest;
import ru.gusev.user.User;
import ru.gusev.user.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private static final BigDecimal FRIEND_TRANSFER_COMMISSION_RATE = new BigDecimal("0.03");
    private static final BigDecimal DEFAULT_TRANSFER_COMMISSION_RATE = new BigDecimal("0.10");

    private final AccountRepository accountRepository;
    private final OperationHistoryRepository operationHistoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Account create(CreateAccountRequest request) {
        User owner = userRepository.findUserById(request.userId());
        if (owner == null) {
            throw new IllegalArgumentException("UserId not found");
        }

        return accountRepository.create(owner);
    }

    @Override
    @Transactional
    public void save(Account account) {
        accountRepository.save(account);
    }

    @Override
    public List<Account> getAccountsByUserId(UUID userId) {
        return accountRepository.getAccountsByUserId(userId);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.getAllAccounts();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalanceById(UUID accountId) {
        if (accountRepository.getAccountById(accountId).isEmpty()) {
            throw new IllegalArgumentException("AccountId not found");
        }

        return accountRepository.getBalanceById(accountId);
    }

    @Override
    @Transactional
    public void withdraw(UUID accountId, BigDecimal amount) {
        requirePositiveAmount(amount);

        Optional<Account> accountOptional = accountRepository.getAccountById(accountId);
        if (accountOptional.isEmpty()) {
            throw new IllegalArgumentException("AccountId not found");
        }

        Account account = accountOptional.get();

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Amount of withdraw is greater than account balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        Operation operation = new Operation(OperationType.WITHDRAW, amount, account);

        accountRepository.save(account);
        operationHistoryRepository.addOperation(accountId, operation);
    }

    @Override
    @Transactional
    public void deposit(UUID accountId, BigDecimal amount) {
        requirePositiveAmount(amount);

        Optional<Account> accountOptional = accountRepository.getAccountById(accountId);
        if (accountOptional.isEmpty()) {
            throw new IllegalArgumentException("AccountId not found");
        }

        Account account = accountOptional.get();

        account.setBalance(account.getBalance().add(amount));
        Operation operation = new Operation(OperationType.DEPOSIT, amount, account);

        accountRepository.save(account);
        operationHistoryRepository.addOperation(accountId, operation);
    }

    @Override
    @Transactional
    public void transfer(UUID sourceAccountId, UUID targetAccountId, BigDecimal amount) {
        requirePositiveAmount(amount);

        Optional<Account> firstAccountOptional = accountRepository.getAccountById(sourceAccountId);
        Optional<Account> secondAccountOptional = accountRepository.getAccountById(targetAccountId);
        if (firstAccountOptional.isEmpty()  || secondAccountOptional.isEmpty()) {
            throw new IllegalArgumentException("AccountId not found");
        }

        Account firstAccount = firstAccountOptional.get();
        Account secondAccount = secondAccountOptional.get();

        boolean isSameOwner = firstAccount.getOwner().getId().equals(secondAccount.getOwner().getId());
        boolean isFriend = firstAccount.getOwner().getFriends().contains(secondAccount.getOwner());
        BigDecimal commission = calculateCommission(amount, isSameOwner, isFriend);
        BigDecimal totalWithdrawAmount = amount.add(commission);

        if (firstAccount.getBalance().compareTo(totalWithdrawAmount) < 0) {
            throw new IllegalArgumentException("Amount of transfer is greater than account balance");
        }

        firstAccount.setBalance(firstAccount.getBalance().subtract(totalWithdrawAmount));
        secondAccount.setBalance(secondAccount.getBalance().add(amount));

        Operation transferOut = new Operation(OperationType.TRANSFER_OUT, totalWithdrawAmount, firstAccount);
        Operation transferIn = new Operation(OperationType.TRANSFER_IN, amount, secondAccount);

        accountRepository.save(firstAccount);
        accountRepository.save(secondAccount);
        operationHistoryRepository.addOperation(sourceAccountId, transferOut);
        operationHistoryRepository.addOperation(targetAccountId, transferIn);
    }

    private BigDecimal calculateCommission(BigDecimal amount, boolean isSameOwner, boolean isFriend) {
        if (isSameOwner) {
            return BigDecimal.ZERO;
        }

        if (isFriend) {
            return amount.multiply(FRIEND_TRANSFER_COMMISSION_RATE);
        }

        return amount.multiply(DEFAULT_TRANSFER_COMMISSION_RATE);
    }

    private void requirePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
