package co.com.pragma.api.config;

import co.com.pragma.api.dto.ErrorResponseDTO;
import co.com.pragma.api.exception.ContractException;
import co.com.pragma.model.exception.ModelExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class GlobalHandlerException {

    private final Logger logger = LoggerFactory.getLogger(GlobalHandlerException.class);

    @ExceptionHandler({
            ContractException.RequestErrorException.class,
            ModelExceptions.BasedSalaryNotValidException.class,
            ModelExceptions.ExistEmailException.class
    })
    public Mono<ResponseEntity<ErrorResponseDTO>> handleBadRequestException(RuntimeException ex) {
        logger.error(ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleAllUncaughtException(Exception ex, ServerHttpRequest request) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO("something was wrong")));
    }
}
