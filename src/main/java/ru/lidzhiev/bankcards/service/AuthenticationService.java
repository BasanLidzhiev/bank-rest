package ru.lidzhiev.bankcards.service;

import ru.lidzhiev.bankcards.dto.JwtAuthenticationResponse;
import ru.lidzhiev.bankcards.dto.SignInRequest;
import ru.lidzhiev.bankcards.dto.SignUpRequest;
import ru.lidzhiev.bankcards.exception.ResourceNotFoundException;
import ru.lidzhiev.bankcards.exception.UserOperationException;
/**
 * Сервис аутентификации пользователей банка.
 *
 * Данный интерфейс описывает методы для регистрации нового пользователя ({@link #signUp(SignUpRequest)}) и входа существующего пользователя ({@link #signIn(SignInRequest)}).
 * Эти методы возвращают JWT-токены, используемые для дальнейшей авторизации запросов.
 */
public interface AuthenticationService {

    /**
     * Регистрация нового пользователя и создание JWT-токена.
     *
     * <p>Метод создает новую запись пользователя в БД и возвращает объект с токеном JWT,
     * используемым для дальнейшей аутентификации запросов API.</p>
     *
     * @param request Запрос на регистрацию ({@link SignUpRequest})
     * @return Объект с JWT-токеном ({@link JwtAuthenticationResponse})
     * @throws UserOperationException если пользователь уже существует
     * @throws ResourceNotFoundException если пользователь не сохранился в бд
     */
    JwtAuthenticationResponse signUp(SignUpRequest request);

    /**
     * Аутентификация существующего пользователя и выдача JWT-токена.
     *
     * <p>Проверяются переданные учетные данные пользователя. Если они верны, генерируется и возвращается JWT-токен.</p>
     *
     * @param request Запрос на вход ({@link SignInRequest})
     * @return Объект с JWT-токеном ({@link JwtAuthenticationResponse})
     */
    JwtAuthenticationResponse signIn(SignInRequest request);
}
