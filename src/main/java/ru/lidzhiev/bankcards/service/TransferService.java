package ru.lidzhiev.bankcards.service;

import ru.lidzhiev.bankcards.entity.Transaction;

public interface TransferService {
    Transaction transfer(Long fromCardId, Long toCardId, Double amount);
}
