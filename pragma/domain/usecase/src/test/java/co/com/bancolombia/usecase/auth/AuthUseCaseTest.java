package co.com.bancolombia.usecase.auth;

import co.com.bancolombia.model.auth.Auth;
import co.com.bancolombia.model.constants.ValidationMessages;
import co.com.bancolombia.model.exception.AuthException;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private BiFunction<UUID, UUID, String> tokenGenerator;

    @Mock
    private BiFunction<String, String, Boolean> passwordMatches;

    @InjectMocks
    private AuthUseCase authUseCase;

    private User testUser;
    private String testEmail;
    private String testPassword;
    private String testHashedPassword;

    @BeforeEach
    void setUp() {
        testEmail = "juan@test.com";
        testPassword = "password123";
        testHashedPassword = "$2a$10$hashedpassword";

        testUser = User.builder()
                .id("550e8400-e29b-41d4-a716-446655440000")
                .name("Juan")
                .lastName("Perez")
                .emailAddress(testEmail)
                .baseSalary(new BigDecimal("2000000"))
                .idRol("b71ed6c9-1dd9-4c14-8a4a-fe06166d5cdb")
                .password(testHashedPassword)
                .build();
    }

    @Test
    @DisplayName("Login successful - valid credentials")
    void loginSuccessful() {
        String expectedToken = "jwt-token-123";

        when(userUseCase.getUserByEmailAddress(testEmail)).thenReturn(Mono.just(testUser));
        when(passwordMatches.apply(testPassword, testHashedPassword)).thenReturn(true);
        when(tokenGenerator.apply(UUID.fromString(testUser.getId()), UUID.fromString(testUser.getIdRol())))
                .thenReturn(expectedToken);

        StepVerifier.create(authUseCase.login(testEmail, testPassword, tokenGenerator, passwordMatches))
                .expectNextMatches(auth ->
                    auth.getIdUser().equals(UUID.fromString(testUser.getId())) &&
                    auth.getIdRole().equals(UUID.fromString(testUser.getIdRol())) &&
                    auth.getToken().equals(expectedToken) &&
                    auth.getNameUser().equals(testUser.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Login fails - user not found")
    void loginFailsUserNotFound() {
        when(userUseCase.getUserByEmailAddress(testEmail)).thenReturn(Mono.empty());

        StepVerifier.create(authUseCase.login(testEmail, testPassword, tokenGenerator, passwordMatches))
                .expectErrorMatches(error ->
                    error instanceof AuthException &&
                    error.getMessage().equals(ValidationMessages.USER_NOT_FOUND_LOGIN + testEmail)
                )
                .verify();
    }

    @Test
    @DisplayName("Login fails - invalid password")
    void loginFailsInvalidPassword() {
        when(userUseCase.getUserByEmailAddress(testEmail)).thenReturn(Mono.just(testUser));
        when(passwordMatches.apply(testPassword, testHashedPassword)).thenReturn(false);

        StepVerifier.create(authUseCase.login(testEmail, testPassword, tokenGenerator, passwordMatches))
                .expectErrorMatches(error ->
                    error instanceof AuthException &&
                    error.getMessage().equals(ValidationMessages.INVALID_CREDENTIALS)
                )
                .verify();
    }

    @Test
    @DisplayName("Login fails - user service error")
    void loginFailsUserServiceError() {
        RuntimeException serviceError = new RuntimeException("Database connection error");
        when(userUseCase.getUserByEmailAddress(testEmail)).thenReturn(Mono.error(serviceError));

        StepVerifier.create(authUseCase.login(testEmail, testPassword, tokenGenerator, passwordMatches))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Login with null email")
    void loginWithNullEmail() {
        when(userUseCase.getUserByEmailAddress(null)).thenReturn(Mono.empty());

        StepVerifier.create(authUseCase.login(null, testPassword, tokenGenerator, passwordMatches))
                .expectErrorMatches(error ->
                    error instanceof AuthException &&
                    error.getMessage().contains(ValidationMessages.USER_NOT_FOUND_LOGIN)
                )
                .verify();
    }

    @Test
    @DisplayName("Login with empty email")
    void loginWithEmptyEmail() {
        String emptyEmail = "";
        when(userUseCase.getUserByEmailAddress(emptyEmail)).thenReturn(Mono.empty());

        StepVerifier.create(authUseCase.login(emptyEmail, testPassword, tokenGenerator, passwordMatches))
                .expectErrorMatches(error ->
                    error instanceof AuthException &&
                    error.getMessage().equals(ValidationMessages.USER_NOT_FOUND_LOGIN + emptyEmail)
                )
                .verify();
    }

    @Test
    @DisplayName("Login with null password")
    void loginWithNullPassword() {
        when(userUseCase.getUserByEmailAddress(testEmail)).thenReturn(Mono.just(testUser));
        when(passwordMatches.apply(null, testHashedPassword)).thenReturn(false);

        StepVerifier.create(authUseCase.login(testEmail, null, tokenGenerator, passwordMatches))
                .expectErrorMatches(error ->
                    error instanceof AuthException &&
                    error.getMessage().equals(ValidationMessages.INVALID_CREDENTIALS)
                )
                .verify();
    }

    @Test
    @DisplayName("Login with empty password")
    void loginWithEmptyPassword() {
        String emptyPassword = "";
        when(userUseCase.getUserByEmailAddress(testEmail)).thenReturn(Mono.just(testUser));
        when(passwordMatches.apply(emptyPassword, testHashedPassword)).thenReturn(false);

        StepVerifier.create(authUseCase.login(testEmail, emptyPassword, tokenGenerator, passwordMatches))
                .expectErrorMatches(error ->
                    error instanceof AuthException &&
                    error.getMessage().equals(ValidationMessages.INVALID_CREDENTIALS)
                )
                .verify();
    }

    @Test
    @DisplayName("Login successful with different role")
    void loginSuccessfulDifferentRole() {
        User adminUser = testUser.toBuilder()
                .idRol("80e86d27-20a4-44be-b90d-44eeb378d409")
                .name("Admin")
                .build();

        String expectedToken = "admin-jwt-token-456";

        when(userUseCase.getUserByEmailAddress(testEmail)).thenReturn(Mono.just(adminUser));
        when(passwordMatches.apply(testPassword, testHashedPassword)).thenReturn(true);
        when(tokenGenerator.apply(UUID.fromString(adminUser.getId()), UUID.fromString(adminUser.getIdRol())))
                .thenReturn(expectedToken);

        StepVerifier.create(authUseCase.login(testEmail, testPassword, tokenGenerator, passwordMatches))
                .expectNextMatches(auth ->
                    auth.getIdUser().equals(UUID.fromString(adminUser.getId())) &&
                    auth.getIdRole().equals(UUID.fromString(adminUser.getIdRol())) &&
                    auth.getToken().equals(expectedToken) &&
                    auth.getNameUser().equals(adminUser.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Login successful with minimum valid data")
    void loginSuccessfulMinimumData() {
        User minimalUser = User.builder()
                .id("550e8400-e29b-41d4-a716-446655440003")
                .name("Min")
                .lastName("User")
                .emailAddress("min@test.com")
                .baseSalary(new BigDecimal("1000000"))
                .idRol("3a371249-a1f0-4eb3-b06c-5a670ab6eca9")
                .password("hash")
                .build();

        String expectedToken = "min-token";

        when(userUseCase.getUserByEmailAddress("min@test.com")).thenReturn(Mono.just(minimalUser));
        when(passwordMatches.apply("pass", "hash")).thenReturn(true);
        when(tokenGenerator.apply(UUID.fromString(minimalUser.getId()), UUID.fromString(minimalUser.getIdRol())))
                .thenReturn(expectedToken);

        StepVerifier.create(authUseCase.login("min@test.com", "pass", tokenGenerator, passwordMatches))
                .expectNextMatches(auth ->
                    auth.getIdUser().equals(UUID.fromString(minimalUser.getId())) &&
                    auth.getIdRole().equals(UUID.fromString(minimalUser.getIdRol())) &&
                    auth.getToken().equals(expectedToken) &&
                    auth.getNameUser().equals(minimalUser.getName())
                )
                .verifyComplete();
    }
}