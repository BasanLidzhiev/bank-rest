package ru.lidzhiev.bankcards.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lidzhiev.bankcards.dto.CardDto;
import ru.lidzhiev.bankcards.dto.CreateCardDto;
import ru.lidzhiev.bankcards.dto.TransferRequestDto;
import ru.lidzhiev.bankcards.entity.Card;
import ru.lidzhiev.bankcards.entity.User;
import ru.lidzhiev.bankcards.entity.enums.CardStatus;
import ru.lidzhiev.bankcards.exception.CardOperationException;
import ru.lidzhiev.bankcards.exception.ErrorCode;
import ru.lidzhiev.bankcards.exception.ResourceNotFoundException;
import ru.lidzhiev.bankcards.repository.CardRepository;
import ru.lidzhiev.bankcards.repository.UserRepository;
import ru.lidzhiev.bankcards.service.CardService;

import java.time.LocalDate;
import java.util.List;

import static ru.lidzhiev.bankcards.util.CardMaskUtil.maskCardNumber;
import static ru.lidzhiev.bankcards.util.RandomCardNumber.generateCardNumber;
/**
 * Реализация интерфейса {@link CardService} для операций с банковскими картами.
 * Данный класс обеспечивает управление карточными операциями: создание новых карт, обновление статуса карты, получение списка карт,
 * выполнение переводов денежных средств и удаление карт. Некоторые методы требуют роли администратора ("ADMIN").
 * Методы, доступные только администраторам, отмечены аннотацией `@PreAuthorize`.
 */
@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardServiceImpl(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto create(CreateCardDto dto, String username) {
        if (dto.getBalance() != null && dto.getBalance() < 0) {
            throw new CardOperationException(ErrorCode.CARD_INSUFFICIENT_FUNDS);
        }

        LocalDate expireAt = LocalDate.parse(dto.getExpireAt());
        if (expireAt.isBefore(LocalDate.now())) {
            throw new CardOperationException(ErrorCode.CARD_EXPIRED);
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

    /**
     * {@inheritDoc}
     */
    public CardDto userRequestCardBlock(CardDto dto, String username) {
        Card card = cardRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));

        // check if the card belongs to the user
        if (!card.getOwner().getUsername().equals(username)) {
            throw new CardOperationException(ErrorCode.NOT_OWNER);
        }

        // set up the request block status
        card.setStatus(CardStatus.REQUEST_BLOCKED.name());

        Card updated = cardRepository.save(card);
        return toDto(updated);
    }
    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));
        card.setStatus(CardStatus.BLOCKED.name());
        Card saved = cardRepository.save(card);
        return toDto(saved);
    }
    /**
     * {@inheritDoc}
     */
    public Page<CardDto> getByUsername(String username, Pageable pageable) {
        Page<Card> cards = cardRepository.findByOwnerUsername(username, pageable);
        return cards.map(this::toDto);
    }

    /**
     * {@inheritDoc}
     */
    public CardDto getById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));
        return toDto(card);
    }

    /**
     * {@inheritDoc}
     */
    public CardDto adminUpdateCardStatus(Long id, String status) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));

        try {
            CardStatus newStatus = CardStatus.valueOf(status.toUpperCase());
            card.setStatus(newStatus.name());
        } catch (IllegalArgumentException e) {
            throw new CardOperationException(ErrorCode.INVALID_STATUS);
        }

        Card updated = cardRepository.save(card);
        return toDto(updated);
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));
        cardRepository.delete(card);
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<CardDto> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Приватный метод поиска пользователя по имени.
     * Используется для нахождения владельца карты.
     *
     * @param username имя пользователя.
     * @return объект пользователя.
     * @throws ResourceNotFoundException если пользователь не найден.
     */
    private User findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * Приватный метод преобразования сущности Card в объект DTO.
     * Скрывает номер карты, отображая его в замаскированном виде.
     *
     * @param card сущность карты.
     * @return объект DTO карты.
     */
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

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void transfer(TransferRequestDto dto, String username) {
        Card from = cardRepository.findByNumber(dto.getFromCardNumber())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));
        Card to = cardRepository.findByNumber(dto.getToCardNumber())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));
        validateUserCards(username, from, to);
        validateTransfer(dto, username, from, to);
        from.setBalance(from.getBalance() - dto.getAmount());
        to.setBalance(to.getBalance() + dto.getAmount());

        cardRepository.save(from);
        cardRepository.save(to);
    }

    /**
     * Приватный метод проверки принадлежности двух карт одному пользователю.
     * Проверяет, принадлежат ли обе карты указанному пользователю.
     *
     * @param username имя пользователя.
     * @param from     исходная карта.
     * @param to       целевая карта.
     * @throws CardOperationException если одна из карт не принадлежит пользователю.
     */
    private void validateUserCards(String username, Card from, Card to) {
        if (!from.getOwner().getUsername().equals(username) || !to.getOwner().getUsername().equals(username)) {
            throw new CardOperationException(ErrorCode.NOT_OWNER);
        }
    }

    /**
     * Приватный метод проверки условий перевода.
     * Проверяет условия для осуществления перевода: совпадение номеров карт, активные статусы карт, достаточность средств.
     *
     * @param dto       объект DTO с деталями перевода.
     * @param username  имя пользователя владельца карт.
     * @param from      исходная карта.
     * @param to        целевая карта.
     * @throws CardOperationException если возникает ошибка при проверке условий.
     */
    private void validateTransfer(TransferRequestDto dto, String username, Card from, Card to) {
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


}
