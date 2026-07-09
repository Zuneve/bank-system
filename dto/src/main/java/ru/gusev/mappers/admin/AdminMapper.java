package ru.gusev.mappers.admin;

import org.mapstruct.Mapper;
import ru.gusev.auth.AuthUser;
import ru.gusev.response.admin.CreateAdminResponse;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    CreateAdminResponse toCreateAdminResponse(AuthUser authUser);
}
