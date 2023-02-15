package ru.skypro.homework.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Integer id) {
        super("Comment with id: " + id + " not found");
    }
}
