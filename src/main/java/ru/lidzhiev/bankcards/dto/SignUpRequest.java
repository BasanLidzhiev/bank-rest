package ru.lidzhiev.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Sign Up Request DTO")
public class SignUpRequest {

    @Schema(description = "Username", example = "Alex123")
    @Size(min = 5, max = 50, message = "Username requires 5 to 50 characters")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Schema(description = "Email", example = "Alex123@gmail.com")
    @Size(min = 5, max = 255, message = "Email requires 5 to 255 characters")
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should look like Alex123@example.com")
    private String email;

    @Schema(description = "Password", example = "my_1secret1_password")
    @Size(max = 255, message = "Password must be between 8 and 255 characters")
    private String password;

    public SignUpRequest() {
    }

    public SignUpRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

}