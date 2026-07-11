package com.mascotas.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Colocar el puerto usado en el frontend
                .allowedOrigins(frontendUrl, 
                                "http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173", "http://127.0.0.1:3000",
                                "https://localhost:5173", "https://localhost:3000", "https://127.0.0.1:5173", "https://127.0.0.1:3000") 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                // allowCredentials en true es obligatorio para que React pueda recibir cookies HttpOnly
                .allowCredentials(true); 
    }
}