package ru.gusev.operation;

import ru.gusev.account.Account;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "operations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Operation {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private OperationType type;

    private BigDecimal amount;

    private LocalDateTime createdAt;

    @ManyToOne
    private Account account;

    public Operation(OperationType type, BigDecimal amount, Account account) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
        this.account = account;
    }
}
