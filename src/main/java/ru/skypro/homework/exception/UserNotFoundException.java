package ru.skypro.homework.exception;

import org.webjars.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException (Integer id) {
        super("User with id: " + id + " not found");
    }
    public UserNotFoundException (String message) {
        super(message);
    }
}
