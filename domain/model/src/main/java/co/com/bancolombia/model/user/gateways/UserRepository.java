package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository {
    Mono<User> save(User user);
    Mono<User> findById(UUID id);
    Mono<Boolean> existUserByEmail(String email);
    Flux<User> findByEmail(String email);
}
