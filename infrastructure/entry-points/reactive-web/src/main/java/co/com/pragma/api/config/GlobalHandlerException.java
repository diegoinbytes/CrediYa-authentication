package co.com.pragma.api.config;

import co.com.pragma.api.exception.ContractException;
import co.com.pragma.model.exception.ModelExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalHandlerException {

    private final Logger logger = LoggerFactory.getLogger(GlobalHandlerException.class);

    @ExceptionHandler({
            ContractException.RequestErrorException.class,
            ModelExceptions.BasedSalaryNotValidException.class,
            ModelExceptions.ExistEmailException.class
    })
    public Mono<ResponseEntity<String>> handleBadRequestException(RuntimeException ex) {
        logger.error(ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()));
    }
}
