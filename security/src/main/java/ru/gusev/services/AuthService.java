package ru.gusev.services;

import ru.gusev.auth.AuthUser;
import ru.gusev.request.admin.CreateAdminRequest;
import ru.gusev.request.user.CreateClientRequest;

public interface AuthService {
    AuthUser createAdmin(CreateAdminRequest request);

    AuthUser createClient(CreateClientRequest request);
}
