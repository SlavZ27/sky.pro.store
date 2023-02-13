package ru.skypro.homework.exception;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(String message) {
        super("Author with id: " + message + " not found");
    }
}
