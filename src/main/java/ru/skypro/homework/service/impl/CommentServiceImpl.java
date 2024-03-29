package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class processes commands related to create comments
 * in ads allowing users to create, update, get, delete comments.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    /**
     * Allows to create a new comment.
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentRepository#save(Object)}
     *
     * @param ads     is not null
     * @param comment is npt null
     * @return {@link Comment}
     * @throws AdsNotFoundException     - if passed non- existent id
     * @throws IllegalArgumentException if passed non- existent parameters
     */
    @Override
    public Comment addCommentsToAds(Ads ads, Comment comment, User author) {
        if (comment == null) {
            log.error("Attempt to add a comment with null value");
            throw new IllegalArgumentException();
        }
        comment.setAds(ads);
        comment.setAuthor(author);
        comment.setDateTime(LocalDateTime.now());
        comment = commentRepository.save(comment);
        log.info("A new comment with ID: {} has been added to ad with ID: {}", comment.getId(), ads.getId());
        return comment;
    }

    /**
     * Allows to update comment.
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentRepository#findById(Object)}
     * Uses {@link CommentRepository#save(Object)}
     *
     * @param commentDto is not null
     * @param ads        is not null
     * @param commentId  is not null
     * @return {@link Comment}
     * @throws AdsNotFoundException     if passed non-existent ads id
     * @throws CommentNotFoundException if passed non-existent comment id
     */
    @Override
    public Comment updateCommentsForAds(CommentDto commentDto, Ads ads, Integer commentId) {
        Comment newComment = commentMapper.dtoToComment(commentDto);
        Comment oldComment = commentRepository.findByIdAndAdsId(ads.getId(), commentId).orElseThrow(() -> {
            log.debug("Comment with ID: {} not found", commentId);
            return new CommentNotFoundException(commentId);
        });
        if (newComment.getAuthor() != null) {
            oldComment.setAuthor(ads.getAuthor());
        }
        if (newComment.getText() != null && newComment.getText().length() > 0) {
            oldComment.setText(newComment.getText());
        }
        oldComment.setDateTime(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(oldComment);
        log.info("Comment with ID: {} has been updated", commentId);
        return updatedComment;
    }

    /**
     * Gets count by ads id
     *
     * @param idAds the id ads
     * @return Integer - the count by ads id
     */
    @Override
    public Integer getCountByIdAds(Integer idAds) {
        return commentRepository.getCountAllByAdsId(idAds);
    }

    /**
     * Allows to get all comments by ads of id, sorted by date time
     * Uses {@link CommentRepository#findAllByIdAdsAndSortDateTime(Integer)}
     *
     * @param adsId is not null
     * @return List of {@link Comment}s
     */
    @Override
    public List<Comment> getAllByIdAdsAndSortDateTime(Integer adsId) {
        return commentRepository.findAllByIdAdsAndSortDateTime(adsId);
    }

    /**
     * Allows to get comment by ads id and comment id
     * Uses {@link CommentRepository#findByIdAndAdsId(Integer, Integer)}
     *
     * @param adsId     is not null
     * @param commentId is not null
     * @return {@link Comment}
     * @throws CommentNotFoundException if passed non-existent id comment
     */
    @Override
    public Comment getCommentOfAds(Integer adsId, Integer commentId) {
        return commentRepository.findByIdAndAdsId(adsId, commentId)
                .orElseThrow(() -> {
                    log.debug("Comment with ID: {} not found", commentId);
                    return new CommentNotFoundException(commentId);
                });
    }

    /**
     * Allows to delete comment
     * Uses {@link CommentRepository#delete(Object)}
     *
     * @param comment is not null
     */
    @Override
    public void removeComment(Comment comment) {
        commentRepository.delete(comment);
        log.info("Comment with ID: {} has been deleted", comment.getId());
    }

    /**
     * Allows to delete all comments by ads id
     * Uses {@link CommentRepository#deleteAllByAdsId(Integer)}
     *
     * @param idAds is not null
     */
    @Override
    public void removeAllCommentsOfAds(Integer idAds) {
        commentRepository.deleteAllByAdsId(idAds);
        log.info("All comments for the 'ads' with ID: {} have been deleted", idAds);
    }

    /**
     * Allows to delete comment by ads id and comment id
     * Uses {@link CommentRepository#findByIdAndAdsId(Integer, Integer)}
     *
     * @param adPk      is not null
     * @param commentId is not null
     * @throws CommentNotFoundException if passed non-existent id comment
     */
    @Override
    public void removeCommentForAds(Integer adPk, Integer commentId) {
        Comment comment = commentRepository.findByIdAndAdsId(adPk, commentId).orElseThrow(() -> {
            log.debug("Comment with ID: {} not found", commentId);
            return new CommentNotFoundException(commentId);
        });
        removeComment(comment);
        log.info("Comment with ID: {} and AdsId: {} have been deleted", commentId, adPk);
    }
}
