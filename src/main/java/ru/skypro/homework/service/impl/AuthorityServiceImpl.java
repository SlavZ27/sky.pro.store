package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.AuthorityRepository;
import ru.skypro.homework.service.AuthorityService;

/**
 * Provides implementations of AuthorityService methods
 *  @see AuthorityService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    /**
     * Create a new authority and save it to repository
     *
     * @param user the user
     * @param role the role
     * @return {@link Authority}
     */
    @Override
    public Authority addAuthority(User user, Role role) {
        Authority tempAuthority = new Authority();
        tempAuthority.setAuthority(role.getRole());
        tempAuthority.setUsername(user.getUsername());
        Authority newAuthority = authorityRepository.save(tempAuthority);
        log.info("New Authority {} has been created with user {}",
                newAuthority.getAuthority(), newAuthority.getUsername());
        return newAuthority;
    }
}
