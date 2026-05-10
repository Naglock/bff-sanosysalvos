package com.mascotas.bff.dto.microservice;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReporteMsResponse(
    @JsonProperty("idReporte")
    Integer idReporte,
    String tipo,
    String estado,
    String fecha,
    String descripcion,
    Double latitud,
    Double longitud,
    String nombreContacto,
    Integer telefonoContacto,
    String nombreMascota,
    String razaMascota,
    @JsonProperty("id_usuario")
    Integer usuarioId,
    String urlFoto
) {}