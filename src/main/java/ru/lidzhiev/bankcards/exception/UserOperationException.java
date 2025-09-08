package ru.lidzhiev.bankcards.exception;

import lombok.Getter;

@Getter
public class UserOperationException extends RuntimeException {
    private final ErrorCode errorCode;

    public UserOperationException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }
}
