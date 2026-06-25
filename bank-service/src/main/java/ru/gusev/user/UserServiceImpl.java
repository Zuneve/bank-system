package ru.gusev.user;

import lombok.RequiredArgsConstructor;
import ru.gusev.user.info.HairColor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(String login, String name, int age, boolean isMale, HairColor hairColor) {
        return userRepository.createUser(login, name, age, isMale, hairColor);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findUserById(UUID id) {
        return Optional.ofNullable(userRepository.findUserById(id));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(userRepository.findByLogin(login));
    }

    @Override
    public void addUserInFriends(UUID userId, UUID friendId) {
        User user1 = userRepository.findUserById(userId);
        User user2 = userRepository.findUserById(friendId);

        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("There is no such id for user1 or user2");
        }

        userRepository.addUserInFriends(userId, friendId);
    }

    @Override
    public void deleteUserFromFriends(UUID userId, UUID oldFriendId) {
        User user1 = userRepository.findUserById(userId);
        User user2 = userRepository.findUserById(oldFriendId);

        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("There is no such id for user1 or user2");
        }

        userRepository.deleteUserFromFriends(userId, oldFriendId);
    }
}
