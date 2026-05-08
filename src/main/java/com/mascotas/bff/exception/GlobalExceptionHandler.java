package com.mascotas.bff.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.swagger.v3.oas.annotations.Hidden;

import java.util.Map;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Cuando React intenta llamar al BFF sin estar logueado (Falta la Cookie)
    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<Map<String, String>> handleMissingCookie(MissingRequestCookieException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", "No autorizado",
                        "message", "Sesión expirada o no iniciada. Por favor, vuelva a iniciar sesión."
                ));
    }

    // 2. Cuando el microservicio (Mascota, Reporte, etc.) responde con un 400 (Bad Request) o 404 (Not Found)
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleMicroserviceClientErrors(HttpClientErrorException ex) {
        // En vez de explotar con un 500, el BFF toma el JSON de error que mandó el microservicio y se lo pasa intacto a React
        return ResponseEntity.status(ex.getStatusCode())
                .header("Content-Type", "application/json")
                .body(ex.getResponseBodyAsString());
    }

    // 3. Cuando el microservicio se cae por completo o lanza un 500 interno
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleMicroserviceServerErrors(HttpServerErrorException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .header("Content-Type", "application/json")
                .body(ex.getResponseBodyAsString());
    }
}