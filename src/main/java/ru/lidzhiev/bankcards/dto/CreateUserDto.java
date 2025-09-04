package ru.lidzhiev.bankcards.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import ru.lidzhiev.bankcards.entity.enums.UserRole;

@Setter
@Getter
public class CreateUserDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be 3-30 chars")
    private String username;

    @NotBlank
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 chars")
    private String password;

    @NotNull(message = "The role must not be null")
    private UserRole role;

    public CreateUserDto() {
    }

    public CreateUserDto(String username, String email, String password, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}