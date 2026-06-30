package ru.gusev.user;

import ru.gusev.request.user.CreateUserRequest;
import ru.gusev.user.info.HairColor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(CreateUserRequest request);

    void save(User user);

    List<User> getFriendsById(UUID id);

    List<User> getUsersByFilters(HairColor hairColor, Boolean isMale);

    Optional<User> findUserById(UUID id);

    Optional<User> findByLogin(String login);

    void addUserInFriends(UUID userId, UUID friendId);

    void deleteUserFromFriends(UUID userId, UUID oldFriendId);
}
