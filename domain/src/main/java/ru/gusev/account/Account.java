package ru.gusev.account;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.gusev.operation.Operation;
import ru.gusev.user.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    private UUID id;

    @Setter
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Operation> operations;

    public Account(User user) {
        this.id = UUID.randomUUID();
        this.balance = BigDecimal.ZERO;
        owner = user;
        operations = new ArrayList<>();
    }
}
