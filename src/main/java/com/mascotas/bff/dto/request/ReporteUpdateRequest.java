package com.mascotas.bff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReporteUpdateRequest(
    @Schema(description = "Tipo de reporte", example = "PERDIDO")
    String tipo,
    
    @Schema(description = "Estado del reporte", example = "RESUELTO")
    String estado,
    
    @Schema(description = "Descripción actualizada", example = "Apareció en la plaza central.")
    String descripcion,
    
    @Schema(description = "Nueva latitud", example = "-33.4500")
    Double latitud,
    
    @Schema(description = "Nueva longitud", example = "-70.6700")
    Double longitud
) {}