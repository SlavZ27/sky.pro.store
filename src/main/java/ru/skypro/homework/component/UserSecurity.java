package ru.skypro.homework.component;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.service.impl.AdsServiceImpl;

@Component("userSecurity")
public class UserSecurity {

    private final UsersRepository usersRepository;
    private final AdsRepository adsRepository;
    private final CommentRepository commentRepository;

    public UserSecurity(AdsServiceImpl adsService, UsersRepository usersRepository, AdsRepository adsRepository, CommentRepository commentRepository) {
        this.usersRepository = usersRepository;
        this.adsRepository = adsRepository;
        this.commentRepository = commentRepository;
    }

    public boolean isAdsAuthor(Integer adsId) {
        Ads ads = adsRepository.findById(adsId).orElseThrow(()-> new AdsNotFoundException("Not found ads with id: " + adsId));
        User user = usersRepository.findById(ads.getAuthor().getId()).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName().equals(user.getUsername());
    }

    public boolean isCommentAuthor(Integer commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new CommentNotFoundException("Not found ads with id: " + commentId));
        User user = usersRepository.findById(comment.getAuthor().getId()).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName().equals(user.getUsername());
    }
}
