package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.exception.InvalidDataException;
import co.com.bancolombia.model.exception.UserExistsException;
import co.com.bancolombia.model.role.gateways.RoleRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("Juan")
                .lastName("Perez")
                .emailAddress("juan@test.com")
                .baseSalary(new BigDecimal("2000000"))
                .idRol("b71ed6c9-1dd9-4c14-8a4a-fe06166d5cdb")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("Save user - valid data should work")
    void saveUser_WithValidData_ShouldWork() {
        when(roleRepository.existsById("b71ed6c9-1dd9-4c14-8a4a-fe06166d5cdb")).thenReturn(Mono.just(true));
        when(userRepository.emailAddressExists("juan@test.com")).thenReturn(Mono.just(false));

        User savedUser = testUser.toBuilder().id("123").build();
        when(userRepository.createUser(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(userUseCase.saveUser(testUser))
                .expectNextMatches(user ->
                    user.getId().equals("123") &&
                    user.getName().equals("Juan"))
                .verifyComplete();

        verify(userRepository).createUser(any(User.class));
    }

    @Test
    @DisplayName("Save user - duplicate email should fail")
    void saveUser_WithDuplicateEmail_ShouldFail() {
        when(roleRepository.existsById("b71ed6c9-1dd9-4c14-8a4a-fe06166d5cdb")).thenReturn(Mono.just(true));
        when(userRepository.emailAddressExists("juan@test.com"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.saveUser(testUser))
                .expectError(UserExistsException.class)
                .verify();

        verify(userRepository, never()).createUser(any(User.class));
    }

    @Test
    @DisplayName("Get all users - should work")
    void getAllUsers_ShouldWork() {
        when(userRepository.findAll())
                .thenReturn(Flux.just(testUser));

        StepVerifier.create(userUseCase.getAllUsers())
                .expectNextMatches(user -> user.getName().equals("Juan"))
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Save user - invalid role should fail")
    void saveUser_WithInvalidRole_ShouldFail() {
        when(userRepository.emailAddressExists("juan@test.com")).thenReturn(Mono.just(false));
        when(roleRepository.existsById("b71ed6c9-1dd9-4c14-8a4a-fe06166d5cdb")).thenReturn(Mono.just(false));

        StepVerifier.create(userUseCase.saveUser(testUser))
                .expectError(InvalidDataException.class)
                .verify();

        verify(userRepository, never()).createUser(any(User.class));
    }


    @Test
    @DisplayName("Get user by email - should work")
    void getUserByEmailAddress_ShouldWork() {
        User userWithId = testUser.toBuilder().id("123").build();
        when(userRepository.getUserByEmailAddress("juan@test.com"))
                .thenReturn(Mono.just(userWithId));

        StepVerifier.create(userUseCase.getUserByEmailAddress("juan@test.com"))
                .expectNextMatches(user ->
                    user.getId().equals("123") &&
                    user.getEmailAddress().equals("juan@test.com"))
                .verifyComplete();

        verify(userRepository).getUserByEmailAddress("juan@test.com");
    }

    @Test
    @DisplayName("Get user by email - not found should fail")
    void getUserByEmailAddress_NotFound_ShouldFail() {
        when(userRepository.getUserByEmailAddress("notfound@test.com"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.getUserByEmailAddress("notfound@test.com"))
                .expectError(UserExistsException.class)
                .verify();

        verify(userRepository).getUserByEmailAddress("notfound@test.com");
    }

    @Test
    @DisplayName("Get user by ID - should work")
    void getUserById_ShouldWork() {
        User userWithId = testUser.toBuilder().id("123").build();
        when(userRepository.getUserById("123"))
                .thenReturn(Mono.just(userWithId));

        StepVerifier.create(userUseCase.getUserById("123"))
                .expectNextMatches(user ->
                    user.getId().equals("123") &&
                    user.getName().equals("Juan"))
                .verifyComplete();

        verify(userRepository).getUserById("123");
    }

    @Test
    @DisplayName("Get user by ID - not found should fail")
    void getUserById_NotFound_ShouldFail() {
        when(userRepository.getUserById("999"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.getUserById("999"))
                .expectError(UserExistsException.class)
                .verify();

        verify(userRepository).getUserById("999");
    }

    @Test
    @DisplayName("Update user - should work")
    void updateUser_ShouldWork() {
        String userId = "123";
        User existingUser = testUser.toBuilder().id(userId).build();
        User updatedUser = existingUser.toBuilder().name("Juan Carlos").build();

        when(userRepository.getUserById(userId)).thenReturn(Mono.just(existingUser));
        when(roleRepository.existsById("b71ed6c9-1dd9-4c14-8a4a-fe06166d5cdb")).thenReturn(Mono.just(true));
        when(userRepository.updateUser(any(User.class))).thenReturn(Mono.just(updatedUser));

        StepVerifier.create(userUseCase.updateUser(userId, testUser.toBuilder().name("Juan Carlos").build()))
                .expectNextMatches(user ->
                    user.getId().equals(userId) &&
                    user.getName().equals("Juan Carlos"))
                .verifyComplete();

        verify(userRepository).getUserById(userId);
        verify(userRepository).updateUser(any(User.class));
        verify(roleRepository).existsById("b71ed6c9-1dd9-4c14-8a4a-fe06166d5cdb");
    }

    @Test
    @DisplayName("Update user - not found should fail")
    void updateUser_NotFound_ShouldFail() {
        String userId = "999";
        when(userRepository.getUserById(userId)).thenReturn(Mono.empty());
        when(roleRepository.existsById("b71ed6c9-1dd9-4c14-8a4a-fe06166d5cdb")).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.updateUser(userId, testUser))
                .expectError(UserExistsException.class)
                .verify();

        verify(userRepository).getUserById(userId);
        verify(userRepository, never()).updateUser(any(User.class));
    }

    @Test
    @DisplayName("Delete user - should work")
    void deleteUser_ShouldWork() {
        String userId = "123";
        User existingUser = testUser.toBuilder().id(userId).build();

        when(userRepository.getUserById(userId)).thenReturn(Mono.just(existingUser));
        when(userRepository.deleteById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.deleteUser(userId))
                .verifyComplete();

        verify(userRepository).getUserById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Delete user - not found should fail")
    void deleteUser_NotFound_ShouldFail() {
        String userId = "999";
        when(userRepository.getUserById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.deleteUser(userId))
                .expectError(UserExistsException.class)
                .verify();

        verify(userRepository).getUserById(userId);
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Get all users - error should fail")
    void getAllUsers_Error_ShouldFail() {
        when(userRepository.findAll())
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        StepVerifier.create(userUseCase.getAllUsers())
                .expectError(InvalidDataException.class)
                .verify();

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Get user by email - interface method should work")
    void getUserByEmail_InterfaceMethod_ShouldWork() {
        User userWithId = testUser.toBuilder().id("123").build();
        when(userRepository.getUserByEmailAddress("juan@test.com"))
                .thenReturn(Mono.just(userWithId));

        StepVerifier.create(userUseCase.getUserByEmail("juan@test.com"))
                .expectNextMatches(user ->
                    user.getId().equals("123") &&
                    user.getEmailAddress().equals("juan@test.com"))
                .verifyComplete();

        verify(userRepository).getUserByEmailAddress("juan@test.com");
    }

    @Test
    @DisplayName("Find by ID - interface method should work")
    void findById_InterfaceMethod_ShouldWork() {
        User userWithId = testUser.toBuilder().id("123").build();
        when(userRepository.getUserById("123"))
                .thenReturn(Mono.just(userWithId));

        StepVerifier.create(userUseCase.findById(123L))
                .expectNextMatches(user ->
                    user.getId().equals("123") &&
                    user.getName().equals("Juan"))
                .verifyComplete();

        verify(userRepository).getUserById("123");
    }

    @Test
    @DisplayName("Find all - interface method should work")
    void findAll_InterfaceMethod_ShouldWork() {
        when(userRepository.findAll())
                .thenReturn(Flux.just(testUser));

        StepVerifier.create(userUseCase.findAll())
                .expectNextMatches(user -> user.getName().equals("Juan"))
                .verifyComplete();

        verify(userRepository).findAll();
    }
}