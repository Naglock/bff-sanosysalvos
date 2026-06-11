package com.mascotas.bff.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
    @Schema(example = "admin@mascotas.cl") String email, 
    @Schema(example = "admin123") String password
) {}