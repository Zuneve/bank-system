package ru.gusev.operation;

import java.util.List;
import java.util.UUID;

public interface OperationService {
    List<Operation> getOperationsByFilters(UUID accountId, OperationType operationType);
}
