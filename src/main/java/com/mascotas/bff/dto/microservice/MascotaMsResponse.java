package com.mascotas.bff.dto.microservice;

public record MascotaMsResponse(
    Integer idMascota,
    String chipMascota,
    String nombreMascota,
    String especie,
    String raza,
    String sexo,
    String tamaño,
    String color
) {}