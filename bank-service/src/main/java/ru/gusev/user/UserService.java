package ru.gusev.user;

import ru.gusev.user.info.HairColor;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(String login, String name, int age, boolean isMale, HairColor hairColor);

    void save(User user);

    Optional<User> findUserById(UUID id);

    Optional<User> findByLogin(String login);

    void addUserInFriends(UUID userId, UUID friendId);

    void deleteUserFromFriends(UUID userId, UUID oldFriendId);
}
