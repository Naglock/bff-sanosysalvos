package com.mascotas.bff.dto.request;

public record ReporteCreateRequest(
    String tipo,
    String estado,
    String descripcion,
    Double latitud,
    Double longitud,
    Integer mascotaId,
    Integer usuarioId
) {}