package ru.lidzhiev.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.lidzhiev.bankcards.dto.JwtAuthenticationResponse;
import ru.lidzhiev.bankcards.dto.SignInRequest;
import ru.lidzhiev.bankcards.dto.SignUpRequest;
import ru.lidzhiev.bankcards.security.JwtService;
import ru.lidzhiev.bankcards.service.AuthenticationService;
import ru.lidzhiev.bankcards.service.impl.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_ReturnsJwtResponse_WhenCredentialsAreValid() throws Exception {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("alex12");
        signInRequest.setPassword("password123");

        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("mocked-jwt-token");

        Mockito.when(authenticationService.signIn(Mockito.any(SignInRequest.class)))
                .thenReturn(jwtResponse);

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));

    }

    @Test
    void signup_ReturnsJwtResponse_WhenCredentialsAreValid() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("validUser");
        signUpRequest.setEmail("valid@email.com");
        signUpRequest.setPassword("valid_password");

        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("mocked-signup-jwt-token");

        Mockito.when(authenticationService.signUp(Mockito.any(SignUpRequest.class)))
                .thenReturn(jwtResponse);

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-signup-jwt-token"));
    }
}
