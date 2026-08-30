package com.nowadaysshop.user_service.application.config.security;

import com.nowadaysshop.user_service.config.security.JwtUtils;
import com.nowadaysshop.user_service.domain.service.AuthService;
import com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.entity.UserEntity;
import com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.repository.SpringDataUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Testy jednostkowe klasy AuthService")
public class AuthServiceTest {

    @Mock
    private SpringDataUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private final String testEmail = "jan.kowalski@example.com";
    private final String rawPassword = "tajneHaslo123";
    private final String encodedPassword = "hash_tajneHaslo123";
    private final UUID userId = UUID.randomUUID();

    @Nested
    @DisplayName("Testy rejestrujące")
    class RegisterTests{

        @Test
        void shouldRegisterUserSuccessfullyTest(){
            when(userRepository.existsByEmail(testEmail)).thenReturn(false  );
            when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
            String result = authService.register(testEmail,"Jan","Kowalski",rawPassword);
            assertThat(result).isEqualTo("Użytkownik zarejestrowany pomyślnie!");
            verify(passwordEncoder,times(1)).encode(rawPassword);
            ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository,times(1)).save(userEntityCaptor.capture());
            UserEntity savedEntity = userEntityCaptor.getValue();
            assertThat(savedEntity.getEmail()).isEqualTo(testEmail);
            assertThat(savedEntity.getFirstName()).isEqualTo("Jan");
            assertThat(savedEntity.getLastName()).isEqualTo("Kowalski");
            assertThat(savedEntity.getPassword()).isEqualTo(encodedPassword);
            assertThat(savedEntity.getRole()).isEqualTo("ROLE_USER");

        }
        @Test

        void shouldThrowExceptionWhenEmailAlreadyExistsTest() {
            // GIVEN
            when(userRepository.existsByEmail(testEmail)).thenReturn(true);


            assertThatThrownBy(() -> authService.register(testEmail, "Jan", "Kowalski", rawPassword))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Użytkownik o podanym adresie e-mail już istnieje!");


            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testy logujące")
    class LoginTests {

        private UserEntity existingUser;

        @BeforeEach
        void setUp() {
            existingUser = new UserEntity();
            existingUser.setId(userId);
            existingUser.setEmail(testEmail);
            existingUser.setPassword(encodedPassword);
            existingUser.setRole("ROLE_USER");
        }

        @Test
        void shouldLoginUserSuccessfullyTest() {

            String expectedToken = "mocked.jwt.token";
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
            when(jwtUtils.generateToken(userId, "ROLE_USER")).thenReturn(expectedToken);


            String token = authService.login(testEmail, rawPassword);


            assertThat(token).isEqualTo(expectedToken);
            verify(jwtUtils, times(1)).generateToken(userId, "ROLE_USER");
        }

        @Test

        void shouldThrowExceptionWhenUserNotFoundTest() {

            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());


            assertThatThrownBy(() -> authService.login(testEmail, rawPassword))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Nieprawidłowy e-mail lub hasło");

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtUtils, never()).generateToken(any(), any());
        }

        @Test

        void shouldThrowExceptionWhenPasswordIsIncorrectTest() {

            String wrongPassword = "złeHasło";
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(wrongPassword, encodedPassword)).thenReturn(false);


            assertThatThrownBy(() -> authService.login(testEmail, wrongPassword))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Nieprawidłowy e-mail lub hasło");

            verify(jwtUtils, never()).generateToken(any(), any());
        }
    }
}


