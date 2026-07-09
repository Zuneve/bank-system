package ru.gusev.admin_initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.gusev.auth.AuthUser;
import ru.gusev.auth.authuser_info.RoleType;
import ru.gusev.repositories.AuthUserRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final AuthUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (repository.findByUsername("admin").isPresent()) {
            return;
        }

        AuthUser admin = new AuthUser();
        admin.setId(UUID.randomUUID());
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin"));
        admin.setRoleType(RoleType.ADMIN);
        admin.setClient(null);

        repository.save(admin);
    }
}
