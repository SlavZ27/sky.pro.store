package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.AuthorityRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public void addAuthority(User user, Role role) {
        Authority tempAuthority = new Authority();
        tempAuthority.setAuthority(role.getRole());
        tempAuthority.setUsername(user.getUsername());
        Authority newAuthority = authorityRepository.save(tempAuthority);
        log.info("New Authority has been created with name - {}", newAuthority.getAuthority());
    }

    public void delAuthority(User user, Role role) {
        authorityRepository.findByUsernameAndAuthority(user.getUsername(), role.getRole())
                .ifPresent(authority -> {
                    authorityRepository.delete(authority);
                    log.info("Authority with ID: {} has been deleted", authority.getId());
                });
    }

    public List<Authority> getAuthority(User user) {
        return authorityRepository.getAllByUsername(user.getUsername());
    }
}
