package ru.lidzhiev.bankcards.service.impl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.lidzhiev.bankcards.dto.CreateUserDto;
import ru.lidzhiev.bankcards.dto.UserDto;
import ru.lidzhiev.bankcards.entity.User;
import ru.lidzhiev.bankcards.exception.ErrorCode;
import ru.lidzhiev.bankcards.exception.ResourceNotFoundException;
import ru.lidzhiev.bankcards.exception.UserOperationException;
import ru.lidzhiev.bankcards.repository.UserRepository;
import ru.lidzhiev.bankcards.service.UserService;

/**
 * Реализация интерфейса UserService, предназначенного для работы с сущностью пользователя.
 * Поддерживает операции по созданию, загрузке, обновлению и удалению пользователей.
 * Использует шифрование паролей с помощью Spring Security PasswordEncoder.
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Загружает пользователя по имени пользователя (username).
     *
     * @param username Имя пользователя для поиска.
     * @return Объект UserDetails с информацией о загруженном пользователе.
     * @throws UsernameNotFoundException Если пользователь не найден.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    public UserDto create(CreateUserDto dto) {
        if (repository.existsByUsername(dto.getUsername())) {
            throw new UserOperationException(ErrorCode.USER_ALREADY_EXISTS);
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new UserOperationException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserRole(dto.getRole());

        User saved = repository.save(user);
        return toDto(saved);
    }

    /**
     * {@inheritDoc}
     */
    public User findEntityByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    public UserDto getByUsername(String username) {
        User user = findEntityByUsername(username);
        return toDto(user);
    }

    /**
     * {@inheritDoc}
     */
    public UserDto getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
        return toDto(user);
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateUser(UserDto dto) {
        User user = repository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        User updated = repository.save(user);
        return toDto(updated);
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
        repository.delete(user);
    }

    /**
     * {@inheritDoc}
     */
    public UserDto getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findEntityByUsername(username);
        return toDto(user);
    }

    /**
     * {@inheritDoc}
     */
    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }
}
