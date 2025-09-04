package ru.lidzhiev.bankcards.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.lidzhiev.bankcards.dto.CardDto;
import ru.lidzhiev.bankcards.dto.CreateCardDto;
import ru.lidzhiev.bankcards.dto.TransferRequestDto;
import ru.lidzhiev.bankcards.service.CardService;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/me/paged")
    public Page<CardDto> getMyCardsPaged(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return cardService.getByUsername(userDetails.getUsername(), pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CardDto createCard(@Valid @RequestBody CreateCardDto dto,
                              @AuthenticationPrincipal UserDetails userDetails) {
        return cardService.create(dto, userDetails.getUsername());
    }

    @GetMapping("/{id}")
    public CardDto getById(@PathVariable Long id) {
        return cardService.getById(id);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/status/request-block")
    public CardDto requestBlock(@Valid @RequestBody CardDto dto,
                                @AuthenticationPrincipal UserDetails userDetails) {
        return cardService.userRequestCardBlock(dto, userDetails.getUsername());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/block")
    public CardDto blockCard(@RequestParam Long cardId) {
        return cardService.blockCard(cardId);
    }

    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public List<CardDto> getAllCards() {
        return cardService.getAllCards();
    }

    // admin, set status of card
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/set-status")
    public CardDto adminUpdateCardStatus(@Valid @RequestParam Long cardId,
                                         @RequestParam String status) {
        return cardService.adminUpdateCardStatus(cardId, status);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transfer")
    public ResponseEntity<Void>  transfer(@Valid @RequestBody TransferRequestDto dto,
                         @AuthenticationPrincipal UserDetails userDetails) {
        cardService.transfer(dto, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
