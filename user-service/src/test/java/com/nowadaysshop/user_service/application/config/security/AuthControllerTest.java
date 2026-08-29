package com.nowadaysshop.user_service.application.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowadaysshop.user_service.domain.service.AuthService;
import com.nowadaysshop.user_service.infrastructure.adapters.in.rest.AuthController;
import com.nowadaysshop.user_service.infrastructure.adapters.in.web.dto.LoginRequest;
import com.nowadaysshop.user_service.infrastructure.adapters.in.web.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testy warstwy REST dla AuthController")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private final String testEmail = "jan.kowalski@example.com";
    private final String testPassword = "Haslo123!";

    @Nested
    @DisplayName("Testy endpointu POST /api/users/register")
    class RegisterEndpointTests {

        @Test

        void shouldReturn200OkOnSuccessfulRegistrationTest() throws Exception {

            RegisterRequest request = new RegisterRequest(testEmail, "Jan", "Kowalski", testPassword);
            String successMessage = "Użytkownik zarejestrowany pomyślnie!";

            given(authService.register(testEmail, "Jan", "Kowalski", testPassword))
                    .willReturn(successMessage);


            mockMvc.perform(post("/api/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(successMessage));

            verify(authService).register(testEmail, "Jan", "Kowalski", testPassword);
        }

        @Test

        void shouldReturnErrorWhenUserAlreadyExistsTest() throws Exception {

            RegisterRequest request = new RegisterRequest(testEmail, "Jan", "Kowalski", testPassword);

            given(authService.register(anyString(), anyString(), anyString(), anyString()))
                    .willThrow(new IllegalArgumentException("Użytkownik o podanym adresie e-mail już istnieje!"));


            mockMvc.perform(post("/api/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Testy endpointu POST /api/users/login")
    class LoginEndpointTests {

        @Test

        void shouldReturn200OkAndTokenOnSuccessfulLoginTest() throws Exception {

            LoginRequest request = new LoginRequest(testEmail, testPassword);
            String mockToken = "eyJhbGciOiJIUzI1NiJ9.mockTokenJWT";

            given(authService.login(testEmail, testPassword)).willReturn(mockToken);


            mockMvc.perform(post("/api/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value(mockToken))
                    .andExpect(jsonPath("$.email").value(testEmail));

            verify(authService).login(testEmail, testPassword);
        }

        @Test

        void shouldReturnErrorOnInvalidCredentialsTest() throws Exception {

            LoginRequest request = new LoginRequest(testEmail, "zleHaslo");

            given(authService.login(testEmail, "zleHaslo"))
                    .willThrow(new IllegalArgumentException("Nieprawidłowy e-mail lub hasło"));


            mockMvc.perform(post("/api/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError());
        }
    }
}

