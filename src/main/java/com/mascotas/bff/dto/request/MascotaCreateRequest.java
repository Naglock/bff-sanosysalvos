package com.mascotas.bff.dto.request;

public record MascotaCreateRequest(
    String chipMascota,
    String nombreMascota,
    String especie,
    String raza,
    String sexo,
    String tamaño,
    String color
) {}