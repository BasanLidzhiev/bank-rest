package ru.lidzhiev.bankcards.util;

/**
 * Утилитарный класс для генерации случайных номеров банковских карт.
 * Генерирует шестнадцатизначный номер карты, состоящий из цифр.
 */
public class RandomCardNumber {

    /**
     * Генерирует случайный шестнадцатизначный номер банковской карты.
     *
     * @return строку, представляющую собой случайно сгенерированный номер карты.
     */
    public static String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append((int)(Math.random() * 10));
        }
        return sb.toString();
    }
}
