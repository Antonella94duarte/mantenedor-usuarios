package cl.desafio.usuarios.mapper;

import cl.desafio.usuarios.dto.UsuarioCreateRequest;
import cl.desafio.usuarios.dto.UsuarioResponse;
import cl.desafio.usuarios.dto.UsuarioUpdateRequest;
import cl.desafio.usuarios.model.Usuario;
import java.util.Locale;
import org.springframework.stereotype.Component;

/** Convierte entre la entidad y los DTO de la API. La contraseña la gestiona el servicio. */
@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioCreateRequest request) {
        Usuario usuario = new Usuario();
        aplicar(usuario, request.nombres(), request.apellidos(), request.rut(), request.dv(),
                request.fechaNacimiento(), request.correoElectronico());
        return usuario;
    }

    public void actualizar(Usuario usuario, UsuarioUpdateRequest request) {
        aplicar(usuario, request.nombres(), request.apellidos(), request.rut(), request.dv(),
                request.fechaNacimiento(), request.correoElectronico());
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getRut(),
                usuario.getDv(),
                usuario.getFechaNacimiento(),
                usuario.getCorreoElectronico());
    }

    public static String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }

    private void aplicar(Usuario usuario, String nombres, String apellidos, Long rut, String dv,
            java.time.LocalDate fechaNacimiento, String correoElectronico) {
        usuario.setNombres(nombres.trim());
        usuario.setApellidos(apellidos.trim());
        usuario.setRut(rut);
        usuario.setDv(dv.trim().toUpperCase(Locale.ROOT));
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setCorreoElectronico(normalizarCorreo(correoElectronico));
    }
}
