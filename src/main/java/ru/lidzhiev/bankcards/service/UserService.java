package ru.lidzhiev.bankcards.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.lidzhiev.bankcards.dto.CreateUserDto;
import ru.lidzhiev.bankcards.dto.UserDto;
import ru.lidzhiev.bankcards.entity.User;

public interface UserService extends UserDetailsService {
    public UserDto create(CreateUserDto dto);
    User findEntityByUsername(String username);
    UserDto getByUsername(String username);
    UserDto getById(Long id);
    UserDto updateUser(UserDto dto);
    void deleteUser(Long id);
    UserDto getCurrentUser();
    UserDto toDto(User user);
}
