# Mantenedor de usuarios

Desafío técnico Fullstack: API REST de usuarios con **Java 17 + Spring Boot 3.4** y un
mantenedor (CRUD) en **React + TypeScript**.

- Cómo ejecutarlo: [`INSTRUCTIONS.md`](INSTRUCTIONS.md)
- Diseño de la API: [`openapi.yaml`](openapi.yaml)

## Stack

| Capa | Tecnologías |
| --- | --- |
| Backend | Java 17, Spring Boot 3.4, Spring Data JPA, H2 en memoria, Bean Validation, springdoc-openapi, Maven |
| Frontend | React 19, TypeScript, Vite, Vitest |
| Infraestructura | Docker, Docker Compose, Nginx |

## Estructura

```
├── openapi.yaml            Contrato de la API
├── docker-compose.yml
├── backend/                API REST (Maven)
│   └── src/main/java/cl/desafio/usuarios/
│       ├── controller/     Capa HTTP: rutas, códigos de estado
│       ├── service/        Reglas de negocio (interfaz + implementación)
│       ├── repository/     Acceso a datos (Spring Data JPA)
│       ├── model/          Entidad JPA
│       ├── dto/            Contratos de entrada y salida de la API
│       ├── mapper/         Conversión entidad <-> DTO
│       ├── validation/     Validación del RUT (módulo 11)
│       ├── exception/      Excepciones de dominio y manejador global
│       └── config/         CORS, OpenAPI, cifrado, datos de ejemplo
└── frontend/               Mantenedor (Vite)
    └── src/
        ├── api/            Cliente HTTP y servicio de usuarios
        ├── hooks/          Gestión de estado (useUsuarios)
        ├── components/     Componentes de presentación
        ├── types/          Tipos compartidos
        └── utils/          RUT y validación del formulario
```

## API

| Método | Ruta | Descripción | Respuestas |
| --- | --- | --- | --- |
| `GET` | `/api/v1/usuarios` | Lista los usuarios | 200 |
| `GET` | `/api/v1/usuarios/{id}` | Obtiene un usuario | 200, 404 |
| `POST` | `/api/v1/usuarios` | Crea un usuario | 201 + `Location`, 400, 409 |
| `PUT` | `/api/v1/usuarios/{id}` | Actualiza un usuario | 200, 400, 404, 409 |
| `DELETE` | `/api/v1/usuarios/{id}` | Elimina un usuario | 204, 404 |

## Decisiones de diseño

- **La contraseña nunca sale de la API.** Se guarda con hash BCrypt y los DTO de respuesta no
  la incluyen. Al actualizar es opcional: si se omite, se conserva la actual.
- **`fechaNacimiento` usa `LocalDate`** (`yyyy-MM-dd`), el tipo de fecha recomendado desde
  Java 8, en lugar de `java.util.Date`: evita desfases por zona horaria en una fecha sin hora.
- **`rut` y `dv` se validan juntos** con el algoritmo módulo 11, tanto en el backend como en
  el formulario. El RUT y el correo son únicos (409 si se repiten).
- **Errores en formato RFC 7807** (`application/problem+json`), con el detalle por campo en
  `errores` para que el formulario los muestre junto al input correspondiente.
- **DTO separados de la entidad**, para no acoplar el contrato de la API al modelo de
  persistencia.
- **El frontend consume la API en el mismo origen** (`/api`, vía proxy de Vite o Nginx), así
  el build no depende de la URL del backend. CORS queda configurable para otros despliegues.
