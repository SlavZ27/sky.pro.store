package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.webjars.NotFoundException;
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AvatarNotFoundException extends NotFoundException {
    public AvatarNotFoundException(Integer id) {
        super("Avatar with id: " + id + " not found");
    }

    public AvatarNotFoundException(String message) {
        super(message);
    }
}
