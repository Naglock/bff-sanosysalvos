package com.mascotas.bff.controller;

import com.mascotas.bff.client.MascotaClient;
import com.mascotas.bff.client.ReporteClient;
import com.mascotas.bff.client.UsuarioClient;
import com.mascotas.bff.dto.microservice.MascotaMsResponse;
import com.mascotas.bff.dto.microservice.ReporteMsResponse;
import com.mascotas.bff.dto.microservice.UsuarioMsResponse;
import com.mascotas.bff.dto.request.MascotaCreateRequest;
import com.mascotas.bff.dto.request.ReporteCreateRequest;
import com.mascotas.bff.dto.request.ReporteIntegralRequest;
import com.mascotas.bff.dto.request.ReporteUpdateRequest;
import com.mascotas.bff.util.JwtDecoderUtil;
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
import java.util.stream.Collectors;

@Tag(name = "Reportes", description = "Operaciones de orquestación para alertas y avistamientos de mascotas")
@RestController
@RequestMapping("/api/bff/reportes")
public class ReporteController {

    private final ReporteClient reporteClient;
    private final MascotaClient mascotaClient;
    private final UsuarioClient usuarioClient;
    private final JwtDecoderUtil jwtDecoderUtil;

    public ReporteController(ReporteClient reporteClient, MascotaClient mascotaClient, UsuarioClient usuarioClient, JwtDecoderUtil jwtDecoderUtil) {
        this.reporteClient = reporteClient;
        this.mascotaClient = mascotaClient;
        this.usuarioClient = usuarioClient;
        this.jwtDecoderUtil = jwtDecoderUtil;
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
            ObjectMapper objectMapper = new ObjectMapper();
            request = objectMapper.readValue(datosJson, ReporteIntegralRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Error procesando el JSON en el BFF: Verifica la sintaxis. " + e.getMessage());
        }

        Integer idUsuarioAutenticado = jwtDecoderUtil.extraerIdUsuario(token);
        Integer idMascotaFinal;
        
        String nombreMascotaReporte;
        String razaMascotaReporte;
        String tipoReporteFinal;

        if (request.mascotaId() != null) {
            idMascotaFinal = request.mascotaId();
            tipoReporteFinal = "PERDIDO";
            
            MascotaMsResponse mascotaExistente = mascotaClient.buscarPorId(idMascotaFinal);
            nombreMascotaReporte = mascotaExistente.nombreMascota();
            razaMascotaReporte = mascotaExistente.raza();
            
        } else {
            tipoReporteFinal = "AVISTADA";
            String nombreFinal = (request.nombreMascota() != null && !request.nombreMascota().isBlank()) ? request.nombreMascota() : "Desconocido";
            String chipFinal = (request.chipMascota() != null && !request.chipMascota().isBlank()) ? request.chipMascota() : "Sin registro";
            String razaFinal = (request.raza() != null && !request.raza().isBlank()) ? request.raza() : "Mestizo";
            String sexoFinal = (request.sexo() != null && !request.sexo().isBlank()) ? request.sexo() : "Desconocido";
            
            MascotaCreateRequest mascotaCall = new MascotaCreateRequest(
                chipFinal, nombreFinal, request.especie(), razaFinal, sexoFinal, request.tamaño(), request.color(), idUsuarioAutenticado
            );

            MascotaMsResponse mascotaCreada = mascotaClient.guardar(mascotaCall, token);
            idMascotaFinal = mascotaCreada.idMascota();
            
            nombreMascotaReporte = mascotaCreada.nombreMascota();
            razaMascotaReporte = mascotaCreada.raza();
        }

        ReporteCreateRequest reporteCall = new ReporteCreateRequest(
            tipoReporteFinal, "ACTIVO", request.descripcion(), request.latitud(), request.longitud(), idMascotaFinal, idUsuarioAutenticado
        );

        ReporteMsResponse reporteCreadoCrudo = reporteClient.guardarIntegral(reporteCall, foto, token);
        
        UsuarioMsResponse usuarioInfo = usuarioClient.buscarPorId(idUsuarioAutenticado, token);

        ReporteMsResponse reporteEnriquecido = new ReporteMsResponse(
            reporteCreadoCrudo.idReporte(),
            reporteCreadoCrudo.tipo(),
            reporteCreadoCrudo.estado(),
            reporteCreadoCrudo.fecha(),
            reporteCreadoCrudo.descripcion(),
            reporteCreadoCrudo.latitud(),
            reporteCreadoCrudo.longitud(),
            usuarioInfo.nombre(), 
            usuarioInfo.telefono(), 
            nombreMascotaReporte,       
            razaMascotaReporte,         
            reporteCreadoCrudo.usuarioId(),
            reporteCreadoCrudo.mascotaId(),
            reporteCreadoCrudo.urlFoto()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(reporteEnriquecido);
    }

    @Operation(summary = "Listar todos los reportes (Público)")
    @GetMapping
    public ResponseEntity<List<ReporteMsResponse>> listarTodos(
            @Parameter(hidden = true) @CookieValue(name = "jwt_token", required = false) String token) {
        List<ReporteMsResponse> reportesCrudos = reporteClient.listarTodos();
        List<ReporteMsResponse> reportesEnriquecidos = reportesCrudos.stream()
                .map(reporte -> enriquecerReporte(reporte, token))
                .collect(Collectors.toList());
        return ResponseEntity.ok(reportesEnriquecidos);
    }

    @Operation(summary = "Buscar reporte por ID (Público)")
    @GetMapping("/{id}")
    public ResponseEntity<ReporteMsResponse> buscarPorId(
            @PathVariable Integer id,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token", required = false) String token) {
        ReporteMsResponse reporteCrudo = reporteClient.buscarPorId(id);
        return ResponseEntity.ok(enriquecerReporte(reporteCrudo, token));
    }

    @Operation(summary = "Buscar por tipo (Público)")
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ReporteMsResponse>> buscarPorTipo(
            @PathVariable String tipo,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token", required = false) String token) {
        List<ReporteMsResponse> reportesCrudos = reporteClient.buscarPorTipo(tipo);
        List<ReporteMsResponse> reportesEnriquecidos = reportesCrudos.stream()
                .map(reporte -> enriquecerReporte(reporte, token))
                .collect(Collectors.toList());
        return ResponseEntity.ok(reportesEnriquecidos);
    }

    @Operation(summary = "Buscar por tipo y estado (Público)")
    @GetMapping("/tipo/{tipo}/estado/{estado}")
    public ResponseEntity<List<ReporteMsResponse>> buscarPorTipoYEstado(
            @PathVariable String tipo, 
            @PathVariable String estado,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token", required = false) String token) {
        List<ReporteMsResponse> reportesCrudos = reporteClient.buscarPorTipoYEstado(tipo, estado);
        List<ReporteMsResponse> reportesEnriquecidos = reportesCrudos.stream()
                .map(reporte -> enriquecerReporte(reporte, token))
                .collect(Collectors.toList());
        return ResponseEntity.ok(reportesEnriquecidos);
    }

    @Operation(summary = "Buscar por especie y tipo (Público)")
    @GetMapping("/especie/{especie}/tipo/{tipo}")
    public ResponseEntity<List<ReporteMsResponse>> buscarPorEspecieYTipo(
            @PathVariable String especie, 
            @PathVariable String tipo,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token", required = false) String token) {
        List<ReporteMsResponse> reportesCrudos = reporteClient.buscarPorEspecieYTipo(especie, tipo);
        List<ReporteMsResponse> reportesEnriquecidos = reportesCrudos.stream()
                .map(reporte -> enriquecerReporte(reporte, token))
                .collect(Collectors.toList());
        return ResponseEntity.ok(reportesEnriquecidos);
    }

    @Operation(summary = "Actualizar reporte (Requiere Autenticación)")
    @PutMapping("/{id}")
    public ResponseEntity<ReporteMsResponse> actualizar(
            @PathVariable Integer id,
            @RequestBody ReporteUpdateRequest request,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        ReporteMsResponse actualizado = reporteClient.actualizarReporte(id, request, token);
        return ResponseEntity.ok(enriquecerReporte(actualizado, token));
    }

    @Operation(summary = "Eliminar reporte (Requiere Autenticación)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {
        reporteClient.eliminarReporte(id, token);
        return ResponseEntity.noContent().build();
    }

    private ReporteMsResponse enriquecerReporte(ReporteMsResponse reporteCrudo, String token) {
        String nombreContactoFinal = "Usuario Desconocido";
        Integer telefonoContactoFinal = null;
        String nombreMascotaFinal = "Desconocido";
        String razaMascotaFinal = "Desconocida";

        try {
            if (reporteCrudo.usuarioId() != null) {
                UsuarioMsResponse usuarioInfo = usuarioClient.buscarPorId(reporteCrudo.usuarioId(), token);
                if (usuarioInfo != null) {
                    nombreContactoFinal = usuarioInfo.nombre();
                    telefonoContactoFinal = usuarioInfo.telefono();
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo obtener la info del usuario para el reporte " + reporteCrudo.idReporte());
        }

        try {
            if (reporteCrudo.mascotaId() != null) {
                MascotaMsResponse mascotaInfo = mascotaClient.buscarPorId(reporteCrudo.mascotaId());
                if (mascotaInfo != null) {
                    nombreMascotaFinal = mascotaInfo.nombreMascota();
                    razaMascotaFinal = mascotaInfo.raza();
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo obtener la info de la mascota para el reporte " + reporteCrudo.idReporte());
        }

        return new ReporteMsResponse(
            reporteCrudo.idReporte(),
            reporteCrudo.tipo(),
            reporteCrudo.estado(),
            reporteCrudo.fecha(),
            reporteCrudo.descripcion(),
            reporteCrudo.latitud(),
            reporteCrudo.longitud(),
            nombreContactoFinal,
            telefonoContactoFinal,
            nombreMascotaFinal,
            razaMascotaFinal,
            reporteCrudo.usuarioId(),
            reporteCrudo.mascotaId(),
            reporteCrudo.urlFoto()
        );
    }
}