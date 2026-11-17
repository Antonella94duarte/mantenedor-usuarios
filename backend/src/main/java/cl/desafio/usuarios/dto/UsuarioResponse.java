package cl.desafio.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/** Representación pública del usuario: nunca incluye la contraseña. */
@Schema(name = "UsuarioResponse", description = "Usuario registrado")
public record UsuarioResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "María José") String nombres,
        @Schema(example = "González Rojas") String apellidos,
        @Schema(example = "12345678") Long rut,
        @Schema(example = "5") String dv,
        @Schema(example = "1990-05-17") LocalDate fechaNacimiento,
        @Schema(example = "maria.gonzalez@example.com") String correoElectronico) {
}
