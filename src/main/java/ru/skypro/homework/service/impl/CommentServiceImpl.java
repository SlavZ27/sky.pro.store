package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This class processes commands related to create comments in ads allowing users to create, update, get, delete comments.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl {

    private final CommentRepository commentRepository;

    /**
     * This method, used by method repository, allows you to create a new comment.
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentRepository#save(Object)}
     *
     * @param ads     is not null
     * @param comment is npt null
     * @return newComment
     * @throws AdsNotFoundException     - if passed non- existent id
     * @throws IllegalArgumentException if passed non- existent parameters
     */
    public Comment addCommentsToAds(Ads ads, Comment comment, User author) {
        if (comment == null) {
            throw new IllegalArgumentException();
        }
        comment.setAds(ads);
        comment.setAuthor(author);
        comment.setDateTime(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    /**
     * This method, used method repository, allows update comment.
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentRepository#findById(Object)}
     * Uses {@link CommentRepository#save(Object)}
     *
     * @param commentDto is not null
     * @param ads        is not null
     * @param commentId  is not null
     * @return Comment
     * @throws AdsNotFoundException     if passed non id ads
     * @throws CommentNotFoundException if passed non id comment
     */
    public Comment updateCommentsForAds(CommentDto commentDto, Ads ads, Integer commentId) {
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

    /**
     * This method, used method repository, allows get all comment by id to ads.
     * Uses {@link CommentRepository#findAllByIdAds(Integer)}
     *
     * @param idAds is not null
     * @return Comment by id
     */
    public List<Comment> getAllByIdAds(Integer idAds) {
        return commentRepository.findAllByIdAds(idAds);
    }

    public Integer getCountByIdAds(Integer idAds) {
        return commentRepository.getCountAllByAdsId(idAds);
    }

    /**
     * This method, used method repository, allows get all comment by id to ads on date time
     * Uses {@link CommentRepository#findAllByIdAdsAndSortDateTime(Integer)}
     *
     * @param adsId is not null
     * @return Comment by id
     */
    public List<Comment> getAllByIdAdsAndSortDateTime(Integer adsId) {
        return commentRepository.findAllByIdAdsAndSortDateTime(adsId);
    }

    /**
     * This method, used method repository, allows get comment by id ads and id comments
     * Uses {@link CommentRepository#findAllByIdAndAdsId(Integer, Integer)}
     *
     * @param adsId     is not null
     * @param commentId is not null
     * @return Comments
     * @throws CommentNotFoundException if passed non id comment
     */
    public Comment getCommentOfAds(Integer adsId, Integer commentId) {
        return commentRepository.findAllByIdAndAdsId(adsId, commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    /**
     * This method, used method repository, allows del comment
     * Uses {@link CommentRepository#delete(Object)}
     *
     * @param comment is not null
     */
    public void removeComment(Comment comment) {
        commentRepository.delete(comment);
    }

    /**
     * This method, used method repository, allows del comment to idAds
     * Uses {@link CommentRepository#deleteAllByAdsId(Integer)}
     *
     * @param idAds is not null
     */
    public void removeAllCommentsOfAds(Integer idAds) {
        commentRepository.deleteAllByAdsId(idAds);
    }

    /**
     * This method, used method repository, allows del comment to id Ads and id comment
     * Uses {@link CommentRepository#findAllByIdAndAdsId(Integer, Integer)}
     *
     * @param adPk      is not null
     * @param commentId is not null
     * @throws CommentNotFoundException if passed non id comment
     */
    public void removeCommentForAds(Integer adPk, Integer commentId){
        Comment comment = commentRepository.findAllByIdAndAdsId(adPk, commentId).orElseThrow(() ->
                new CommentNotFoundException(commentId));
            removeComment(comment);
    }

//    public void removeCommentAdmin(Integer commentId) {
//        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
//                new CommentNotFoundException(commentId));
//        removeComment(comment);
//    }
}
