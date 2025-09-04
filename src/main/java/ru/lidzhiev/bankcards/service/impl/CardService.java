package ru.lidzhiev.bankcards.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.lidzhiev.bankcards.dto.CardDto;
import ru.lidzhiev.bankcards.dto.CreateCardDto;
import ru.lidzhiev.bankcards.dto.TransferRequestDto;
import ru.lidzhiev.bankcards.entity.Card;
import ru.lidzhiev.bankcards.entity.User;
import ru.lidzhiev.bankcards.entity.enums.CardStatus;
import ru.lidzhiev.bankcards.repository.CardRepository;
import ru.lidzhiev.bankcards.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static ru.lidzhiev.bankcards.util.CardMaskUtil.maskCardNumber;
import static ru.lidzhiev.bankcards.util.RandomCardNumber.generateCardNumber;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardService(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CardDto create(CreateCardDto dto, String username) {
        if (dto.getBalance() != null && dto.getBalance() < 0) {
            throw new IllegalArgumentException("Balance can't be negative!");
        }

        LocalDate expireAt = LocalDate.parse(dto.getExpireAt());
        if (expireAt.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiration date can't be in the past!");
        }

        User owner = findUserEntityByUsername(username);

        Card card = new Card();
        card.setNumber(generateCardNumber());
        card.setExpireAt(expireAt);
        card.setBalance(dto.getBalance() != null ? dto.getBalance() : 0.0);
        card.setStatus(CardStatus.ACTIVE.name());
        card.setOwner(owner);

        Card saved = cardRepository.save(card);
        return toDto(saved);
    }

    public CardDto userRequestCardBlock(CardDto dto, String username) {
        Card card = cardRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + dto.getId()));

        // check if the card belongs to the user
        if (!card.getOwner().getUsername().equals(username)) {
            throw new SecurityException("Access denied: this card does not belong to you");
        }

        // set up the request block status
        card.setStatus(CardStatus.REQUEST_BLOCKED.name());

        Card updated = cardRepository.save(card);
        return toDto(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CardDto blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + cardId));
        card.setStatus(CardStatus.BLOCKED.name());
        Card saved = cardRepository.save(card);
        return toDto(saved);
    }

    public Page<CardDto> getByUsername(String username, Pageable pageable) {
        Page<Card> cards = cardRepository.findByOwnerUsername(username, pageable);
        return cards.map(this::toDto);
    }


    public CardDto getById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
        return toDto(card);
    }

    public CardDto adminUpdateCardStatus(Long id, String status) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));

        try {
            CardStatus newStatus = CardStatus.valueOf(status.toUpperCase());
            card.setStatus(newStatus.name());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid card status: " + status);
        }

        Card updated = cardRepository.save(card);
        return toDto(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
        cardRepository.delete(card);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<CardDto> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Find owner by по username
    private User findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public CardDto toDto(Card card) {
        return new CardDto(
                card.getId(),
                maskCardNumber(card.getNumber()),
                card.getStatus(),
                card.getExpireAt().toString(),
                card.getBalance(),
                card.getOwner() != null ? card.getOwner().getUsername() : null
        );
    }

    public void transfer(TransferRequestDto dto, String username) {
        if (dto.getFromCardId().equals(dto.getToCardId())) {
            throw new IllegalArgumentException("Cannot transfer to the same card");
        }

        Card from = cardRepository.findById(dto.getFromCardId())
                .orElseThrow(() -> new RuntimeException("From-card not found"));
        Card to = cardRepository.findById(dto.getToCardId())
                .orElseThrow(() -> new RuntimeException("To-card not found"));

        // check owner both cards
        if (!from.getOwner().getUsername().equals(username) || !to.getOwner().getUsername().equals(username)) {
            throw new SecurityException("Both cards must belong to the current user");
        }

        if (from.getBalance() < dto.getAmount()) {
            throw new IllegalArgumentException("Not enough balance to transfer");
        }

        from.setBalance(from.getBalance() - dto.getAmount());
        to.setBalance(to.getBalance() + dto.getAmount());

        cardRepository.save(from);
        cardRepository.save(to);
    }

}
