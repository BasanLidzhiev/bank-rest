package ru.lidzhiev.bankcards.service;

import ru.lidzhiev.bankcards.dto.TransferRequestDto;
import ru.lidzhiev.bankcards.entity.Transaction;

public interface TransferService {
    Transaction transfer(TransferRequestDto transferRequest);
}
