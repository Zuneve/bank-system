package ru.gusev.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.gusev.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SpringDataAccountRepositoryAdapter implements AccountRepository {
    private final SpringDataAccountRepository repository;

    @Override
    public Account create(User user) {
        Account account = new Account(user);
        return repository.save(account);
    }

    @Override
    public void save(Account account) {
        repository.save(account);
    }

    @Override
    public Optional<Account> getAccountById(UUID accountId) {
        return repository.findById(accountId);
    }

    @Override
    public List<Account> getAccountsByUserId(UUID userId) {
        return repository.findByOwner_Id(userId);
    }

    @Override
    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    @Override
    public BigDecimal getBalanceById(UUID accountId) {
        return repository.findBalanceById(accountId);
    }
}
