package ru.gusev.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gusev.user.specification.user.UserSpecification;
import ru.gusev.request.user.CreateUserRequest;
import ru.gusev.user.info.HairColor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(CreateUserRequest request) {
        User user = new User(
                request.login(),
                request.name(),
                request.age(),
                request.isMale(),
                request.hairColor()
        );

        return userRepository.createUser(user);
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> getFriendsById(UUID id) {
        return findUserById(id)
                .map(User::getFriends)
                .orElseThrow(() -> new EntityNotFoundException("UserId not found"));
    }

    @Override
    public List<User> getUsersByFilters(HairColor hairColor, Boolean isMale) {
        Specification<User> specification = UserSpecification.hasHairColor(hairColor)
                .and(UserSpecification.hasGender(isMale));

        return userRepository.findAll(specification);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(UUID id) {
        return Optional.ofNullable(userRepository.findUserById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(userRepository.findByLogin(login));
    }

    @Override
    @Transactional
    public void addUserInFriends(UUID userId, UUID friendId) {
        User user1 = userRepository.findUserById(userId);
        User user2 = userRepository.findUserById(friendId);

        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("There is no such id for user1 or user2");
        }

        userRepository.addUserInFriends(userId, friendId);
    }

    @Override
    @Transactional
    public void deleteUserFromFriends(UUID userId, UUID oldFriendId) {
        User user1 = userRepository.findUserById(userId);
        User user2 = userRepository.findUserById(oldFriendId);

        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("There is no such id for user1 or user2");
        }

        userRepository.deleteUserFromFriends(userId, oldFriendId);
    }
}
