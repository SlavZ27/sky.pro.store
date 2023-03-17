package ru.skypro.homework.service;

import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.User;

/**
 * Provides method for create new Authority.
 */
public interface AuthorityService {

    /**
     * Add authority.
     *
     * @param user the user
     * @param role the role
     * @return {@link Authority}
     */
    Authority addAuthority(User user, Role role);
}
