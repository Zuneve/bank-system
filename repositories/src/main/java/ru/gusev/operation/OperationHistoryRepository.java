package ru.gusev.operation;


import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface OperationHistoryRepository {
    void addOperation(UUID accountId, Operation operation);

    List<Operation> getOperationsById(UUID accountId);

    List<Operation> findAll(Specification<Operation> specification);
}
