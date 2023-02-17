package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl {

    private final CommentRepository commentRepository;
    private final AdsRepository adsRepository;

    public Comment addCommentsToAds(Integer adsId, Comment comment) {
        Comment newComment;
        Ads ads = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(adsId);
        });
        if (comment != null) {
            newComment = comment;
            newComment.setAds(ads);
        } else {
            throw new IllegalArgumentException();
        }

        return commentRepository.save(newComment);
    }

    public Comment updateCommentsForAds(CommentDto commentDto, Integer adsId, Integer commentId) {
        Ads ads = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(adsId);
        });
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("There is not comment with id = " + commentId);
            return new CommentNotFoundException(commentId);
        });

        comment.setAds(ads);
        comment.setAuthor(ads.getAuthor());
        comment.setText(commentDto.getText());

        comment.setDateTime(LocalDateTime.parse(commentDto.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        return commentRepository.save(comment);
    }

    public List<Comment> listComment(Integer adsId) {
        return commentRepository.findAllByIdAds(adsId);
    }

    public void removeComment(Comment comment) {
        commentRepository.delete(comment);
    }

    public void removeAllCommentsOfAds(Integer idAds) {
        List<Comment> commentList = getAllByIdAds(idAds);
        for (Comment comment : commentList) {
            removeComment(comment);
        }
    }

    public List<Comment> getAllByIdAds(Integer idAds) {
        return commentRepository.findAllByIdAds(idAds);
    }

    public Comment getCommentOfAds(Integer adsId, Integer commentId) {
        return commentRepository.findAllByIdAndAdsId(adsId, commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
    }

}
