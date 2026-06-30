package ru.gusev.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SpringDataUserRepositoryAdapter implements UserRepository {
    private final SpringDataUserRepository repository;

    @Override
    public User createUser(User user) {
        return repository.save(user);
    }

    @Override
    public void save(User user) {
        repository.save(user);
    }

    @Override
    public User findUserById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public User findByLogin(String login) {
        return repository.findByLogin(login).orElse(null);
    }

    @Override
    public List<User> findAll(Specification<User> specification) {
        return repository.findAll(specification);
    }

    @Override
    public void addUserInFriends(UUID userId, UUID friendId) {
        repository.findById(userId).ifPresent(user ->
        {
            repository.findById(friendId).ifPresent(oldFriend ->
            {
                user.getFriends().add(oldFriend);
                repository.save(user);
            });
        });
    }

    @Override
    public void deleteUserFromFriends(UUID userId, UUID oldFriendId) {
        repository.findById(userId).ifPresent(user ->
        {
            repository.findById(oldFriendId).ifPresent(oldFriend ->
            {
                user.getFriends().remove(oldFriend);
                repository.save(user);
            });
        });
    }
}
