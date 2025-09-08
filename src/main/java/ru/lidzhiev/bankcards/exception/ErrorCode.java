package ru.lidzhiev.bankcards.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Пользователь не найден"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "Пользователь с таким именем уже существует"),
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "Карта не найдена"),
    CARD_EXPIRED(HttpStatus.FORBIDDEN, "Операция невозможна: карта просрочена"),
    CARD_BLOCKED(HttpStatus.FORBIDDEN, "Операция невозможна: карта заблокирована"),
    INVALID_STATUS(HttpStatus.FORBIDDEN, "Операция невозможна: некорректный статус карты"),
    CARD_INSUFFICIENT_FUNDS(HttpStatus.BAD_REQUEST, "Недостаточно средств на карте"),
    SAME_CARD_TRANSFER(HttpStatus.CONFLICT, "Перевод на одну и ту же карту невозможен"),
    NOT_OWNER(HttpStatus.FORBIDDEN, "Ошибка при выполнении операции. Карта не принадлежит владельцу");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }
}
