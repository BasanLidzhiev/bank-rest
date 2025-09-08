package ru.lidzhiev.bankcards.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lidzhiev.bankcards.dto.TransferRequestDto;
import ru.lidzhiev.bankcards.entity.Card;
import ru.lidzhiev.bankcards.entity.Transaction;
import ru.lidzhiev.bankcards.repository.CardRepository;
import ru.lidzhiev.bankcards.repository.TransactionRepository;
import ru.lidzhiev.bankcards.service.TransferService;
import ru.lidzhiev.bankcards.util.CardMaskUtil;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferServiceImpl implements TransferService {
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;

    public TransferServiceImpl(TransactionRepository transactionRepository, CardRepository cardRepository) {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional
    public Transaction transfer(TransferRequestDto dto) {
        Card fromCard = findUserCard(dto.getFromCardNumber());
        Card toCard = findUserCard(dto.getToCardNumber());

        validateTransfer(dto, fromCard, toCard);
        doTransfer(dto.getAmount(), fromCard, toCard);

        return saveTransaction(dto, fromCard, toCard);
    }

    private Card findUserCard (String cardNumber) {
        return cardRepository.findByNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card with number " + CardMaskUtil.maskCardNumber(cardNumber) + " not found"));
    }

    private void validateTransfer(TransferRequestDto dto, Card from, Card to) {
        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same card");
        }
        if (!from.getStatus().equals("ACTIVE") || !to.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Одна из карт заблокирована или неактивна");
        }
        if (from.getBalance() < dto.getAmount()) {
            throw new IllegalArgumentException("Not enough balance to transfer");
        }
    }

    private void doTransfer(Double amount, Card from, Card to) {
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        cardRepository.saveAll(List.of(from, to));
    }

    private Transaction saveTransaction(TransferRequestDto dto, Card from, Card to) {
        Transaction transaction = Transaction.builder()
                .fromCard(from)
                .toCard(to)
                .amount(dto.getAmount())
                .createdAt(LocalDateTime.now())
                .status("COMPLETED")
                .build();
        return transactionRepository.save(transaction);
    }
}
