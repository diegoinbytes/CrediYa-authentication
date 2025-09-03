package co.com.pragma.api;

import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, HandlerV1.class})
@WebFluxTest
class RouterRestTest {

    /*@Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserUseCase userUseCase;
    @MockBean
    private TransactionalOperator transactionalOperator;
    @MockBean
    private ObjectMapper objectMapper;
    @MockBean
    private Validator validator;

    // Clase Builder estática para crear objetos User de prueba
    private static class TestUserBuilder {
        private String id = UUID.randomUUID().toString();
        private String email = "testuser@mail.com";
        private String name = "Test";
        private String lastName = "User";
        private String documentNumber = "123456789";
        private String phone = "1234567890";
        private String idRol = "1";
        private int basicSalary = 1000;

        public TestUserBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public TestUserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public TestUserBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public User build() {
            return User.builder()
                    .id(this.id)
                    .email(this.email)
                    .name(this.name)
                    .lastName(this.lastName)
                    .documentNumber(this.documentNumber)
                    .phone(this.phone)
                    .idRol(this.idRol)
                    .basicSalary(this.basicSalary)
                    .build();
        }

        public UserDTO buildDTO() {
            // Se actualiza el constructor de UserDTO para incluir todos los campos.
            return new UserDTO(this.email, this.name, this.lastName, this.email, this.documentNumber, this.phone, this.idRol, this.basicSalary);
        }
    }

    // --- Pruebas para GET /api/v1/users/{id} ---

    @Test
    void testGetUserById_Success() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        User user = new TestUserBuilder().withId(userId).build();
        when(userUseCase.getBy(UUID.fromString(userId))).thenReturn(Mono.just(user));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/users/{id}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class).isEqualTo(user);
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        when(userUseCase.getBy(UUID.fromString(userId))).thenReturn(Mono.empty());

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/users/{id}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    // --- Pruebas para GET /api/v1/users?email={email} ---

    @Test
    void testGetUsersByEmail_Success() {
        // Arrange
        String email = "test@mail.com";
        User user = new TestUserBuilder().withEmail(email).build();
        when(userUseCase.findByEmail(email)).thenReturn(Flux.just(user));

        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/users").queryParam("email", email).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .contains(user);
    }

    @Test
    void testGetUsersByEmail_NotFound() {
        // Arrange
        String email = "notfound@mail.com";
        when(userUseCase.findByEmail(email)).thenReturn(Flux.empty());

        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/users").queryParam("email", email).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBodyList(User.class).hasSize(0);
    }

    @Test
    void testGetUsersByEmail_BadRequestMissingEmail() {
        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    // --- Pruebas para POST /api/v1/users ---

    @Test
    void testPostResource_Success() {
        // Arrange
        TestUserBuilder builder = new TestUserBuilder().withEmail("newuser@mail.com").withName("New User");
        UserDTO userDTO = builder.buildDTO();
        User userToSave = builder.build();
        User savedUser = builder.build();

        when(objectMapper.convertValue(any(UserDTO.class), eq(User.class))).thenReturn(userToSave);
        when(userUseCase.create(any(User.class))).thenReturn(Mono.just(savedUser));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock de la validación exitosa
        Errors errors = mock(Errors.class);
        when(errors.hasErrors()).thenReturn(false);
        when(validator.validateObject(any())).thenReturn(errors);

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class).isEqualTo(savedUser);
    }

    @Test
    void testPostResource_BadRequestValidationFailure() {
        // Arrange
        TestUserBuilder builder = new TestUserBuilder().withEmail("invalid-email").withName("Invalid User");
        UserDTO userDTO = builder.buildDTO();

        // Mock de la validación fallida
        Errors errors = mock(Errors.class);
        when(errors.hasErrors()).thenReturn(true);
        when(validator.validateObject(any())).thenReturn(errors);

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest();
    }*/
}
