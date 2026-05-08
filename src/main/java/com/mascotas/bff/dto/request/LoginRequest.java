package com.mascotas.bff.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
    @Schema(example = "juan.perez@example.com") String email, 
    @Schema(example = "PasswordSegura123") String password
) {}