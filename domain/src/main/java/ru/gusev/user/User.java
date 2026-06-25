package ru.gusev.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.gusev.user.info.HairColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    private UUID id;

    private String login;
    private String name;
    private int age;
    private boolean isMale;

    @Enumerated(EnumType.STRING)
    private HairColor hairColor;

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends;

    public User(String login, String name, int age, boolean isMale, HairColor hairColor) {
        id = UUID.randomUUID();
        this.login = login;
        this.name = name;
        this.age = age;
        this.isMale = isMale;
        this.hairColor = hairColor;
        friends = new ArrayList<>();
    }
}
