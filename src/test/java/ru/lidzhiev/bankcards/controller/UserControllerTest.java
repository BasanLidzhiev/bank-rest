package ru.lidzhiev.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.lidzhiev.bankcards.dto.CreateUserDto;
import ru.lidzhiev.bankcards.dto.UserDto;
import ru.lidzhiev.bankcards.entity.enums.UserRole;
import ru.lidzhiev.bankcards.security.JwtService;
import ru.lidzhiev.bankcards.service.impl.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void register_ShouldReturnUserDto() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto("alex12", "alex@mail.com", "pass123", UserRole.ROLE_ADMIN );
        UserDto userDto = new UserDto(1L, "alex12", "alex@mail.com");

        Mockito.when(userService.create(any(CreateUserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alex12"));
    }

    @Test
    @WithMockUser(username = "alex12", roles = "USER")
    void getCurrentUser_ShouldReturnUserDto() throws Exception {
        UserDto userDto = new UserDto(1L, "alex12", "alex@mail.com");

        Mockito.when(userService.getByUsername("alex12")).thenReturn(userDto);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alex12"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_AsAdmin_ShouldReturnUserDto() throws Exception {
        UserDto userDto = new UserDto(1L, "alex12", "alex@mail.com");

        Mockito.when(userService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alex12"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_AsAdmin_ShouldReturnUserDto() throws Exception {
        UserDto dto = new UserDto(1L, "alex12", "alex@mail.com");

        Mockito.when(userService.updateUser(any(UserDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alex12"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_AsAdmin_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk()); // Или isNoContent(), если в контроллере вернёшь 204
    }
}
