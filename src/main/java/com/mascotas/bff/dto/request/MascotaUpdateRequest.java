package com.mascotas.bff.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MascotaUpdateRequest(
        @Schema(description = "Nombre de la mascota", example = "Rex")
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,

        @Schema(description = "Especie de la mascota", example = "PERRO")
        @NotBlank(message = "La especie no puede estar vacía")
        String especie,

        @Schema(description = "Raza de la mascota", example = "Pastor Alemán")
        @NotBlank(message = "La raza no puede estar vacía")
        String raza,

        @Schema(description = "Sexo de la mascota", example = "Macho")
        @NotBlank(message = "El sexo no puede estar vacío")
        String sexo,

        @Schema(description = "Color de la mascota", example = "Negro con café y manchas blancas")
        @NotBlank(message = "El color no puede estar vacío")
        String color,

        @Schema(description = "Tamaño de la mascota", example = "GRANDE")
        @NotBlank(message = "El tamaño no puede estar vacío")
        String tamaño
) {}
