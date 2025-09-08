package ru.lidzhiev.bankcards.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TransferRequestDto {
    @NotNull(message = "Source card ID is required")
    private String fromCardNumber;

    @NotNull(message = "Destination card ID is required")
    private String toCardNumber;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    private Double amount;

    public TransferRequestDto() {
    }

    public TransferRequestDto(String fromCardNumber, String toCardNumber, Double amount) {
        this.fromCardNumber = fromCardNumber;
        this.toCardNumber = toCardNumber;
        this.amount = amount;
    }

}
