# Instrucciones de ejecución

Todo se ejecuta desde una terminal, parado en la raíz del repositorio. Hay dos formas de
levantar el proyecto: con Docker Compose (recomendada) o de forma manual.

| Servicio | URL (Docker Compose) | URL (manual) |
| --- | --- | --- |
| Frontend (mantenedor) | http://localhost:3000 | http://localhost:5173 |
| API REST | http://localhost:8080/api/v1/usuarios | igual |
| Swagger UI | http://localhost:8080/swagger-ui.html | igual |
| OpenAPI generado en ejecución | http://localhost:8080/v3/api-docs | igual |
| Consola H2 | http://localhost:8080/h2-console | igual |

El diseño de la API está en [`openapi.yaml`](openapi.yaml).

La base de datos H2 es **en memoria**: los datos se reinician cada vez que se detiene el
backend. Al iniciar se cargan dos usuarios de ejemplo.

## Opción A: Docker Compose

Requisito: Docker con Docker Compose v2.

```bash
docker compose up --build
```

Abrir http://localhost:3000. Para detener: `Ctrl+C` y luego `docker compose down`.

Si los puertos 8080 o 3000 están ocupados, se pueden cambiar:

```bash
BACKEND_PORT=8081 FRONTEND_PORT=3001 docker compose up --build
```

## Opción B: Ejecución manual

Requisitos: Java 17 y Node.js 20.19 o superior (con npm). Maven no necesita estar instalado
porque el repositorio incluye Maven Wrapper (`mvnw`); si ya tienes Maven 3.9+, puedes usar
`mvn` en lugar de `./mvnw`.

### 1. Backend (terminal 1)

```bash
cd backend
./mvnw clean package
java -jar target/usuarios-api.jar
```

En Windows (cmd o PowerShell) usar `mvnw.cmd clean package`.

`clean package` ejecuta además las pruebas. La API queda disponible en http://localhost:8080.

### 2. Frontend (terminal 2)

```bash
cd frontend
npm ci
npm run build
npm run preview
```

Abrir http://localhost:5173. El servidor redirige las llamadas a `/api` hacia
`http://localhost:8080`, por lo que el backend debe estar corriendo.

Para desarrollo con recarga en caliente, usar `npm run dev` en lugar de `build` + `preview`.

## Pruebas

```bash
cd backend && ./mvnw test
```

```bash
cd frontend && npm test
```

## Configuración

Variables de entorno opcionales:

| Variable | Dónde | Valor por defecto | Descripción |
| --- | --- | --- | --- |
| `SERVER_PORT` | backend | `8080` | Puerto de la API |
| `APP_CORS_ALLOWED_ORIGINS` | backend | `http://localhost:5173,http://localhost:3000` | Orígenes permitidos por CORS |
| `APP_SEED_ENABLED` | backend | `true` | Carga los usuarios de ejemplo al iniciar |
| `VITE_PROXY_TARGET` | frontend (`.env`) | `http://localhost:8080` | Backend al que se redirige `/api` |
| `VITE_API_URL` | frontend (`.env`) | `/api/v1` | URL base de la API (se fija al hacer build) |

Consola H2: JDBC URL `jdbc:h2:mem:usuariosdb`, usuario `sa`, sin contraseña.

## Probar la API desde la terminal

```bash
# Listar
curl http://localhost:8080/api/v1/usuarios

# Crear
curl -i -X POST http://localhost:8080/api/v1/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nombres":"Ana","apellidos":"Pérez Díaz","rut":22222222,"dv":"2","fechaNacimiento":"1995-03-10","correoElectronico":"ana.perez@example.com","contrasena":"Secreta.123"}'

# Obtener por id
curl http://localhost:8080/api/v1/usuarios/1

# Actualizar (la contraseña es opcional: si se omite, se conserva)
curl -X PUT http://localhost:8080/api/v1/usuarios/1 \
  -H "Content-Type: application/json" \
  -d '{"nombres":"María José","apellidos":"González Soto","rut":12345678,"dv":"5","fechaNacimiento":"1990-05-17","correoElectronico":"maria.gonzalez@example.com"}'

# Eliminar
curl -i -X DELETE http://localhost:8080/api/v1/usuarios/1
```
