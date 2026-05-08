package com.mascotas.bff.controller;

import com.mascotas.bff.client.AuthClient;
import com.mascotas.bff.dto.microservice.AuthMsResponse;
import com.mascotas.bff.dto.request.LoginRequest;
import com.mascotas.bff.dto.response.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Gestión de sesiones y seguridad (Login/Logout)")
@RestController
@RequestMapping("/api/bff/auth")
public class AuthController {

    private final AuthClient authClient;

    public AuthController(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Operation(summary = "Iniciar sesión", description = "Valida credenciales en el MS Auth y establece la Cookie HttpOnly con el JWT.", responses = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, cookie establecida"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public ResponseEntity<UserInfoResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthMsResponse msResponse = authClient.login(request);
        Cookie jwtCookie = new Cookie("jwt_token", msResponse.jwtToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false); // Cambiar a true en Producción con HTTPS
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); 
        
        response.addCookie(jwtCookie);

        UserInfoResponse frontendResponse = new UserInfoResponse(
                msResponse.nombre(),
                msResponse.idUsuario(),
                msResponse.rol()
        );

        return ResponseEntity.ok(frontendResponse);
    }

    @Operation(summary = "Cerrar sesión", description = "Elimina la cookie de sesión del navegador.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie deleteCookie = new Cookie("jwt_token", null);
        deleteCookie.setMaxAge(0); 
        deleteCookie.setPath("/");
        response.addCookie(deleteCookie);
        return ResponseEntity.ok().build();
    }
}