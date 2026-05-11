package com.mascotas.bff.client;

import com.mascotas.bff.dto.microservice.UsuarioMsResponse;
import com.mascotas.bff.dto.request.UsuarioCreateRequest;
import com.mascotas.bff.dto.request.UsuarioUpdateRequest;

import java.util.List;

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

    // Llamada al PUT /api/usuarios/perfil del microservicio (Requiere Token)
    public UsuarioMsResponse actualizarPerfil(UsuarioUpdateRequest request, String token) {
        return restClient.put()
                .uri("/api/usuarios/perfil")
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .body(UsuarioMsResponse.class);
    }
    // Llamada al GET /api/usuarios del microservicio (Requiere Token)
    public List<UsuarioMsResponse> listarUsuarios(String token) {
        return restClient.get()
                .uri("/api/usuarios")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<UsuarioMsResponse>>() {});
    }

    // Llamada al GET /api/usuarios/rut/{rut} del microservicio (Requiere Token)
    public List<UsuarioMsResponse> buscarPorRut(String rut, String token) {
        return restClient.get()
                .uri("/api/usuarios/rut/" + rut)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<UsuarioMsResponse>>() {});
    }

    // Llamada al DELETE /api/usuarios/{id} del microservicio (Requiere Token y rol ADMIN)
    public void eliminarUsuario(Integer id, String token) {
        restClient.delete()
                .uri("/api/usuarios/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }
    // Llamada al PUT /api/usuarios/{id}/rol-admin (Para promover usuarios)
    public UsuarioMsResponse promoverAAdmin(Integer id, String token) {
        return restClient.put()
                .uri("/api/usuarios/" + id + "/rol-admin")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(UsuarioMsResponse.class);
    }
}