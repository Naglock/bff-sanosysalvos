package com.mascotas.bff.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record MascotaUpdateRequest(
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,

        @NotBlank(message = "La especie no puede estar vacía")
        String especie,

        @NotBlank(message = "La raza no puede estar vacía")
        String raza,

        @NotBlank(message = "El sexo no puede estar vacío")
        String sexo,

        @NotBlank(message = "El color no puede estar vacío")
        String color,

        @JsonProperty("tamaño") 
        @NotBlank(message = "El tamaño no puede estar vacío")
        String tamaño
) {}
