package ru.gusev.operation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gusev.operation.specification.operation.OperationSpecification;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {
    public final OperationHistoryRepository operationHistoryRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Operation> getOperationsByFilters(UUID accountId, OperationType operationType) {
        Specification<Operation> specification = OperationSpecification.hasAccountId(accountId)
                .and(OperationSpecification.hasType(operationType));

        return operationHistoryRepository.findAll(specification);
    }

}
