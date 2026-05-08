package com.mascotas.bff.controller;

import com.mascotas.bff.client.ReporteClient;
import com.mascotas.bff.client.UsuarioClient;
import com.mascotas.bff.dto.response.DashboardResponse;
import com.mascotas.bff.dto.microservice.ReporteMsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Dashboard", description = "Agregación de datos para la vista principal")
@RestController
@RequestMapping("/api/bff/dashboard")
public class DashboardController {

    private final UsuarioClient usuarioClient;
    private final ReporteClient reporteClient;

    public DashboardController(UsuarioClient usuarioClient, ReporteClient reporteClient) {
        this.usuarioClient = usuarioClient;
        this.reporteClient = reporteClient;
    }

    @Operation(summary = "Cargar datos iniciales del Dashboard")
    @GetMapping("/resumen/{idUsuario}")
    public ResponseEntity<DashboardResponse> obtenerResumen(
            @PathVariable Integer idUsuario,
            @Parameter(hidden = true) @CookieValue(name = "jwt_token") String token) {

        // 1. Datos del perfil
        var perfil = usuarioClient.buscarPorId(idUsuario, token);

        // 2. Reportes globales para el mapa (Filtrados por tipo PERDIDO y estado ACTIVO)
        List<ReporteMsResponse> todosLosReportes = reporteClient.listarTodos();
        
        List<ReporteMsResponse> perdidosZona = todosLosReportes.stream()
                .filter(r -> "PERDIDO".equals(r.tipo()) && "ACTIVO".equals(r.estado()))
                .collect(Collectors.toList());

        // 3. Mis reportes (Filtrados por el id del usuario logueado)
        // Nota: Si tu microservicio de reportes no tiene este filtro, el BFF lo hace aquí:
        List<ReporteMsResponse> misReportes = todosLosReportes.stream()
                .filter(r -> idUsuario.equals(r.usuarioId())) 
                .collect(Collectors.toList());

        DashboardResponse resumen = new DashboardResponse(
                perfil,
                perdidosZona,
                misReportes,
                "Bienvenido " + perfil.nombre() + ". Hay " + perdidosZona.size() + " mascotas perdidas reportadas."
        );

        return ResponseEntity.ok(resumen);
    }
}