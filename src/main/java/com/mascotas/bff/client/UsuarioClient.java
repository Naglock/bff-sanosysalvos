package com.mascotas.bff.client;

import com.mascotas.bff.dto.microservice.UsuarioMsResponse;
import com.mascotas.bff.dto.request.UsuarioCreateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UsuarioClient {

    private final RestClient restClient;

    public UsuarioClient(RestClient.Builder builder, @Value("${ms.usuario.url}") String usuarioUrl) {
        this.restClient = builder.baseUrl(usuarioUrl).build();
    }

    // Llamada al POST /api/usuarios del microservicio
    public UsuarioMsResponse registrarUsuario(UsuarioCreateRequest request) {
        return restClient.post()
                .uri("/api/usuarios")
                .body(request)
                .retrieve()
                .body(UsuarioMsResponse.class);
    }

    // Llamada al GET /api/usuarios/{id} del microservicio (Requiere Token)
    public UsuarioMsResponse buscarPorId(Integer id, String token) {
        return restClient.get()
                .uri("/api/usuarios/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(UsuarioMsResponse.class);
    }
}