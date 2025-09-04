package ru.lidzhiev.bankcards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lidzhiev.bankcards.entity.Card;
import ru.lidzhiev.bankcards.entity.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromCard(Card fromCard);
    List<Transaction> findByToCard(Card toCard);
    List<Transaction> findByFromCardOrToCard(Card fromCard, Card toCard);
}
