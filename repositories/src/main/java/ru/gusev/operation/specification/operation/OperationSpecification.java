package ru.gusev.operation.specification.operation;

import org.springframework.data.jpa.domain.Specification;
import ru.gusev.operation.Operation;
import ru.gusev.operation.OperationType;

import java.util.UUID;

public class OperationSpecification {
    public static Specification<Operation> hasAccountId(UUID accountId) {
        if (accountId == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) ->
                cb.equal(root.get("account").get("id"), accountId);
    }

    public static Specification<Operation> hasType(OperationType type) {
        if (type == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) ->
                cb.equal(root.get("type"), type);
    }
}
