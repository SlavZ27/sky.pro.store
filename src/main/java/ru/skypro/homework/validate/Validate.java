package ru.skypro.homework.validate;

public class Validate {
    public static boolean validatePhone(String phone) {
        if (!phone.startsWith("+")) {
            return false;
        }
        phone = phone.substring(1);
        if (!phone.startsWith("7")) {
            return false;
        }
        phone = phone.substring(1);
        if (!phone.startsWith("9")) {
            return false;
        }
        phone = phone.substring(1);
        if (phone.length() != 9) {
            return false;
        }
        try {
            Integer.parseInt(phone);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
