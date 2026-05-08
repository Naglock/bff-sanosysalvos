package com.mascotas.bff.dto.response;

import com.mascotas.bff.dto.microservice.ReporteMsResponse;
import com.mascotas.bff.dto.microservice.UsuarioMsResponse;
import java.util.List;

public record DashboardResponse(
    UsuarioMsResponse perfil,
    List<ReporteMsResponse> mascotasPerdidasZona, // Todos los reportes PERDIDO / ACTIVO
    List<ReporteMsResponse> misReportesActivos,   // Reportes del usuario logueado
    String resumenNotificaciones                  // Un texto dinámico opcional
) {}