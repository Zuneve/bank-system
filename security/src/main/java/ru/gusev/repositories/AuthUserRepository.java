package ru.gusev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gusev.auth.AuthUser;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {
    Optional<AuthUser> findByUsername(String username);
}
