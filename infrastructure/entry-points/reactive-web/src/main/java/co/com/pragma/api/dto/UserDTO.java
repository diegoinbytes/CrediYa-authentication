package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name="UserDTO")
public record UserDTO(
    String id,
    @NotNull
    @NotBlank
    String name,
    @NotNull
    @NotBlank
    String lastName,
    @NotNull
    @NotBlank
    @Email
    String email,
    String documentNumber,
    String phone,
    @NotNull
    @NotBlank
    String idRol,
    int basicSalary
) {}
