package com.mascotas.bff.dto.request;

public record ReporteIntegralRequest(
    Integer mascotaId,
    String nombreMascota,
    String chipMascota,
    String raza,
    String sexo,
    String especie,
    String tamaño,
    String color,
    String tipo,
    String descripcion,
    Double latitud,
    Double longitud
) {}