package co.com.pragma.api;

import co.com.pragma.api.exception.ContractException;
import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HandlerV1 {

    private final Logger logger = LoggerFactory.getLogger(HandlerV1.class);
    private final TransactionalOperator transactionalOperator;
    private final UserUseCase userUseCase;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public Mono<ServerResponse> getResource(ServerRequest serverRequest) {
        UUID id = UUID.fromString(serverRequest.pathVariable("id"));
        logger.info("call get resource for user : {}", id);
        return userUseCase.getBy(id)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound()
                        .build());
    }

    public Mono<ServerResponse> getUsersByEmail(ServerRequest serverRequest) {
        String email = serverRequest.queryParam("email")
                .orElseThrow(() -> new ContractException.RequestErrorException("Email parameter is required"));

        logger.info("call get users by email: {}", email.replaceAll("(^.).*(@.*$)", "$1***$2"));

        return userUseCase.findByEmail(email)
                .collectList()
                .flatMap(users -> {
                    if (users.isEmpty()) {
                        return ServerResponse.notFound().build();
                    } else {
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(users);
                    }
                });
    }

    public Mono<ServerResponse> postResource(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(UserDTO.class)
                .flatMap(this::validate)
                .doOnNext(userDTO -> logger.info("call post resource for user: {}", userDTO.email().replaceAll("(^.).*(@.*$)", "$1***$2")))
                .map(user -> objectMapper.convertValue(user, User.class))
                .flatMap(userUseCase::create)
                .as(transactionalOperator::transactional)
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser)
                );
    }

    public Mono<UserDTO> validate(UserDTO userDTO){
        Errors errors = validator.validateObject(userDTO);
        if (errors.hasErrors()){
            return Mono.error(new ContractException.RequestErrorException("error in request data."));
        } else {
            return Mono.just(userDTO);
        }
    }
}
