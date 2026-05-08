package com.mascotas.bff.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;

public record ReporteIntegralRequest(
    @Schema(description = "ID del usuario que reporta", example = "1") 
    Integer usuarioId,
    @Schema(description = "Si la mascota ya está registrada, envía su ID aquí. Si es null, se creará una nueva.", example = "1", nullable = true)
    Integer mascotaId, 

    @Schema(description = "Tipo de reporte", example = "PERDIDO", allowableValues = {"PERDIDO", "AVISTADA"})
    String tipo,
    @Schema(example = "Visto por última vez cerca del parque central") String descripcion,
    @Schema(example = "-33.4489") Double latitud,
    @Schema(example = "-70.6693") Double longitud,

    @Schema(description = "Especie (Obligatorio si mascotaId es null)", example = "PERRO") String especie,
    @Schema(description = "Tamaño (Obligatorio si mascotaId es null)", example = "MEDIANO") String tamano,
    @Schema(description = "Color predominante", example = "Café con manchas blancas") String color,
    
    @Schema(description = "Nombre (Dejar nulo si es un avistamiento anónimo)", example = "Bobby", nullable = true) String nombreMascota,
    @Schema(description = "Número de chip", example = "123456789", nullable = true) String chipMascota,
    @Schema(example = "Poodle", nullable = true) String raza,
    @Schema(example = "Macho", nullable = true) String sexo
) {}