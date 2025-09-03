package co.com.pragma.usecase.user;

import co.com.pragma.model.exception.ModelExceptions;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User testUser;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testUser = User.builder()
                .id(testId.toString())
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .documentNumber("12345678")
                .phone("1234567890")
                .idRol("1")
                .basicSalary(5000000)
                .build();
    }

    @Test
    void getBy_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(testId)).thenReturn(Mono.just(testUser));

        // When & Then
        StepVerifier.create(userUseCase.getBy(testId))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void getBy_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Given
        when(userRepository.findById(testId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userUseCase.getBy(testId))
                .verifyComplete();
    }

    @Test
    void findByEmail_ShouldReturnUsers_WhenUsersExist() {
        // Given
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Flux.just(testUser));

        // When & Then
        StepVerifier.create(userUseCase.findByEmail(email))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenNoUsersExist() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(userUseCase.findByEmail(email))
                .verifyComplete();
    }

    @Test
    void create_ShouldSaveUser_WhenValidUserAndEmailDoesNotExist() {
        // Given
        when(userRepository.existUserByEmail(testUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));

        // When & Then
        StepVerifier.create(userUseCase.create(testUser))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void create_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        when(userRepository.existUserByEmail(testUser.getEmail())).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(userUseCase.create(testUser))
                .expectError(ModelExceptions.ExistEmailException.class)
                .verify();
    }

    @Test
    void create_ShouldThrowException_WhenBasicSalaryIsNegative() {
        // Given
        User invalidUser = testUser.toBuilder().basicSalary(-1000).build();

        // When & Then
        StepVerifier.create(userUseCase.create(invalidUser))
                .expectError(ModelExceptions.BasedSalaryNotValidException.class)
                .verify();
    }

    @Test
    void create_ShouldThrowException_WhenBasicSalaryExceedsLimit() {
        // Given
        User invalidUser = testUser.toBuilder().basicSalary(16000000).build();

        // When & Then
        StepVerifier.create(userUseCase.create(invalidUser))
                .expectError(ModelExceptions.BasedSalaryNotValidException.class)
                .verify();
    }

    @Test
    void create_ShouldSaveUser_WhenBasicSalaryIsZero() {
        // Given
        User validUser = testUser.toBuilder().basicSalary(0).build();
        when(userRepository.existUserByEmail(validUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(validUser));

        // When & Then
        StepVerifier.create(userUseCase.create(validUser))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void create_ShouldSaveUser_WhenBasicSalaryIsMaximumAllowed() {
        // Given
        User validUser = testUser.toBuilder().basicSalary(15000000).build();
        when(userRepository.existUserByEmail(validUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(validUser));

        // When & Then
        StepVerifier.create(userUseCase.create(validUser))
                .expectNext(validUser)
                .verifyComplete();
    }
}
