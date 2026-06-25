package ru.gusev.operation;

import ru.gusev.account.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class JpaOperationHistoryRepository implements OperationHistoryRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Override
    public void addOperation(UUID accountId, Operation operation) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            Account account = entityManager.find(Account.class, accountId);
            account.getOperations().add(operation);
            entityManager.merge(operation);

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
    public List<Operation> getOperationsById(UUID accountId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Account account = entityManager.find(Account.class, accountId);
        return account.getOperations();
    }
}
