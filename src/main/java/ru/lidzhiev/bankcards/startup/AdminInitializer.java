package ru.lidzhiev.bankcards.startup;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.lidzhiev.bankcards.dto.CreateUserDto;
import ru.lidzhiev.bankcards.entity.enums.UserRole;
import ru.lidzhiev.bankcards.repository.UserRepository;
import ru.lidzhiev.bankcards.service.impl.UserService;

@RequiredArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.existsByUsername(adminUsername)) {
            return;
        }
        CreateUserDto userCreateDTO = new CreateUserDto();
        userCreateDTO.setUsername(adminUsername);
        userCreateDTO.setPassword(adminPassword);
        userCreateDTO.setEmail(adminEmail);
        userCreateDTO.setRole(UserRole.ROLE_ADMIN);

        userService.create(userCreateDTO);
    }
}