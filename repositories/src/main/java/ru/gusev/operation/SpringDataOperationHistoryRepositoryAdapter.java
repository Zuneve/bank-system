package ru.gusev.operation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.gusev.account.AccountRepository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SpringDataOperationHistoryRepositoryAdapter implements OperationHistoryRepository {
    private final AccountRepository accountRepository;
    private final SpringDataOperationHistoryRepository repository;

    @Override
    public void addOperation(UUID accountId, Operation operation) {
        accountRepository.getAccountById(accountId).ifPresent(account ->
        {
            account.getOperations().add(operation);

            repository.save(operation);
        });
    }

    @Override
    public List<Operation> getOperationsById(UUID accountId) {
        return repository.findByAccount_Id(accountId);
    }

    @Override
    public List<Operation> findAll(Specification<Operation> specification) {
        return repository.findAll(specification);
    }
}
