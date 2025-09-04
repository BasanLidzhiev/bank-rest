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
import ru.lidzhiev.bankcards.repository.UserRepository;
import ru.lidzhiev.bankcards.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public UserDto create(CreateUserDto dto) {
        if (repository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("User with this username already exists");
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserRole(dto.getRole());

        User saved = repository.save(user);
        return toDto(saved);
    }

    // get entity by username for JWT
    public User findEntityByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    // get username for controller
    public UserDto getByUsername(String username) {
        User user = findEntityByUsername(username);
        return toDto(user);
    }

    public UserDto getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toDto(user);
    }

    // Only ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateUser(UserDto dto) {
        User user = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getId()));
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        User updated = repository.save(user);
        return toDto(updated);
    }

    // only ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        repository.delete(user);
    }

    public UserDto getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findEntityByUsername(username);
        return toDto(user);
    }

    //Converter method to convert User entity to UserDto
    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }
}
