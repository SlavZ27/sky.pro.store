package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.webjars.NotFoundException;
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ImageNotFoundException extends NotFoundException {
    public ImageNotFoundException(Integer id) {
        super("Image with id: " + id + " not found");
    }

    public ImageNotFoundException(String id) {
        super("Image with id: " + id + " not found");
    }
}
