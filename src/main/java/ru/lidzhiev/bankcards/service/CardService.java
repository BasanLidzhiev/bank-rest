package ru.lidzhiev.bankcards.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.lidzhiev.bankcards.dto.CardDto;
import ru.lidzhiev.bankcards.dto.CreateCardDto;
import ru.lidzhiev.bankcards.dto.TransferRequestDto;

import java.util.List;

public interface CardService {
    CardDto create(CreateCardDto dto, String username);
    CardDto userRequestCardBlock(CardDto dto, String username);
    CardDto blockCard(Long cardId);
    Page<CardDto> getByUsername(String username, Pageable pageable);
    CardDto getById(Long id);
    CardDto adminUpdateCardStatus(Long id, String status);
    void deleteCard(Long id);
    List<CardDto> getAllCards();
    void transfer(TransferRequestDto dto, String username);
}
