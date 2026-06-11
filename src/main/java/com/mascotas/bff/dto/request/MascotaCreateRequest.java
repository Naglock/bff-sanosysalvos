package com.mascotas.bff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record MascotaCreateRequest(
    @Schema(description = "Chip de la mascota", example = "987654321012345")
    String chipMascota,

    @Schema(description = "Nombre de la mascota", example = "Rex")
    String nombreMascota,

    @Schema(description = "Especie de la mascota", example = "PERRO")
    String especie,

    @Schema(description = "Raza de la mascota", example = "Pastor Alemán")
    String raza,

    @Schema(description = "Sexo de la mascota", example = "Macho")
    String sexo,

    @Schema(description = "Tamaño de la mascota", example = "GRANDE")
    String tamaño,

    @Schema(description = "Color de la mascota", example = "Negro con café")
    String color,

    @Schema(hidden = true)
    Integer usuarioId
) {}