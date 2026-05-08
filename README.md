# Mascotas Perdidas - BFF (Backend for Frontend) 🐾

Este repositorio contiene el **BFF (Backend for Frontend)** encargado de orquestar la comunicación entre el Frontend (React) y los microservicios del sistema de reporte de mascotas para DUOC UC. Este componente centraliza la seguridad, simplifica el consumo de datos y optimiza la experiencia de usuario.

## 🛠️ 1. Especificaciones Técnicas

El proyecto utiliza tecnologías de vanguardia para garantizar un rendimiento óptimo en una arquitectura de microservicios:

* **Framework:** Spring Boot 4.0.6 (Spring Framework 7)
* **Java:** Versión 17 (LTS)
* **Arquitectura:** Pattern BFF (Backend for Frontend)
* **Seguridad:** Spring Security con persistencia de sesión mediante **Cookies HttpOnly** (Protección contra XSS)
* **Documentación:** Swagger / OpenAPI 3 (Springdoc 2.8.5)
* **Puerto de ejecución:** 8090

---

## 🚀 2. Guía de Integración para Frontend

Para conectar el cliente (React + Vite) con este BFF, es imperativo configurar el cliente HTTP para manejar el estado de sesión basado en cookies.

### A. Configuración de Axios
Se debe habilitar `withCredentials` para que el navegador acepte y envíe la cookie `jwt_token` automáticamente en cada petición hacia el puerto `8090`.

```javascript
// src/api/axiosConfig.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8090/api/bff',
  withCredentials: true, // CRÍTICO: Permite el flujo de Cookies HttpOnly
  headers: {
    'Content-Type': 'application/json'
  }
});

export default api;
```

### B. Flujo de Autenticación
1. **Login (`POST /auth/login`):** Al autenticarse, el servidor inyecta la cookie de sesión. No se debe guardar el token en `localStorage`.
2. **Validación de Sesión:** Si el BFF retorna un código HTTP **401 Unauthorized**, el usuario debe ser redirigido a la vista de `/login`.

---

## 🔒 3. Niveles de Acceso y Seguridad

El sistema está diseñado para que la información crítica esté protegida, pero manteniendo la utilidad pública del mapa de mascotas.

* 🟢 **Acceso Público (Sin Login):** Cualquier usuario (incluso visitantes anónimos) puede acceder a los métodos `GET` para listar reportes, buscar por ID, especie o estado. Esto permite que el mapa de mascotas funcione para todo el mundo.
* 🔴 **Acceso Privado (Requiere Login):** Operaciones críticas como el Dashboard (`GET /dashboard/...`), la creación de reportes (`POST`), actualizaciones (`PUT`) y eliminaciones (`DELETE`) exigen que el usuario tenga su cookie de sesión activa. Además, los usuarios solo pueden editar o borrar **sus propios** reportes.

---

## 📌 4. Endpoints Vitales y Casos de Uso (Orquestación)

### Resumen de Rutas
| Método | Endpoint | Acceso | Propósito |
| :--- | :--- | :--- | :--- |
| **GET** | `/dashboard/resumen/{id}` | Privado | Retorna Perfil, Mascotas Perdidas y reportes propios en un solo JSON. |
| **GET** | `/reportes` | Público | Lista global de reportes activos para visualización en Mapas. |
| **PUT** | `/reportes/{id}` | Privado | Edición de reportes (ej. cambiar estado a `RESUELTO`). |
| **DELETE** | `/reportes/{id}` | Privado | Eliminación de registros de reportes. |

### 🌟 El Endpoint Estrella: `POST /reportes/integral` (Privado)
Este endpoint está diseñado para manejar múltiples escenarios desde un solo formulario en React, evitando que el usuario tenga que hacer pasos repetitivos. 

**Caso 1: Reportar mascota propia YA registrada (Menú Desplegable)**
Si el usuario ya tiene su mascota en el sistema, en el frontend selecciona su mascota desde un `<select>`. Se envía el `mascotaId` en el JSON y el resto de los datos de la mascota se omiten. El BFF detecta el ID y enlaza el nuevo reporte directamente a la mascota existente, ahorrando tiempo.

**Caso 2: Reportar mascota propia NUEVA**
Si el usuario perdió una mascota que nunca había registrado, envía el JSON con `mascotaId: null` junto con todos los datos (nombre, raza, chip). El BFF se encarga de viajar al microservicio de Mascotas, crearla, obtener el nuevo ID, y luego generar el reporte automáticamente.

**Caso 3: Buen Samaritano (Avistamiento de mascota ajena)**
Si un usuario ve un perrito en la calle, envía un reporte tipo `"AVISTADA"` con `mascotaId: null` y deja campos en blanco (ej. no sabe el nombre ni la raza). El BFF incluye una **lógica de resiliencia**: auto-completará los campos vacíos con valores por defecto como `"Desconocido"`, `"Sin registro"` y `"Mestizo"`, asegurando que el reporte se cree en el mapa sin lanzar errores de validación.

---

## 🚑 5. Manejo de Errores Global

El BFF centraliza y limpia las excepciones de los microservicios, entregando un formato consistente para facilitar la validación en formularios del lado del cliente:

```json
{
  "status": 400,
  "message": "Error en la validación de los datos enviados.",
  "fieldErrors": {
    "descripcion": "La descripción del reporte es obligatoria.",
    "latitud": "Debe seleccionar un punto en el mapa."
  }
}
```

---

## 📖 6. Documentación Interactiva

Puedes probar los flujos, realizar peticiones y revisar los esquemas de datos estructurados en:

🔗 **Swagger UI:** [http://localhost:8090/swagger](http://localhost:8090/swagger)

---

## ⚙️ 7. Comandos de Desarrollo

```bash
# Limpiar dependencias y compilar el proyecto
mvn clean install

# Ejecutar el servidor omitiendo la fase de tests
mvn spring-boot:run -DskipTests
```

---
