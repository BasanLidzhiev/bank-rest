package ru.lidzhiev.bankcards.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lidzhiev.bankcards.dto.TransferRequestDto;
import ru.lidzhiev.bankcards.entity.Card;
import ru.lidzhiev.bankcards.entity.Transaction;
import ru.lidzhiev.bankcards.exception.CardOperationException;
import ru.lidzhiev.bankcards.exception.ErrorCode;
import ru.lidzhiev.bankcards.exception.ResourceNotFoundException;
import ru.lidzhiev.bankcards.repository.CardRepository;
import ru.lidzhiev.bankcards.repository.TransactionRepository;
import ru.lidzhiev.bankcards.service.TransferService;

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
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));
    }

    private void validateTransfer(TransferRequestDto dto, Card from, Card to) {
        if (from.getId().equals(to.getId())) {
            throw new CardOperationException(ErrorCode.SAME_CARD_TRANSFER);
        }
        if (!from.getStatus().equals("ACTIVE") || !to.getStatus().equals("ACTIVE")) {
            throw new CardOperationException(ErrorCode.CARD_BLOCKED);
        }
        if (from.getBalance() < dto.getAmount()) {
            throw new CardOperationException(ErrorCode.CARD_INSUFFICIENT_FUNDS);
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
