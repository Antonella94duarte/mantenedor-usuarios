package cl.desafio.usuarios.repository;

import cl.desafio.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByRut(Long rut);

    boolean existsByRutAndIdNot(Long rut, Long id);

    boolean existsByCorreoElectronicoIgnoreCase(String correoElectronico);

    boolean existsByCorreoElectronicoIgnoreCaseAndIdNot(String correoElectronico, Long id);
}
