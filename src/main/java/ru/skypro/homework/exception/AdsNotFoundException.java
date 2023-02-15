package ru.skypro.homework.exception;

public class AdsNotFoundException extends RuntimeException {
    public AdsNotFoundException (Integer id) {
        super("Ads with id: " + id + " not found");
    }
}
