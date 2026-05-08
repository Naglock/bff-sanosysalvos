package com.mascotas.bff.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "cookieAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Mascotas Perdidas - API BFF")
                        .version("1.0")
                        .description("BFF que orquesta el sistema de reportes de mascotas para DUOC UC. " +
                                "Maneja seguridad mediante Cookies HttpOnly.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo - DUOC UC")
                                .email("correos@duocuc.cl")))
                // Configuramos Swagger para que use la cookie jwt_token
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name("jwt_token") // Nombre de la cookie
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)));
    }
}