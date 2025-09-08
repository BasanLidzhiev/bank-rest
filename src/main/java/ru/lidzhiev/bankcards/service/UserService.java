package ru.lidzhiev.bankcards.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.lidzhiev.bankcards.dto.CreateUserDto;
import ru.lidzhiev.bankcards.dto.UserDto;
import ru.lidzhiev.bankcards.entity.User;
import ru.lidzhiev.bankcards.exception.ResourceNotFoundException;
import ru.lidzhiev.bankcards.exception.UserOperationException;

/**
 * Интерфейс для предоставления сервисов, связанных с управлением пользователями приложения.
 * Расширяет стандартный интерфейс Spring Security {@code UserDetailsService},
 * реализуя специфичные методы для работы с пользователями.
 */
public interface UserService extends UserDetailsService {
    /**
     * Создает нового пользователя.
     * Перед созданием проверяет уникальность имен пользователя и электронной почты.
     *
     * @param dto Данные нового пользователя.
     * @return Объект UserDto с информацией о новом пользователе.
     * @throws UserOperationException Если пользователь с таким именем или email уже существует.
     */
    public UserDto create(CreateUserDto dto);


    /**
     * Поиск сущности пользователя по имени пользователя.
     *
     * @param username Имя пользователя.
     * @return Объект User с полной информацией о пользователе.
     * @throws ResourceNotFoundException Если пользователь не найден.
     */
    User findEntityByUsername(String username);

    /**
     * Получает публичную информацию о пользователе по имени пользователя.
     *
     * @param username Имя пользователя.
     * @return Объект UserDto с публичной информацией о пользователе.
     * @throws ResourceNotFoundException Если пользователь не найден.
     */
    UserDto getByUsername(String username);

    /**
     * Получает публичную информацию о пользователе по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Объект UserDto с публичной информацией о пользователе.
     * @throws ResourceNotFoundException Если пользователь не найден.
     */
    UserDto getById(Long id);

    /**
     * Обновляет информацию о пользователе.
     * Может выполняться только администратором системы.
     *
     * @param dto Объект UserDto с обновленными данными пользователя.
     * @return Объект UserDto с актуальной информацией о пользователе.
     * @throws ResourceNotFoundException Если пользователь не найден.
     */
    UserDto updateUser(UserDto dto);

    /**
     * Удаляет пользователя из базы данных.
     * Может выполняться только администратором системы.
     *
     * @param id Идентификатор пользователя.
     * @throws ResourceNotFoundException Если пользователь не найден.
     */
    void deleteUser(Long id);

    /**
     * Получает текущего залогиненного пользователя.
     *
     * @return Объект UserDto с информацией о текущем пользователе.
     */
    UserDto getCurrentUser();

    /**
     * Преобразование объекта User в объект UserDto.
     *
     * @param user Объект User для конвертации.
     * @return Объект UserDto с минимальной информацией о пользователе.
     */
    UserDto toDto(User user);
}
