package com.mascotas.bff.controller;

import com.mascotas.bff.client.MascotaClient;
import com.mascotas.bff.dto.microservice.MascotaMsResponse;
import com.mascotas.bff.dto.request.MascotaCreateRequest;
import com.mascotas.bff.dto.request.MascotaUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Mascotas", description = "Operaciones CRUD para la gestión de mascotas desde el BFF")
@RestController
@RequestMapping("/api/bff/mascotas")
public class MascotaController {

    private final MascotaClient mascotaClient;

    public MascotaController(MascotaClient mascotaClient) {
        this.mascotaClient = mascotaClient;
    }

    // --- RUTAS PÚBLICAS (No requieren Cookie) ---

    @Operation(summary = "Listar todas las mascotas")
    @GetMapping
    public ResponseEntity<List<MascotaMsResponse>> listarTodas() {
        return ResponseEntity.ok(mascotaClient.listarTodas());
    }

    @Operation(summary = "Buscar mascota por ID")
    @GetMapping("/{id}")
    public ResponseEntity<MascotaMsResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(mascotaClient.buscarPorId(id));
    }

    @Operation(summary = "Buscar mascotas por especie")
    @GetMapping("/especie/{especie}")
    public ResponseEntity<List<MascotaMsResponse>> buscarPorEspecie(@PathVariable String especie) {
        return ResponseEntity.ok(mascotaClient.buscarPorEspecie(especie));
    }

    @Operation(summary = "Buscar mascotas por tamaño")
    @GetMapping("/tamano/{tamano}")
    public ResponseEntity<List<MascotaMsResponse>> buscarPorTamano(@PathVariable String tamano) {
        return ResponseEntity.ok(mascotaClient.buscarPorTamano(tamano));
    }

    @Operation(summary = "Buscar mascota por número de chip")
    @GetMapping("/chip/{chip}")
    public ResponseEntity<MascotaMsResponse> buscarPorChip(@PathVariable String chip) {
        return ResponseEntity.ok(mascotaClient.buscarPorChip(chip));
    }

    // --- RUTAS PROTEGIDAS (Requieren Cookie JWT) ---

    @Operation(summary = "Registrar una nueva mascota (Independiente del reporte)")
    @PostMapping
    public ResponseEntity<MascotaMsResponse> guardar(
            @Valid @RequestBody MascotaCreateRequest request,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mascotaClient.guardar(request, token));
    }

    @Operation(summary = "Actualizar datos de una mascota")
    @PutMapping("/{id}")
    public ResponseEntity<MascotaMsResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MascotaUpdateRequest request,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        return ResponseEntity.ok(mascotaClient.actualizarMascota(id, request, token));
    }

    @Operation(summary = "Eliminar mascota")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        mascotaClient.eliminarMascota(id, token);
        return ResponseEntity.noContent().build();
    }
}