package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(Integer id) {
        super("Image with id: " + id + " not found");
    }

    public ImageNotFoundException(String message) {
        super(message);
    }
}
