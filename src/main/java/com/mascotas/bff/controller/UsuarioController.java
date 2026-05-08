package com.mascotas.bff.controller;

import com.mascotas.bff.client.UsuarioClient;
import com.mascotas.bff.dto.microservice.UsuarioMsResponse;
import com.mascotas.bff.dto.request.UsuarioCreateRequest;
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

        // --- PRUEBA DE DEBUGGING ---
        System.out.println("====== DEBUG ======");
        System.out.println("ID solicitado: " + id);
        System.out.println("Token recibido desde la Cookie: " + token);
        System.out.println("===================");
        
        UsuarioMsResponse perfil = usuarioClient.buscarPorId(id, token);
        return ResponseEntity.ok(perfil);
    }
}