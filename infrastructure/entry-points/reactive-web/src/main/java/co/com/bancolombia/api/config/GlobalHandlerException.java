package co.com.bancolombia.api.config;

import co.com.bancolombia.api.exception.ContractException;
import co.com.bancolombia.model.exception.ModelExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler({
            ContractException.RequestErrorException.class,
            ModelExceptions.BasedSalaryNotValidException.class,
            ModelExceptions.ExistEmailException.class
    })
    public Mono<ResponseEntity<String>> handleBadRequestException(RuntimeException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()));
    }
}
