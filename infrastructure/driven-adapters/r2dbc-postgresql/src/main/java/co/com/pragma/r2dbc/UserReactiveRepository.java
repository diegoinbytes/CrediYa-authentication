package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, UUID>, ReactiveQueryByExampleExecutor<UserEntity> {

    @Query("SELECT * FROM app_user WHERE email = :email")
    Flux<UserEntity> findByEmail(String email);
}
