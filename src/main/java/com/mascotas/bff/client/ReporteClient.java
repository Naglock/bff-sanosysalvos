package com.mascotas.bff.client;

import com.mascotas.bff.dto.microservice.ReporteMsResponse;
import com.mascotas.bff.dto.request.ReporteCreateRequest;
import com.mascotas.bff.dto.request.ReporteUpdateRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ReporteClient {

    private final RestClient restClient;

    public ReporteClient(RestClient.Builder builder, @Value("${ms.reporte.url}") String reporteUrl) {
        this.restClient = builder.baseUrl(reporteUrl).build();
    }

    // --- MÉTODOS GET (Públicos, no necesitan token) ---
    
    public List<ReporteMsResponse> listarTodos() {
        return restClient.get()
                .uri("/api/reporte")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ReporteMsResponse>>() {});
    }

    public ReporteMsResponse buscarPorId(Integer id) {
        return restClient.get()
                .uri("/api/reporte/" + id)
                .retrieve()
                .body(ReporteMsResponse.class);
    }

    public List<ReporteMsResponse> buscarPorTipo(String tipo) {
        return restClient.get()
                .uri("/api/reporte/tipo/" + tipo)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ReporteMsResponse>>() {});
    }

    public List<ReporteMsResponse> buscarPorTipoYEstado(String tipo, String estado) {
        return restClient.get()
                .uri("/api/reporte/tipo/" + tipo + "/estado/" + estado)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ReporteMsResponse>>() {});
    }

    public List<ReporteMsResponse> buscarPorEspecieYTipo(String especie, String tipo) {
        return restClient.get()
                .uri("/api/reporte/especie/" + especie + "/tipo/" + tipo)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ReporteMsResponse>>() {});
    }

    // --- MÉTODOS PUT/DELETE/POST (Privados, SÍ necesitan token) ---
    
    public ReporteMsResponse guardarIntegral(ReporteCreateRequest request, List<MultipartFile> fotos, String token) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            HttpHeaders jsonHeaders = new HttpHeaders();
            jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ReporteCreateRequest> jsonEntity = new HttpEntity<>(request, jsonHeaders);
            body.add("reporte", jsonEntity);

            if (fotos != null && !fotos.isEmpty()) {
                for (MultipartFile foto : fotos) {
                    if (foto != null && !foto.isEmpty()) {
                        ByteArrayResource fileResource = new ByteArrayResource(foto.getBytes()) {
                            @Override
                            public String getFilename() {
                                return foto.getOriginalFilename();
                            }
                        };
                        
                        HttpHeaders fileHeaders = new HttpHeaders();
                        fileHeaders.setContentType(MediaType.parseMediaType(
                                foto.getContentType() != null ? foto.getContentType() : MediaType.IMAGE_JPEG_VALUE));
                        
                        HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(fileResource, fileHeaders);
                        body.add("foto", fileEntity);
                    }
                }
            }

            return restClient.post()
                    .uri("/api/reporte")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(ReporteMsResponse.class);

        } catch (IOException e) {
            throw new RuntimeException("Error al procesar las imagenes antes de enviarlas al backend", e);
        }
    }

    public ReporteMsResponse actualizarReporte(Integer id, ReporteUpdateRequest request, String token) {
        return restClient.put()
                .uri("/api/reporte/" + id)
                .header("Authorization", "Bearer " + token) // Pasamos el JWT para el 'Principal'
                .body(request)
                .retrieve()
                .body(ReporteMsResponse.class);
    }

    public void eliminarReporte(Integer id, String token) {
        restClient.delete()
                .uri("/api/reporte/" + id)
                .header("Authorization", "Bearer " + token) // Pasamos el JWT para el 'Principal'
                .retrieve()
                .toBodilessEntity();
    }

    public ResponseEntity<byte[]> obtenerFotoDesdeMsReportes(String nombreFoto) {
        return restClient.get()
                .uri("/uploads/" + nombreFoto)
                .retrieve()
                .toEntity(byte[].class);
    }
    
}