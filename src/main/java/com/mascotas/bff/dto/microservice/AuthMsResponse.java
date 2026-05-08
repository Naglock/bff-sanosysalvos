package com.mascotas.bff.dto.microservice;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthMsResponse(
    @JsonProperty("token")
    String jwtToken, 
    String nombre, 
    @JsonProperty("id_usuario")
    Integer idUsuario, 
    String rol
) {}