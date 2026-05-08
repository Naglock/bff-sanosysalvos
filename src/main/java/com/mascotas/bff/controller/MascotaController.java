package com.mascotas.bff.controller;

import com.mascotas.bff.client.MascotaClient;
import com.mascotas.bff.dto.microservice.MascotaMsResponse;
import com.mascotas.bff.dto.request.MascotaCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mascotas", description = "Gestión directa del inventario de mascotas")
@RestController
@RequestMapping("/api/bff/mascotas")
public class MascotaController {

    private final MascotaClient mascotaClient;

    public MascotaController(MascotaClient mascotaClient) {
        this.mascotaClient = mascotaClient;
    }

    @Operation(summary = "Listar todas las mascotas", description = "Obtiene el listado global de animales registrados.")
    @GetMapping
    public ResponseEntity<List<MascotaMsResponse>> listar(
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        List<MascotaMsResponse> mascotas = mascotaClient.listarTodas(token);
        return mascotas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(mascotas);
    }

    @Operation(summary = "Registrar mascota", description = "Crea una mascota de forma individual.")
    @PostMapping
    public ResponseEntity<MascotaMsResponse> guardar(
            @RequestBody MascotaCreateRequest request,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        MascotaMsResponse nueva = mascotaClient.guardar(request, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }
}