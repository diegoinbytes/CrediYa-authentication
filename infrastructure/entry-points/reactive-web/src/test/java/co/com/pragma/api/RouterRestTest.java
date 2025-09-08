package co.com.pragma.api;

import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    private WebTestClient webTestClient;

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    public TransactionalOperator transactionalOperator;

    @Mock
    private Validator validator;

    private HandlerV1 handlerV1;
    private RouterRest routerRest;

    private final String BASE_URL = "/api/v1";
    private final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private final String EMAIL = "test@example.com";
    private User testUser;

    @BeforeEach
    void setUp() {
        handlerV1 = new HandlerV1(transactionalOperator, userUseCase, objectMapper, validator);
        routerRest = new RouterRest();

        webTestClient = WebTestClient.bindToRouterFunction(routerRest.routerFunction(handlerV1)).build();

        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setName("Test User");
        testUser.setEmail(EMAIL);
    }

    @Test
    void shouldGetUserById() {
        when(userUseCase.getBy(any(UUID.class))).thenReturn(Mono.just(testUser));

        webTestClient.get()
                .uri(BASE_URL + "/usuarios/" + USER_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(USER_ID)
                .jsonPath("$.email").isEqualTo(EMAIL);
    }

    @Test
    void shouldSearchUsersByEmail() {
        when(userUseCase.findByEmail(EMAIL)).thenReturn(reactor.core.publisher.Flux.just(testUser));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/usuarios/search")
                        .queryParam("email", EMAIL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].email").isEqualTo(EMAIL);
    }

    @Test
    void shouldCreateUser() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("newuser@example.com");
        Errors mockErrors = Mockito.mock(Errors.class);

        when(objectMapper.convertValue(any(), eq(User.class))).thenAnswer(invocation -> {
            Object source = invocation.getArgument(0);
            if (source instanceof UserDTO) {
                User user = new User();
                user.setName(((UserDTO) source).name());
                user.setEmail(((UserDTO) source).email());
                // Set other fields as needed
                return user;
            }
            return null;
        });

        when(userUseCase.create(any(User.class))).thenReturn(Mono.just(testUser));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(validator.validateObject(any())).thenReturn(mockErrors);
        when(mockErrors.hasErrors()).thenReturn(false);

        webTestClient.post()
                .uri(BASE_URL + "/usuarios/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "New User",
                            "email": "newuser@example.com"
                        }
                        """)
                .exchange()
                .expectStatus().isOk();
    }
}
