package com.mascotas.bff.client;

import com.mascotas.bff.dto.microservice.MascotaMsResponse;
import com.mascotas.bff.dto.request.MascotaCreateRequest;
import com.mascotas.bff.dto.request.MascotaUpdateRequest; // 👈 Asegúrate de tener este DTO
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class MascotaClient {

    private final RestClient restClient;

    public MascotaClient(RestClient.Builder builder, @Value("${ms.mascota.url}") String mascotaUrl) {
        this.restClient = builder.baseUrl(mascotaUrl).build();
    }

    // --- MÉTODOS PÚBLICOS (No requieren Token) ---

    // GET: Lista TODAS las mascotas
    public List<MascotaMsResponse> listarTodas() {
        return restClient.get()
                .uri("/api/mascota")
                .retrieve()
                .body(new ParameterizedTypeReference<List<MascotaMsResponse>>() {});
    }

    // GET: Buscar por ID
    public MascotaMsResponse buscarPorId(Integer id) {
        return restClient.get()
                .uri("/api/mascota/" + id)
                .retrieve()
                .body(MascotaMsResponse.class);
    }

    // GET: Buscar por Especie
    public List<MascotaMsResponse> buscarPorEspecie(String especie) {
        return restClient.get()
                .uri("/api/mascota/especie/" + especie)
                .retrieve()
                .body(new ParameterizedTypeReference<List<MascotaMsResponse>>() {});
    }

    // GET: Buscar por Tamaño
    public List<MascotaMsResponse> buscarPorTamano(String tamano) {
        return restClient.get()
                .uri("/api/mascota/tamano/" + tamano)
                .retrieve()
                .body(new ParameterizedTypeReference<List<MascotaMsResponse>>() {});
    }

    // GET: Buscar por Chip
    public MascotaMsResponse buscarPorChip(String chip) {
        return restClient.get()
                .uri("/api/mascota/chip/" + chip)
                .retrieve()
                .body(MascotaMsResponse.class);
    }


    // --- MÉTODOS PROTEGIDOS (Requieren Token para Principal) ---

    // POST: Crea una mascota
    public MascotaMsResponse guardar(MascotaCreateRequest request, String token) {
        return restClient.post()
                .uri("/api/mascota")
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .body(MascotaMsResponse.class);
    }

    // PUT: Actualiza una mascota
    public MascotaMsResponse actualizarMascota(Integer id, MascotaUpdateRequest request, String token) {
        return restClient.put()
                .uri("/api/mascota/" + id)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .body(MascotaMsResponse.class);
    }

    // DELETE: Elimina una mascota
    public void eliminarMascota(Integer id, String token) {
        restClient.delete()
                .uri("/api/mascota/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }
}
