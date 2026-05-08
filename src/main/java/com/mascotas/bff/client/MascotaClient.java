package com.mascotas.bff.client;

import com.mascotas.bff.dto.microservice.MascotaMsResponse;
import com.mascotas.bff.dto.request.MascotaCreateRequest;
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

    // Mapeado a tu: @GetMapping ("/") -> Lista TODAS las mascotas
    public List<MascotaMsResponse> listarTodas(String token) {
        return restClient.get()
                .uri("/api/mascota") 
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<MascotaMsResponse>>() {});
    }

    // Mapeado a tu: @PostMapping ("/") -> Crea una mascota usando Principal
    public MascotaMsResponse guardar(MascotaCreateRequest request, String token) {
        return restClient.post()
                .uri("/api/mascota")
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .body(MascotaMsResponse.class);
    }
}