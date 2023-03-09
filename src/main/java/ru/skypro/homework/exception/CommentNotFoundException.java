package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.webjars.NotFoundException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Integer id) {
        super("Comment with id: " + id + " not found");
    }

    public CommentNotFoundException(String id) {
        super("Comment with id: " + id + " not found");
    }
}
