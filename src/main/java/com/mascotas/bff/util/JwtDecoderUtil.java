package com.mascotas.bff.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.Base64;

@Component
public class JwtDecoderUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Integer extraerIdUsuario(String token) {
        try {
            String[] chunks = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
            JsonNode jsonNode = objectMapper.readTree(payload);
            return jsonNode.get("idUsuario").asInt();
        } catch (Exception e) {
            throw new RuntimeException("Error al decodificar el token", e);
        }
    }
}