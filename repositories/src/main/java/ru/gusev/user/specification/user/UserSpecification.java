package ru.gusev.user.specification.user;

import org.springframework.data.jpa.domain.Specification;
import ru.gusev.user.User;
import ru.gusev.user.info.HairColor;

public class UserSpecification {
    public static Specification<User> hasHairColor(HairColor hairColor) {
        if (hairColor == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) ->
                cb.equal(root.get("hairColor"), hairColor);
    }

    public static Specification<User> hasGender(Boolean isMale) {
        if (isMale == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) ->
                cb.equal(root.get("isMale"), isMale);
    }
}
