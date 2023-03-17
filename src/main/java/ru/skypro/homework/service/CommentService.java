package ru.skypro.homework.service;

import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;

import java.util.List;

/**
 * Provides methods for comment processing
 */
public interface CommentService {
    /**
     * Add comments to ads.
     *
     * @param ads     the ads
     * @param comment the comment
     * @param author  the author
     * @return {@link Comment}
     */
    Comment addCommentsToAds(Ads ads, Comment comment, User author);

    /**
     * Update comments for ads.
     *
     * @param commentDto the comment dto
     * @param ads        the ads
     * @param commentId  the comment id
     * @return {@link Comment}
     */
    Comment updateCommentsForAds(CommentDto commentDto, Ads ads, Integer commentId);

    /**
     * Gets count by ads of id.
     *
     * @param idAds the id ads
     * @return Integer - the count
     */
    Integer getCountByIdAds(Integer idAds);

    /**
     * Gets all by ads id  and sorted by date time.
     *
     * @param adsId the ads id
     * @return List of {@link Comment}s
     */
    List<Comment> getAllByIdAdsAndSortDateTime(Integer adsId);

    /**
     * Gets comment of ads.
     *
     * @param adsId     the ads id
     * @param commentId the comment id
     * @return {@link Comment}
     */
    Comment getCommentOfAds(Integer adsId, Integer commentId);

    /**
     * Remove comment.
     *
     * @param comment the comment
     */
    void removeComment(Comment comment);

    /**
     * Remove all comments of ads.
     *
     * @param idAds the ads id
     */
    void removeAllCommentsOfAds(Integer idAds);

    /**
     * Remove comment by ads id and comment id.
     *
     * @param adPk      the ad pk
     * @param commentId the comment id
     */
    void removeCommentForAds(Integer adPk, Integer commentId);
}
