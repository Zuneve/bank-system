package ru.gusev.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gusev.auth.AuthUser;
import ru.gusev.auth.authuser_info.RoleType;
import ru.gusev.repositories.AuthUserRepository;
import ru.gusev.request.admin.CreateAdminRequest;
import ru.gusev.request.user.CreateClientRequest;
import ru.gusev.request.user.CreateUserRequest;
import ru.gusev.user.User;
import ru.gusev.user.UserService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService
{
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;


    @Override
    @Transactional
    public AuthUser createAdmin(CreateAdminRequest request) {
        if (authUserRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        AuthUser admin = new AuthUser();

        admin.setId(UUID.randomUUID());
        admin.setUsername(request.username());
        admin.setPasswordHash(passwordEncoder.encode(request.password()));
        admin.setRoleType(RoleType.ADMIN);
        admin.setClient(null);

        return authUserRepository.save(admin);
    }

    @Override
    @Transactional
    public AuthUser createClient(CreateClientRequest request) {
        if (authUserRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User client = userService.createUser(new CreateUserRequest(
                request.login(),
                request.name(),
                request.age(),
                request.male(),
                request.hairColor()
        ));

        AuthUser authUser = new AuthUser();
        authUser.setId(UUID.randomUUID());
        authUser.setUsername(request.username());
        authUser.setPasswordHash(passwordEncoder.encode(request.password()));
        authUser.setRoleType(RoleType.CLIENT);
        authUser.setClient(client);

        return authUserRepository.save(authUser);
    }
}
