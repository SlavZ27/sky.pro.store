package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.repository.CommentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class CommentServiceImpl {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
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

}
