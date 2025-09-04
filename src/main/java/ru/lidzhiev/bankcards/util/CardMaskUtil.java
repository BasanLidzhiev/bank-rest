package ru.lidzhiev.bankcards.util;

public class CardMaskUtil {

    // Метод static, чтобы использовать без создания объекта
    public static String maskCardNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        String last4 = number.substring(number.length() - 4);
        return "**** **** **** " + last4;
    }
}