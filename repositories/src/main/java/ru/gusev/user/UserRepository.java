package ru.gusev.user;

import ru.gusev.user.info.HairColor;

import java.util.UUID;

public interface UserRepository {
    User createUser(String login, String name, int age, boolean isMale, HairColor hairColor);

    void save(User user);

    User findUserById(UUID id);

    User findByLogin(String login);

    void addUserInFriends(UUID userId, UUID friendId);

    void deleteUserFromFriends(UUID userId, UUID oldFriendId);
}
