package ru.skypro.homework.service;

import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;

import java.util.List;

public interface CommentService {
    Comment addCommentsToAds(Ads ads, Comment comment, User author);

    Comment updateCommentsForAds(CommentDto commentDto, Ads ads, Integer commentId);

    Integer getCountByIdAds(Integer idAds);

    List<Comment> getAllByIdAdsAndSortDateTime(Integer adsId);

    Comment getCommentOfAds(Integer adsId, Integer commentId);

    void removeComment(Comment comment);

    void removeAllCommentsOfAds(Integer idAds);

    void removeCommentForAds(Integer adPk, Integer commentId);
}
