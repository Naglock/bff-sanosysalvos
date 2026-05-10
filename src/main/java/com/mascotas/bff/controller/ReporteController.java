package com.mascotas.bff.controller;

import com.mascotas.bff.client.MascotaClient;
import com.mascotas.bff.client.ReporteClient;
import com.mascotas.bff.dto.microservice.MascotaMsResponse;
import com.mascotas.bff.dto.microservice.ReporteMsResponse;
import com.mascotas.bff.dto.request.MascotaCreateRequest;
import com.mascotas.bff.dto.request.ReporteCreateRequest;
import com.mascotas.bff.dto.request.ReporteIntegralRequest;
import com.mascotas.bff.dto.request.ReporteUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Tag(name = "Reportes", description = "Operaciones de orquestación para alertas y avistamientos de mascotas")
@RestController
@RequestMapping("/api/bff/reportes")
public class ReporteController {

    private final ReporteClient reporteClient;
    private final MascotaClient mascotaClient;

    public ReporteController(ReporteClient reporteClient, MascotaClient mascotaClient) {
        this.reporteClient = reporteClient;
        this.mascotaClient = mascotaClient;
    }

    @Operation(
        summary = "Crear reporte integral (Orquestador con Imagen)",
        description = "Maneja 3 casos y sube la foto: A) Registra y reporta mascota perdida. B) Registra avistamiento anónimo. C) Reporta mascota ya existente mediante ID.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Operación integral exitosa con imagen")
        }
    )
    @PostMapping(value = "/integral", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReporteMsResponse> guardarReporteIntegral(
            @Parameter(description = "Pega el JSON exacto aquí") @RequestPart("datos") String datosJson,
            @RequestPart("foto") MultipartFile foto,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        
        ReporteIntegralRequest request;
        try {
            // Convertimos el String crudo (que Swagger envía sin problemas) a nuestro objeto Java
            ObjectMapper objectMapper = new ObjectMapper();
            request = objectMapper.readValue(datosJson, ReporteIntegralRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Error procesando el JSON en el BFF: Verifica la sintaxis. " + e.getMessage());
        }

        Integer idMascotaFinal;

        if (request.mascotaId() != null) {
            // CASO C: Mascota ya existe
            idMascotaFinal = request.mascotaId();
        } else {
            // CASOS A y B: Mascota nueva o avistamiento
            String nombreFinal = (request.nombreMascota() != null && !request.nombreMascota().isBlank()) ? request.nombreMascota() : "Desconocido";
            String chipFinal = (request.chipMascota() != null && !request.chipMascota().isBlank()) ? request.chipMascota() : "Sin registro";
            String razaFinal = (request.raza() != null && !request.raza().isBlank()) ? request.raza() : "Mestizo";
            String sexoFinal = (request.sexo() != null && !request.sexo().isBlank()) ? request.sexo() : "Desconocido";
            
            MascotaCreateRequest mascotaCall = new MascotaCreateRequest(
                chipFinal, nombreFinal, request.especie(), razaFinal, sexoFinal, request.tamano(), request.color(), request.usuarioId()
            );

            MascotaMsResponse mascotaCreada = mascotaClient.guardar(mascotaCall, token);
            idMascotaFinal = mascotaCreada.idMascota();
        }

        // Crear Reporte
        ReporteCreateRequest reporteCall = new ReporteCreateRequest(
            request.tipo(), "ACTIVO", request.descripcion(), request.latitud(), request.longitud(), idMascotaFinal, request.usuarioId()
        );

        ReporteMsResponse reporteCreado = reporteClient.guardarIntegral(reporteCall, foto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteCreado);
    }

    @Operation(summary = "Listar todos los reportes (Público)")
    @GetMapping
    public ResponseEntity<List<ReporteMsResponse>> listarTodos() {
        return ResponseEntity.ok(reporteClient.listarTodos());
    }

    @Operation(summary = "Buscar reporte por ID (Público)")
    @GetMapping("/{id}")
    public ResponseEntity<ReporteMsResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(reporteClient.buscarPorId(id));
    }

    @Operation(summary = "Buscar por tipo (Público)")
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ReporteMsResponse>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(reporteClient.buscarPorTipo(tipo));
    }

    @Operation(summary = "Buscar por tipo y estado (Público)")
    @GetMapping("/tipo/{tipo}/estado/{estado}")
    public ResponseEntity<List<ReporteMsResponse>> buscarPorTipoYEstado(
            @PathVariable String tipo, @PathVariable String estado) {
        return ResponseEntity.ok(reporteClient.buscarPorTipoYEstado(tipo, estado));
    }

    @Operation(summary = "Buscar por especie y tipo (Público)")
    @GetMapping("/especie/{especie}/tipo/{tipo}")
    public ResponseEntity<List<ReporteMsResponse>> buscarPorEspecieYTipo(
            @PathVariable String especie, @PathVariable String tipo) {
        return ResponseEntity.ok(reporteClient.buscarPorEspecieYTipo(especie, tipo));
    }

    @Operation(summary = "Actualizar reporte (Requiere Autenticación)")
    @PutMapping("/{id}")
    public ResponseEntity<ReporteMsResponse> actualizar(
            @PathVariable Integer id,
            @RequestBody ReporteUpdateRequest request, // Ahora usa correctamente org.springframework.web.bind.annotation.RequestBody
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        
        ReporteMsResponse actualizado = reporteClient.actualizarReporte(id, request, token);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar reporte (Requiere Autenticación)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        
        reporteClient.eliminarReporte(id, token);
        return ResponseEntity.noContent().build();
    }
}