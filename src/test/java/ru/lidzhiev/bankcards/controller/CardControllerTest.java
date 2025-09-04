package ru.lidzhiev.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.lidzhiev.bankcards.dto.CardDto;
import ru.lidzhiev.bankcards.dto.CreateCardDto;
import ru.lidzhiev.bankcards.dto.TransferRequestDto;
import ru.lidzhiev.bankcards.security.JwtService;
import ru.lidzhiev.bankcards.service.impl.CardService;
import ru.lidzhiev.bankcards.service.impl.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username="alex12", roles={"ADMIN"})
    void createCard_ReturnsCardDto_WhenRequestIsValid() throws Exception {
        CreateCardDto createCardDto = new CreateCardDto("2026-01-01", 1000.0, "alex");
        CardDto cardDto = new CardDto(1L, "**** **** **** 1234", "ACTIVE", "2026-01-01", 1000.0, "alex");

        when(cardService.create(any(CreateCardDto.class), eq("alex12"))).thenReturn(cardDto);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCardDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maskedNumber").value("**** **** **** 1234"));
    }

    @Test
    @WithMockUser(username="alex12", roles={"USER"})
    void transfer_ReturnsNoContent_WhenTransferSuccessful() throws Exception {
        TransferRequestDto transferRequestDto = new TransferRequestDto(1L, 2L, 100.0);

        doNothing().when(cardService).transfer(any(TransferRequestDto.class), eq("alex12"));

        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequestDto)))
                .andExpect(status().isNoContent());

        verify(cardService, times(1)).transfer(any(TransferRequestDto.class), eq("alex12"));
    }

    @Test
    @WithMockUser(username="alex12", roles={"USER"})
    void getMyCardsPaged_ReturnsPageOfCards() throws Exception {
        CardDto cardDto1 = new CardDto(1L, "**** **** **** 1234", "ACTIVE", "2026-01-01", 1000.0, "alex");
        CardDto cardDto2 = new CardDto(2L, "**** **** **** 5678", "ACTIVE", "2026-01-01", 2000.0, "alex");
        List<CardDto> cards = List.of(cardDto1, cardDto2);

        Pageable pageable = PageRequest.of(0, 5);
        PageImpl<CardDto> page = new PageImpl<>(cards, pageable, cards.size());

        when(cardService.getByUsername(eq("alex12"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/cards/me/paged")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].maskedNumber").value("**** **** **** 1234"))
                .andExpect(jsonPath("$.content[1].maskedNumber").value("**** **** **** 5678"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }


}
