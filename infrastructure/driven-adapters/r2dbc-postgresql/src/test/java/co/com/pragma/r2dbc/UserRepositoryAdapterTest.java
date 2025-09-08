package co.com.pragma.r2dbc;

import co.com.pragma.model.exception.ModelExceptions;
import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.builder.TestUserBuilder;
import co.com.pragma.r2dbc.builder.TestUserEntityBuilder;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inicializamos el adaptador con los mocks
        adapter = Mockito.spy(new UserRepositoryAdapter(repository, mapper));
    }

    @Test
    void testSave_Success() {
        // Arrange
        User user = new TestUserBuilder().build();
        UserEntity userEntity = new TestUserEntityBuilder()
                .withId(UUID.fromString(user.getId()))
                .build();
        UserEntity savedEntity = new TestUserEntityBuilder()
                .withId(UUID.fromString(user.getId()))
                .build();
        
        when(mapper.map(any(User.class), eq(UserEntity.class))).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(savedEntity));
        when(mapper.map(any(UserEntity.class), eq(User.class))).thenReturn(user);

        // Act & Assert
        StepVerifier.create(adapter.save(user))
                .expectNextMatches(savedUser -> savedUser.getId().equals(user.getId()))
                .verifyComplete();

        verify(mapper).map(user, UserEntity.class);
        verify(repository).save(userEntity);
        verify(mapper).map(savedEntity, User.class);
    }

    @Test
    void testSave_WhenEmailExists_ShouldThrowException() {
        // Arrange
        User user = new TestUserBuilder().build();
        UserEntity userEntity = new TestUserEntityBuilder()
                .withId(UUID.fromString(user.getId()))
                .build();
        
        when(mapper.map(any(User.class), eq(UserEntity.class))).thenReturn(userEntity);
        when(repository.save(userEntity))
                .thenReturn(Mono.error(new DataIntegrityViolationException("Duplicate email")));

        // Act & Assert
        StepVerifier.create(adapter.save(user))
                .expectErrorSatisfies(throwable -> {
                    assertTrue(throwable instanceof ModelExceptions.ExistEmailException);
                    assertEquals("Email is already registred", throwable.getMessage());
                })
                .verify();
    }

    @Test
    void testFindById_Success() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        User expectedUser = new TestUserBuilder().withId(userId).build();
        UserEntity userEntity = new TestUserEntityBuilder()
                .withId(UUID.fromString(userId))
                .build();
        
        when(repository.findById(UUID.fromString(userId))).thenReturn(Mono.just(userEntity));
        when(mapper.map(any(UserEntity.class), eq(User.class))).thenReturn(expectedUser);

        // Act & Assert
        StepVerifier.create(adapter.findById(UUID.fromString(userId)))
                .expectNextMatches(user -> user.getId().equals(userId))
                .verifyComplete();

        verify(repository).findById(UUID.fromString(userId));
        verify(mapper).map(userEntity, User.class);
    }

    @Test
    void testFindById_WhenUserNotFound_ShouldReturnEmpty() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(repository.findById(userId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.findById(userId))
                .verifyComplete();

        verify(repository).findById(userId);
        verify(mapper, never()).map(any(), eq(User.class));
    }

    @Test
    void testExistUserByEmail_WhenEmailExists_ShouldReturnTrue() {
        // Arrange
        String email = "test@example.com";
        UserEntity userEntity = new TestUserEntityBuilder().withEmail(email).build();
        User user = new TestUserBuilder().withEmail(email).build();
        
        when(repository.findByEmail(email)).thenReturn(Flux.just(userEntity));
        when(mapper.map(any(UserEntity.class), eq(User.class))).thenReturn(user);

        // Act & Assert
        StepVerifier.create(adapter.existUserByEmail(email))
                .expectNext(true)
                .verifyComplete();
                
        verify(repository).findByEmail(email);
        verify(mapper).map(userEntity, User.class);
    }

    @Test
    void testExistUserByEmail_WhenEmailDoesNotExist_ShouldReturnFalse() {
        // Arrange
        String email = "nonexistent@example.com";
        when(repository.findByEmail(email)).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(adapter.existUserByEmail(email))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void testFindByEmail_Success() {
        // Arrange
        String email = "test@example.com";
        User expectedUser = new TestUserBuilder().withEmail(email).build();
        UserEntity userEntity = new TestUserEntityBuilder().withEmail(email).build();
        
        when(repository.findByEmail(email)).thenReturn(Flux.just(userEntity));
        when(mapper.map(any(UserEntity.class), eq(User.class))).thenReturn(expectedUser);

        // Act & Assert
        StepVerifier.create(adapter.findByEmail(email))
                .expectNextMatches(user -> user.getEmail().equals(email))
                .verifyComplete();

        verify(repository).findByEmail(email);
        verify(mapper).map(userEntity, User.class);
    }

    @Test
    void testFindByEmail_WhenNoUserFound_ShouldReturnEmpty() {
        // Arrange
        String email = "nonexistent@example.com";
        when(repository.findByEmail(email)).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(adapter.findByEmail(email))
                .verifyComplete();

        verify(repository).findByEmail(email);
        verify(mapper, never()).map(any(), eq(User.class));
    }
}