package ru.gusev.mappers.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gusev.auth.AuthUser;
import ru.gusev.response.user.CreateClientResponse;

@Mapper(componentModel = "spring")
public interface AuthUserMapper {
    @Mapping(source = "client.id", target = "userId")
    CreateClientResponse toCreateClientResponse(AuthUser authUser);
}
