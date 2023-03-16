package ru.skypro.homework.component;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.skypro.homework.repository.UsersRepository;


/**
 * Provides methods for checking the validity of objects to the user
 */
@Component
public class UserSecurity {

    private final UsersRepository usersRepository;

    /**
     * Instantiates a new User security.
     *
     * @param usersRepository the users repository
     */
    public UserSecurity(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * Is ads author boolean.
     *
     * @param adsId the ads id
     * @return the boolean
     */
    public boolean isAdsAuthor(Integer adsId) {
        return usersRepository.isAdsAuthor(adsId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /**
     * Is comment author boolean.
     *
     * @param commentId the comment id
     * @return the boolean
     */
    public boolean isCommentAuthor(Integer commentId) {
        return usersRepository.isCommentAuthor(
                commentId, SecurityContextHolder.getContext().getAuthentication().getName()
        );

    }
}
