package ru.gusev.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.gusev.auth.authuser_info.RoleType;
import ru.gusev.user.User;

import java.util.UUID;

@Entity
@Getter
@Setter
public class AuthUser {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @OneToOne
    private User client;  // for admin = null
}
