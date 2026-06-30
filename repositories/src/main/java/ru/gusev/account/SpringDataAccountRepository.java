package ru.gusev.account;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface SpringDataAccountRepository extends JpaRepository<Account, UUID> {
    @Query("SELECT a.balance FROM Account a WHERE a.id = :accountId")
    BigDecimal findBalanceById(@Param("accountId") UUID accountId);

    @EntityGraph(attributePaths = "owner")
    List<Account> findByOwner_Id(UUID userId);

    @Override
    @EntityGraph(attributePaths = "owner")
    List<Account> findAll();
}
