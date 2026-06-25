package ru.gusev.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import ru.gusev.user.info.HairColor;

import java.util.UUID;

@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Override
    public User createUser(String login, String name, int age, boolean isMale, HairColor hairColor) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            User user = new User(login, name, age, isMale, hairColor);

            entityManager.persist(user);

            entityManager.getTransaction().commit();

            return user;
        } catch (Exception exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw exception;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void save(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            entityManager.merge(user);

            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw exception;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public User findUserById(UUID id) {

        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.find(User.class, id);
        }
    }

    @Override
    public User findByLogin(String login) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager
                    .createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login)
                    .getSingleResult();
        }
    }

    @Override
    public void addUserInFriends(UUID userId, UUID friendId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            User user = entityManager.find(User.class, userId);
            User friend = entityManager.find(User.class, friendId);

            user.getFriends().add(friend);

            entityManager.merge(user);

            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw exception;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void deleteUserFromFriends(UUID userId, UUID oldFriendId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();

            User user = entityManager.find(User.class, userId);
            User oldFriend = entityManager.find(User.class, oldFriendId);

            user.getFriends().remove(oldFriend);

            entityManager.merge(user);

            entityManager.getTransaction().commit();

        } catch (Exception exception) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw exception;
        } finally {
            entityManager.close();
        }
    }
}
