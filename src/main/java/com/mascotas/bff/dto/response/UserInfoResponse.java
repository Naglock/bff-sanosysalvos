package com.mascotas.bff.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserInfoResponse(
    @Schema(example = "Juan") String nombre, 
    @Schema(example = "1") String idUsuario, 
    @Schema(example = "USUARIO") String rol
) {}