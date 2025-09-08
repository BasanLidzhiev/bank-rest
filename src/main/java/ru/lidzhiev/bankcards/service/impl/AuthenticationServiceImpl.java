package ru.lidzhiev.bankcards.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.lidzhiev.bankcards.dto.CreateUserDto;
import ru.lidzhiev.bankcards.dto.JwtAuthenticationResponse;
import ru.lidzhiev.bankcards.dto.SignInRequest;
import ru.lidzhiev.bankcards.dto.SignUpRequest;
import ru.lidzhiev.bankcards.entity.User;
import ru.lidzhiev.bankcards.entity.enums.UserRole;
import ru.lidzhiev.bankcards.security.JwtService;
import ru.lidzhiev.bankcards.service.AuthenticationService;
import ru.lidzhiev.bankcards.service.UserService;

/**
 * Сервис аутентификации пользователей.
 * Этот класс реализует интерфейс {@link AuthenticationService}, обеспечивая функциональность регистрации и входа пользователей.
**/
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        CreateUserDto createUserDto = new CreateUserDto(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                UserRole.ROLE_USER
        );
        userService.create(createUserDto);

        // get entity User for JWT
        User user = userService.findEntityByUsername(request.getUsername());

        String jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    // user login
    /**
     * {@inheritDoc}
     */
    @Override
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        User user = userService.findEntityByUsername(request.getUsername());

        String jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}
