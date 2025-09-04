package ru.lidzhiev.bankcards.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CardDto {
    private Long id;
    private String maskedNumber;
    private String status;
    private String expireAt;
    private Double balance;
    private String ownerUsername;

    public CardDto() {}


    public CardDto(Long id, String maskedNumber, String status, String expireAt, Double balance, String ownerUsername) {
        this.id = id;
        this.maskedNumber = maskedNumber;
        this.status = status;
        this.expireAt = expireAt;
        this.balance = balance;
        this.ownerUsername = ownerUsername;
    }

}

