package co.com.pragma.usecase.user;

import co.com.pragma.model.exception.ModelExceptions;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> getBy(UUID id){
        return userRepository.findById(id);
    }

    public Flux<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<User> create(User user){
        if (user.getBasicSalary() >=0 && user.getBasicSalary() <=15000000){
            return userRepository.existUserByEmail(
                user.getEmail()).flatMap(
                exist -> {
                    if (exist){
                        return Mono.error( new ModelExceptions.ExistEmailException("Email is already registred"));
                    } else {
                        return userRepository.save(user);
                    }
                }
            );
        } else {
            return Mono.error(new ModelExceptions.BasedSalaryNotValidException("Base salary must be between 0 and 15,000,000"));
        }
    }
}
