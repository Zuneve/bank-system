package ru.gusev.account;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import ru.gusev.user.User;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JpaAccountRepository implements AccountRepository{
    private final EntityManagerFactory entityManagerFactory;

    @Override
    public Account create(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            Account account = new Account(user);
            entityManager.persist(account);

            entityManager.getTransaction().commit();

            return account;
        } catch (Exception exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw exception;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void save(Account account) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            entityManager.merge(account);

            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw exception;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Optional<Account> getAccountById(UUID accountId) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(Account.class, accountId));
        }
    }

    @Override
    public BigDecimal getBalanceById(UUID accountId) {

        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.find(Account.class, accountId).getBalance();
        }
    }
}
