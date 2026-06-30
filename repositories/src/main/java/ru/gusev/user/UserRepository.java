package ru.gusev.user;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    User createUser(User user);

    void save(User user);

    User findUserById(UUID id);

    User findByLogin(String login);

    List<User> findAll(Specification<User> specification);

    void addUserInFriends(UUID userId, UUID friendId);

    void deleteUserFromFriends(UUID userId, UUID oldFriendId);
}
