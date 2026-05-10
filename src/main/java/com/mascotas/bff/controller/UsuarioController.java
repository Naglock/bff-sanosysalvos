package com.mascotas.bff.controller;

import com.mascotas.bff.client.UsuarioClient;
import com.mascotas.bff.dto.microservice.UsuarioMsResponse;
import com.mascotas.bff.dto.request.UsuarioCreateRequest;
import com.mascotas.bff.dto.request.UsuarioUpdateRequest; // Asegúrate de tener este DTO creado
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Usuarios", description = "Gestión de perfiles de usuario y registro")
@RestController
@RequestMapping("/api/bff/usuarios")
public class UsuarioController {

    private final UsuarioClient usuarioClient;

    public UsuarioController(UsuarioClient usuarioClient) {
        this.usuarioClient = usuarioClient;
    }

    @Operation(summary = "Registrar usuario", description = "Endpoint público para crear una nueva cuenta.")
    @PostMapping("/registro")
    public ResponseEntity<UsuarioMsResponse> registrar(@RequestBody UsuarioCreateRequest request) {
        UsuarioMsResponse nuevoUsuario = usuarioClient.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @Operation(summary = "Obtener perfil", description = "Obtiene los datos del usuario logueado mediante su ID.", responses = {
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado (Falta cookie)")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioMsResponse> obtenerPerfil(
            @PathVariable Integer id,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        
        UsuarioMsResponse perfil = usuarioClient.buscarPorId(id, token);
        return ResponseEntity.ok(perfil);
    }

    @Operation(summary = "Actualizar perfil", description = "Actualiza los datos del usuario logueado usando la cookie de sesión.")
    @PutMapping("/perfil")
    public ResponseEntity<UsuarioMsResponse> actualizarPerfil(
            @RequestBody UsuarioUpdateRequest request,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        
        UsuarioMsResponse perfilActualizado = usuarioClient.actualizarPerfil(request, token);
        return ResponseEntity.ok(perfilActualizado);
    }
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene la lista completa de usuarios registrados. Requiere permisos de administrador.")
    @GetMapping
    public ResponseEntity<java.util.List<UsuarioMsResponse>> listarUsuarios(
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        return ResponseEntity.ok(usuarioClient.listarUsuarios(token));
    }

    @Operation(summary = "Buscar por RUT", description = "Busca usuarios coincidiendo con el RUT proporcionado (formato con guion).")
    @GetMapping("/rut/{rut}")
    public ResponseEntity<java.util.List<UsuarioMsResponse>> buscarPorRut(
            @PathVariable String rut,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        return ResponseEntity.ok(usuarioClient.buscarPorRut(rut, token));
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema mediante su ID. Requiere permisos de administrador.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @PathVariable Integer id,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        usuarioClient.eliminarUsuario(id, token);
        return ResponseEntity.noContent().build();
    }
}