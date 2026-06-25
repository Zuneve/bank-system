package ru.gusev.operation;


import java.util.List;
import java.util.UUID;

public interface OperationHistoryRepository {
    void addOperation(UUID accountId, Operation operation);

    List<Operation> getOperationsById(UUID accountId);
}
