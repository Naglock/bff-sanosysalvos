package com.mascotas.bff.client;

import com.mascotas.bff.dto.request.LoginRequest;
import com.mascotas.bff.dto.microservice.AuthMsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuthClient {

    private final RestClient restClient;

    // Usamos @Value para leer la URL desde application.properties
    public AuthClient(RestClient.Builder builder, @Value("${ms.auth.url}") String authUrl) {
        // Configuramos la URL base para todas las peticiones de este cliente
        this.restClient = builder.baseUrl(authUrl).build();
    }

    public AuthMsResponse login(LoginRequest request) {
        return restClient.post()
                .uri("/api/auth/login") // La ruta exacta del MS de Auth
                .body(request)          // Spring convierte el LoginRequest a JSON automáticamente
                .retrieve()             // Ejecuta la petición
                .body(AuthMsResponse.class); // Convierte el JSON de respuesta a tu Record
    }
}