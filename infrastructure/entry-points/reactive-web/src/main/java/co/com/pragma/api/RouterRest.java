package co.com.pragma.api;

import co.com.pragma.api.dto.ErrorResponseDTO;
import co.com.pragma.api.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import io.swagger.v3.oas.models.OpenAPI;


@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
        @RouterOperation(
            path="/api/v1/usuarios/{id}", method= RequestMethod.GET,
            produces = "application/json",
            beanClass= HandlerV1.class, beanMethod="getResource",
            operation=@Operation(
                operationId = "getUser",
                summary="Get user by id",
                tags={"Usuarios"},
                parameters = {
                    @Parameter(
                            name = "id",
                            in = ParameterIn.PATH,
                            description = "UUID of the user",
                            required = true,
                            example = "550e8400-e29b-41d4-a716-446655440000"
                    )
                },
                responses={
                    @ApiResponse(
                            responseCode="200",
                            content=@Content(schema=@Schema(implementation= UserDTO.class))),
                    @ApiResponse(responseCode="404")
                })
        ),
        @RouterOperation(
            path="/api/v1/usuarios/", method= RequestMethod.POST,
            beanClass= HandlerV1.class, beanMethod="postResource",
            operation=@Operation(
                operationId = "createUser",
                summary="Create user", tags={"Usuarios"},
                requestBody=@io.swagger.v3.oas.annotations.parameters.RequestBody(
                        required=true, content=@Content(schema=@Schema(implementation=UserDTO.class))),
                responses={
                    @ApiResponse(responseCode="201",
                        content=@Content(schema=@Schema(implementation=UserDTO.class))),
                    @ApiResponse(
                        responseCode="400",
                        content = @Content(schema=@Schema(implementation = ErrorResponseDTO.class)
                    ))
                })
        ),
        @RouterOperation(
            path="/api/v1/usuarios/search", method= RequestMethod.GET,
            produces = "application/json",
            beanClass= HandlerV1.class, beanMethod="getUsersByEmail",
            operation=@Operation(
                operationId = "getUsersByEmail",
                summary="Get users by email",
                tags={"Usuarios"},
                parameters = {
                    @Parameter(
                            name = "email",
                            in = ParameterIn.QUERY,
                            description = "Email to search for",
                            required = true,
                            example = "user@example.com"
                    )
                },
                responses={
                    @ApiResponse(
                        responseCode="200",
                        content=@Content(schema=@Schema(implementation= UserDTO.class))),
                    @ApiResponse(
                        responseCode="404",
                        content = @Content(schema=@Schema(implementation = ErrorResponseDTO.class))
                    ),
                    @ApiResponse(
                        responseCode="400",
                        content = @Content(schema=@Schema(implementation = ErrorResponseDTO.class)
                    ))
                })
        )
    })
    public RouterFunction<ServerResponse> routerFunction(HandlerV1 handlerV1) {
        return RouterFunctions
            .route()
            .path("/api/v1",
                    builder -> builder
                            .GET("/usuarios/search", handlerV1::getUsersByEmail)
                            .GET("/usuarios/{id}", handlerV1::getResource)
                            .POST("/usuarios/", handlerV1::postResource)
            )
            .build();
        }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("CrediYa user API")
                .version("v1")
                .description("Endpoints for users"));
    }
}
