package ru.skypro.homework.service;

import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.User;

public interface AuthorityService {

    Authority addAuthority(User user, Role role);
}
