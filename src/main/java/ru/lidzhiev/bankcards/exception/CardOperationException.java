package ru.lidzhiev.bankcards.exception;

import lombok.Getter;

@Getter
public class CardOperationException extends RuntimeException {
    private final ErrorCode errorCode;

    public CardOperationException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }
}
