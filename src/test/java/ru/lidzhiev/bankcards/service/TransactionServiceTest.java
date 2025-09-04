package ru.lidzhiev.bankcards.service;

import ru.lidzhiev.bankcards.entity.Card;
import ru.lidzhiev.bankcards.entity.Transaction;
import ru.lidzhiev.bankcards.repository.CardRepository;
import ru.lidzhiev.bankcards.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import ru.lidzhiev.bankcards.service.impl.TransferService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private final CardRepository cardRepository = mock(CardRepository.class);
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final TransferService transactionService = new TransferService(transactionRepository, cardRepository);

    @Test
    void transfer_successful() {
        Card from = new Card(); from.setId(1L); from.setBalance(100.0); from.setStatus("ACTIVE");
        Card to = new Card(); to.setId(2L); to.setBalance(50.0); to.setStatus("ACTIVE");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.transfer(1L, 2L, 30.0);

        assertEquals(70.0, from.getBalance());
        assertEquals(80.0, to.getBalance());
        assertEquals("COMPLETED", result.getStatus());
        verify(cardRepository, times(1)).save(from);
        verify(cardRepository, times(1)).save(to);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void transfer_notEnoughFunds() {
        Card from = new Card(); from.setId(1L); from.setBalance(10.0); from.setStatus("ACTIVE");
        Card to = new Card(); to.setId(2L); to.setBalance(50.0); to.setStatus("ACTIVE");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> transactionService.transfer(1L, 2L, 20.0));
        assertTrue(e.getMessage().contains("Недостаточно средств"));
    }

    @Test
    void transfer_blockedCard() {
        Card from = new Card(); from.setId(1L); from.setBalance(100.0); from.setStatus("BLOCKED");
        Card to = new Card(); to.setId(2L); to.setBalance(50.0); to.setStatus("ACTIVE");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> transactionService.transfer(1L, 2L, 20.0));
        assertTrue(e.getMessage().contains("Одна из карт заблокирована"));
    }
}

