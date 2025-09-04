package ru.lidzhiev.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Request for user login")
public class SignInRequest {

    @Schema(description = "Username", example = "User123")
    @Size(min = 5, max = 50, message = "Username length must be between 5 and 50 characters")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Schema(description = "Password", example = "my_1secret1_password")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    @NotBlank(message = "Password cannot be empty")
    private String password;

    public SignInRequest() {
    }

    public SignInRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
