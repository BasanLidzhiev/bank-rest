package ru.lidzhiev.bankcards.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateCardDto {

    @NotNull(message = "Expiration date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Expiration date must be in format YYYY-MM-DD")
    private String expireAt;

    @NotNull(message = "Balance is required")
    @Min(value = 0, message = "Balance cannot be negative")
    private Double balance;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be 3-30 chars")
    private String username;

    public CreateCardDto() {
    }

    public CreateCardDto(String expireAt, Double balance, String username) {
        this.expireAt = expireAt;
        this.balance = balance;
        this.username = username;
    }

}