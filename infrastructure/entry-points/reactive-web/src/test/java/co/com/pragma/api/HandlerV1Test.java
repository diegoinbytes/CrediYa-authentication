package co.com.pragma.api;

import co.com.pragma.api.exception.ContractException;
import co.com.pragma.builder.TestUserBuilder;
import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.UUID;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandlerV1Test {

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private TransactionalOperator transactionalOperator;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Validator validator;
    
    @InjectMocks
    private HandlerV1 handlerV1;


    private final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private final String EMAIL = "test@example.com";
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new TestUserBuilder()
                .withId(USER_ID)
                .withEmail(EMAIL)
                .build();
    }

    @Test
    void getResource_WhenUserExists_ShouldReturnUser() {
        // Arrange
        ServerRequest request = createMockRequestWithId(USER_ID);
        when(userUseCase.getBy(UUID.fromString(USER_ID))).thenReturn(Mono.just(testUser));

        // Act
        Mono<org.springframework.web.reactive.function.server.ServerResponse> responseMono = 
            handlerV1.getResource(request);

        // Assert
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> {
                    assert serverResponse.statusCode().is2xxSuccessful();
                    return true;
                })
                .verifyComplete();

        verify(userUseCase).getBy(UUID.fromString(USER_ID));
    }

    @Test
    void getResource_WhenUserNotExists_ShouldReturnNotFound() {
        // Arrange
        ServerRequest request = createMockRequestWithId(USER_ID);
        when(userUseCase.getBy(UUID.fromString(USER_ID))).thenReturn(Mono.empty());

        // Act
        Mono<org.springframework.web.reactive.function.server.ServerResponse> responseMono = 
            handlerV1.getResource(request);

        // Assert
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> {
                    assert serverResponse.statusCode().is4xxClientError();
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void getUsersByEmail_WhenEmailProvided_ShouldReturnUsers() {
        // Arrange
        ServerRequest request = createMockRequestWithEmail(EMAIL);
        when(userUseCase.findByEmail(EMAIL)).thenReturn(reactor.core.publisher.Flux.just(testUser));

        // Act
        Mono<ServerResponse> responseMono = handlerV1.getUsersByEmail(request);

        // Assert
        StepVerifier.create(responseMono)
            .assertNext(serverResponse -> {
                assertThat(serverResponse.statusCode().is2xxSuccessful()).isTrue();
            })
            .verifyComplete();

        verify(userUseCase).findByEmail(EMAIL);
    }

    @Test
    void getUsersByEmail_WhenEmailNotProvided_ShouldThrowException() {
        // Arrange
        ServerRequest request = createMockRequestWithoutEmail();

        // Act & Assert
        StepVerifier.create(handlerV1.getUsersByEmail(request))
                .expectErrorMatches(throwable -> 
                    throwable instanceof ContractException.RequestErrorException &&
                    throwable.getMessage().contains("Email parameter is required")
                )
                .verify();
    }
    private ServerRequest createMockRequestWithId(String id) {
        ServerRequest request = mock(ServerRequest.class);
        when(request.pathVariable("id")).thenReturn(id);
        return request;
    }

    private ServerRequest createMockRequestWithEmail(String email) {
        ServerRequest request = mock(ServerRequest.class);
        when(request.queryParam("email")).thenReturn(java.util.Optional.of(email));
        return request;
    }

    private ServerRequest createMockRequestWithoutEmail() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.queryParam("email")).thenReturn(java.util.Optional.empty());
        return request;
    }
}
