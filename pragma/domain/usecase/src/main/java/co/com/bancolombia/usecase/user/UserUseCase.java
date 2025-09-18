package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.constants.ValidationMessages;
import co.com.bancolombia.model.exception.InvalidDataException;
import co.com.bancolombia.model.exception.UserExistsException;
import co.com.bancolombia.model.role.gateways.RoleRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.user.interfaces.IUserUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class UserUseCase implements IUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Mono<User> saveUser(User user) {
        return Mono.fromRunnable(() -> validateData(user))
                .then(confirmEmailNotRegistered(user.getEmailAddress()))
                .then(confirmRoleExists(user.getIdRol()))
                .then(Mono.fromCallable(() -> asignarId(user)))
                .flatMap(this::createUser);
    }

    public Mono<User> updateUser(String id, User usuario) {
        return Mono.fromRunnable(() -> validateData(usuario))
                .then(userRepository.getUserById(id))
                .switchIfEmpty(Mono.error(new UserExistsException(id)))
                .then(confirmRoleExists(usuario.getIdRol()))
                .then(Mono.fromCallable(() -> usuario.toBuilder().id(id).build()))
                .flatMap(userRepository::updateUser);
    }

    private void validateData(User user) {
        try {
            user.validateData();
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    private Mono<Void> confirmEmailNotRegistered(String emailAddress) {
        return userRepository.emailAddressExists(emailAddress)
                .flatMap(existe -> {
                    if (existe) {
                        return Mono.error(new UserExistsException(emailAddress));
                    }
                    return Mono.empty();
                });
    }

    public Mono<User> getUserByEmailAddress(String email_address) {
        return userRepository.getUserByEmailAddress(email_address)
                .switchIfEmpty(Mono.error(new UserExistsException(ValidationMessages.USER_NOT_FOUND_EMAIL + email_address)))
                .onErrorMap(error -> {
                    if (error instanceof UserExistsException) {
                        return error;
                    }
                    return new InvalidDataException(ValidationMessages.ERROR_GETTING_USER, error);
                });
    }

    private Mono<Void> confirmRoleExists(String idRol) {
        return roleRepository.existsById(idRol)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new InvalidDataException(ValidationMessages.ROLE_NOT_EXISTS + idRol + ValidationMessages.ROLE_NOT_EXISTS_SUFFIX));
                    }
                    return Mono.empty();
                });
    }

    private User asignarId(User user) {
        return user.toBuilder()
                .id(UUID.randomUUID().toString())
                .build();
    }

    private Mono<User> createUser(User user) {
        return userRepository.createUser(user)
                .onErrorMap(error ->
                        new InvalidDataException(ValidationMessages.ERROR_SAVING_USER, error)
                );
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll()
                .onErrorMap(error ->
                        new InvalidDataException(ValidationMessages.ERROR_GETTING_USERS, error)
                );
    }

    public Mono<User> getUserById(String id) {
        return userRepository.getUserById(id)
                .switchIfEmpty(Mono.error(new UserExistsException(ValidationMessages.USER_NOT_FOUND_ID + id)))
                .onErrorMap(error -> {
                    if (error instanceof UserExistsException) {
                        return error;
                    }
                    return new InvalidDataException(ValidationMessages.ERROR_GETTING_USER, error);
                });
    }

    @Override
    public Mono<User> getUserByEmail(String email) {
        return getUserByEmailAddress(email);
    }

    /*
        public Mono<Void> deleteUser(String id) {
            return userRepository.getUserById(id)
                    .switchIfEmpty(Mono.error(new UserExistsException("Usuario no encontrado con ID: " + id)))
                    .then(userRepository.deleteById(id))
                    .onErrorMap(error -> {
                        if (error instanceof UserExistsException) {
                            return error;
                        }
                        return new InvalidDataException(ValidationMessages.ERROR_DELETING_USER, error);
                    });
        }*/
    public Mono<Void> deleteUser(String id) {
        return userRepository.getUserById(id)
                .switchIfEmpty(Mono.error(new UserExistsException(ValidationMessages.USER_NOT_FOUND_ID
                        + id)))
                .flatMap(user -> userRepository.deleteById(id))
                .onErrorMap(error -> {
                    if (error instanceof UserExistsException) {
                        return error;
                    }
                    return new InvalidDataException(ValidationMessages.ERROR_DELETING_USER, error);
                });
    }

    @Override
    public Mono<User> findById(Long id) {
        return getUserById(String.valueOf(id));
    }

    @Override
    public Flux<User> findAll() {
        return getAllUsers();
    }
}
