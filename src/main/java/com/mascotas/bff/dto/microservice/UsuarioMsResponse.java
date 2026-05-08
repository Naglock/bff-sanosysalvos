package com.mascotas.bff.dto.microservice;

public record UsuarioMsResponse(
    Integer idUsuario, 
    String run,
    String nombre,
    String apellido1,
    String apellido2,
    String email,
    Integer telefono,
    String fechaNacimiento,
    String rol
) {}