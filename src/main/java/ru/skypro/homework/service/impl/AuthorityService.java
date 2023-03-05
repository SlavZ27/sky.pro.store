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
        tempAuthority.setAuthority(role.name());
        tempAuthority.setUsername(user.getUsername());
        authorityRepository.save(tempAuthority);
    }

    public void delAuthority(User user, Role role) {
        Authority tempAuthority = authorityRepository.findByUsernameAndAuthority(user.getUsername(), "ROLE_" + role.name())
                .orElse(null);
        if (tempAuthority != null) {
            authorityRepository.delete(tempAuthority);
        }
    }

    public List<Authority> getAuthority(User user) {
        return authorityRepository.getAllByUsername(user.getUsername());
    }

//    public boolean checkRoleForUsername(User user, Role role) {
//        return authorityRepository.findByUsernameAndAuthority(user.getUsername(), "ROLE_" + role.name()).isPresent();
//    }
}
