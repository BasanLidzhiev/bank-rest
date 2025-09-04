package ru.lidzhiev.bankcards.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lidzhiev.bankcards.entity.Card;
import ru.lidzhiev.bankcards.entity.Transaction;
import ru.lidzhiev.bankcards.repository.CardRepository;
import ru.lidzhiev.bankcards.repository.TransactionRepository;
import ru.lidzhiev.bankcards.service.TransferService;

import java.time.LocalDateTime;

@Service
public class TransferServiceImpl implements TransferService {
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;

    public TransferServiceImpl(TransactionRepository transactionRepository, CardRepository cardRepository) {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional
    public Transaction transfer(Long fromCardId, Long toCardId, Double amount) {
        Card fromCard = cardRepository.findById(fromCardId)
                .orElseThrow(() -> new RuntimeException("Карта-отправитель не найдена"));
        Card toCard = cardRepository.findById(toCardId)
                .orElseThrow(() -> new RuntimeException("Карта-получатель не найдена"));

        if (fromCard.getBalance() < amount) {
            throw new RuntimeException("Недостаточно средств на карте-отправителе");
        }

        if (!fromCard.getStatus().equals("ACTIVE") || !toCard.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Одна из карт заблокирована или неактивна");
        }

        //transfer
        fromCard.setBalance(fromCard.getBalance() - amount);
        toCard.setBalance(toCard.getBalance() + amount);

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        Transaction transaction = new Transaction();
        transaction.setFromCard(fromCard);
        transaction.setToCard(toCard);
        transaction.setAmount(amount);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setStatus("COMPLETED");

        return transactionRepository.save(transaction);
    }
}
