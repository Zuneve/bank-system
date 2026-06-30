package ru.gusev.mappers.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gusev.response.user.UserResponse;
import ru.gusev.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "male", target = "isMale")
    UserResponse toUserResponse(User user);
}
