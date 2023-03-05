package ru.skypro.homework.component;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.skypro.homework.repository.UsersRepository;

@Component("userSecurity")
public class UserSecurity {

    private final UsersRepository usersRepository;

    public UserSecurity(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public boolean isAdsAuthor(Integer adsId) {
        return usersRepository.isAdsAuthor(adsId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public boolean isCommentAuthor(Integer commentId) {
        return usersRepository.isCommentAuthor(
                commentId, SecurityContextHolder.getContext().getAuthentication().getName()
        );

    }
}
