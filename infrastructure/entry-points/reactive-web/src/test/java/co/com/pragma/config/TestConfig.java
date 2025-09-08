package co.com.pragma.config;

import co.com.pragma.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.validation.Validator;

@TestConfiguration
public class TestConfig {

    @Bean
    public UserUseCase userUseCase() {
        return Mockito.mock(UserUseCase.class);
    }

    @Bean
    public TransactionalOperator transactionalOperator() {
        return Mockito.mock(TransactionalOperator.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return Mockito.mock(ObjectMapper.class);
    }

    @Bean
    public Validator validator() {
        return Mockito.mock(Validator.class);
    }
}
