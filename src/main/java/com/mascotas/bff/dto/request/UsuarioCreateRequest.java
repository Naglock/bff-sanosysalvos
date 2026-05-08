package com.mascotas.bff.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioCreateRequest(
    @Schema(example = "12345678-5") String run,
    @Schema(example = "Maria") String nombre,
    @Schema(example = "López") String apellido1,
    @Schema(example = "Torres") String apellido2,
    @Schema(example = "20-10-1995") String fechaNacimiento,
    @Schema(example = "maria.lopez@example.com") String email,
    @Schema(example = "912345678") Integer telefono,
    @Schema(example = "PasswordSegura123") String password
) {}