package com.mascotas.bff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioUpdateRequest(
    @Schema(description = "Nombre(s) del usuario", example = "Maria Alejandra") 
    String nombre,

    @Schema(description = "Primer apellido", example = "López") 
    String apellido1,

    @Schema(description = "Segundo apellido", example = "Torres", nullable = true) 
    String apellido2,

    @Schema(description = "Correo electrónico de contacto", example = "maria.nueva@example.com") 
    String email,

    @Schema(description = "Teléfono móvil", example = "555666777") 
    Integer telefono
    
) {}