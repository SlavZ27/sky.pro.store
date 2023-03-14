package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AdsNotFoundException extends RuntimeException {
    public AdsNotFoundException (Integer id) {
        super("Ads with id: " + id + " not found");
    }
    public AdsNotFoundException (String message) {
        super(message);
    }
}
