package ru.lidzhiev.bankcards.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.lidzhiev.bankcards.dto.CreateUserDto;
import ru.lidzhiev.bankcards.dto.JwtAuthenticationResponse;
import ru.lidzhiev.bankcards.dto.SignInRequest;
import ru.lidzhiev.bankcards.dto.SignUpRequest;
import ru.lidzhiev.bankcards.entity.User;
import ru.lidzhiev.bankcards.security.JwtService;
import ru.lidzhiev.bankcards.service.impl.AuthenticationServiceImpl;
import ru.lidzhiev.bankcards.service.impl.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private UserService userService;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private ru.lidzhiev.bankcards.service.AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);

        authenticationService = new AuthenticationServiceImpl(
                userService, jwtService, authenticationManager
        );
    }

    @Test
    void signUp_ShouldReturnJwtResponse() {
        // Arrange
        SignUpRequest request = new SignUpRequest("alex12", "alex@mail.com", "pass123");
        User user = new User();
        user.setUsername("alex12");
        when(userService.create(any(CreateUserDto.class))).thenReturn(any());
        when(userService.findEntityByUsername("alex12")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("mocked-jwt-token");

        // Act
        JwtAuthenticationResponse response = authenticationService.signUp(request);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        verify(userService).create(any(CreateUserDto.class));
        verify(jwtService).generateToken(user);
    }

    @Test
    void signIn_ShouldReturnJwtResponse() {
        // Arrange
        SignInRequest request = new SignInRequest("alex12", "pass123");
        User user = new User();
        user.setUsername("alex12");
        when(userService.findEntityByUsername("alex12")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("mocked-jwt-token");

        // Act
        JwtAuthenticationResponse response = authenticationService.signIn(request);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(user);
    }
}
