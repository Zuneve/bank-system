package ru.gusev.operation;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SpringDataOperationHistoryRepository extends
        JpaRepository<Operation, UUID>,
        JpaSpecificationExecutor<Operation> {
    @EntityGraph(attributePaths = "account")
    List<Operation> findByAccount_Id(UUID accountId);

    @Override
    @EntityGraph(attributePaths = "account")
    List<Operation> findAll(Specification<Operation> specification);
}
