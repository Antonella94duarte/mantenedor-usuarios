package cl.desafio.usuarios.dto;

import cl.desafio.usuarios.validation.ConRut;
import cl.desafio.usuarios.validation.RutValido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@RutValido
@Schema(name = "UsuarioUpdateRequest", description = "Datos para modificar un usuario existente")
public record UsuarioUpdateRequest(
        @Schema(example = "María José")
        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres no pueden superar los 100 caracteres")
        String nombres,

        @Schema(example = "González Rojas")
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
        String apellidos,

        @Schema(description = "RUT sin puntos ni dígito verificador", example = "12345678")
        @NotNull(message = "El RUT es obligatorio")
        @Positive(message = "El RUT debe ser un número positivo")
        Long rut,

        @Schema(description = "Dígito verificador (0-9 o K)", example = "5")
        @NotBlank(message = "El dígito verificador es obligatorio")
        @Pattern(regexp = "^[0-9kK]$", message = "El dígito verificador debe ser un número o K")
        String dv,

        @Schema(example = "1990-05-17")
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        LocalDate fechaNacimiento,

        @Schema(example = "maria.gonzalez@example.com")
        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El correo electrónico no tiene un formato válido")
        @Size(max = 150, message = "El correo electrónico no puede superar los 150 caracteres")
        String correoElectronico,

        @Schema(
                description = "Nueva contraseña. Si se omite, se conserva la actual.",
                example = "NuevaClave.456",
                minLength = 8,
                maxLength = 72,
                nullable = true)
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String contrasena) implements ConRut {
}
