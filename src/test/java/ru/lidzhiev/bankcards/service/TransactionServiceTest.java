package ru.lidzhiev.bankcards.service;

import ru.lidzhiev.bankcards.dto.TransferRequestDto;
import ru.lidzhiev.bankcards.entity.Card;
import ru.lidzhiev.bankcards.entity.Transaction;
import ru.lidzhiev.bankcards.repository.CardRepository;
import ru.lidzhiev.bankcards.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import ru.lidzhiev.bankcards.service.impl.TransferServiceImpl;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private final CardRepository cardRepository = mock(CardRepository.class);
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final TransferService transactionService = new TransferServiceImpl(transactionRepository, cardRepository);

    @Test
    void transfer_successful() {
        Card from = new Card(); from.setId(1L); from.setBalance(100.0); from.setStatus("ACTIVE"); from.setNumber("1234");
        Card to = new Card(); to.setId(2L); to.setBalance(50.0); to.setStatus("ACTIVE"); to.setNumber("1233");

        TransferRequestDto dto = TransferRequestDto.builder()
                .fromCardNumber("1234")
                .toCardNumber("1233")
                .amount(30.0)
                .build();

        when(cardRepository.findByNumber(dto.getFromCardNumber())).thenReturn(Optional.of(from));
        when(cardRepository.findByNumber(dto.getToCardNumber())).thenReturn(Optional.of(to));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.transfer(dto);

        assertEquals(70.0, from.getBalance());
        assertEquals(80.0, to.getBalance());
        assertEquals("COMPLETED", result.getStatus());
        verify(cardRepository, times(1)).saveAll(List.of(from, to));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void transfer_notEnoughFunds() {
        Card from = new Card(); from.setId(1L); from.setBalance(10.0); from.setStatus("ACTIVE"); from.setNumber("1234");
        Card to = new Card(); to.setId(2L); to.setBalance(50.0); to.setStatus("ACTIVE"); to.setNumber("1233");

        TransferRequestDto dto = TransferRequestDto.builder()
                .fromCardNumber("1234")
                .toCardNumber("1233")
                .amount(20.0)
                .build();
        when(cardRepository.findByNumber(dto.getFromCardNumber())).thenReturn(Optional.of(from));
        when(cardRepository.findByNumber(dto.getToCardNumber())).thenReturn(Optional.of(to));

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> transactionService.transfer(dto));
        assertTrue(e.getMessage().contains("Not enough balance to transfer"));
    }

    @Test
    void transfer_blockedCard() {
        Card from = new Card(); from.setId(1L); from.setBalance(30.0); from.setStatus("BLOCKED"); from.setNumber("1234");
        Card to = new Card(); to.setId(2L); to.setBalance(50.0); to.setStatus("ACTIVE"); to.setNumber("1233");

        TransferRequestDto dto = TransferRequestDto.builder()
                .fromCardNumber("1234")
                .toCardNumber("1233")
                .amount(20.0)
                .build();
        when(cardRepository.findByNumber(dto.getFromCardNumber())).thenReturn(Optional.of(from));
        when(cardRepository.findByNumber(dto.getToCardNumber())).thenReturn(Optional.of(to));

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> transactionService.transfer(dto));
        assertTrue(e.getMessage().contains("Одна из карт заблокирована"));
    }
}

