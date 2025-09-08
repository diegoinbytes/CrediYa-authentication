package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name="ErrorResponse")
public record ErrorResponseDTO(
    String error
) {}
