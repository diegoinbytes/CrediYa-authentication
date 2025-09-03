package co.com.pragma.r2dbc;

import co.com.pragma.model.exception.ModelExceptions;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        UUID,
        UserReactiveRepository
> implements UserRepository {

    public UserRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, entity -> mapper.map(entity, User.class));
    }

    @Override
    public Mono<User> save(User entity) {
        return super.save(entity)
            /*.onErrorResume(
                DataIntegrityViolationException.class,
                ex ->
                    Mono.error(
                        new ModelExceptions.ExistEmailException("Email is already registred")
                    )
            )*/;
    }

    @Override
    public Mono<User> findById(UUID id) {
        return super.findById(id);
    }


    @Override
    public Mono<Boolean> existUserByEmail(String email) {
        return findByEmail(email)
                .hasElements();
    }

    @Override
    public Flux<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(entity -> mapper.map(entity, User.class));
    }
}
