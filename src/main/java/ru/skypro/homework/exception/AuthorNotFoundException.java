package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.webjars.NotFoundException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AuthorNotFoundException extends NotFoundException {
    public AuthorNotFoundException(String id) {
        super("Author with id: " + id + " not found");
    }

    public AuthorNotFoundException(Integer id) {
        super("Author with id: " + id + " not found");
    }
}
