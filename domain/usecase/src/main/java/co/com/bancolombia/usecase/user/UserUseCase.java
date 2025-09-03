package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.exception.ModelExceptions;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(UserUseCase.class);

    public Mono<User> getBy(UUID id){
        return userRepository.findById(id);
    }

    public Mono<User> create(User user){
        if (user.getBasicSalary() >=0 && user.getBasicSalary() <=15000000){
            return Mono.error(new ModelExceptions.BasedSalaryNotValidException("Base salary must be between 0 and 15,000,000"));
        }
        return userRepository.existUserByEmail(
            user.getEmail()).flatMap(
            exist -> {
                if (exist){
                    logger.info("user already exist with email {}", user.getEmail().replaceAll("(^.).*(@.*$)", "$1***$2"));
                    return Mono.error( new ModelExceptions.ExistEmailException("Email is already registred"));
                } else {
                    return userRepository.save(user);
                }
            }
        );

    }
}
