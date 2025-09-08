package ru.lidzhiev.bankcards.util;

/**
 * Утилитарный класс для маскировки номеров банковских карт.
 * Содержит метод для скрытия части номера карты, оставляя видимыми только последние четыре цифры.
 */
public class CardMaskUtil {

    /**
     * Маскирует номер банковской карты, скрывая все символы кроме последних четырех.
     *
     * @param number номер карты, подлежащий маскировке.
     * @return замаскированную версию номера карты.
     */
    public static String maskCardNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        String last4 = number.substring(number.length() - 4);
        return "**** **** **** " + last4;
    }
}