package cl.desafio.usuarios.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.desafio.usuarios.dto.UsuarioCreateRequest;
import cl.desafio.usuarios.dto.UsuarioResponse;
import cl.desafio.usuarios.dto.UsuarioUpdateRequest;
import cl.desafio.usuarios.exception.RecursoDuplicadoException;
import cl.desafio.usuarios.exception.UsuarioNoEncontradoException;
import cl.desafio.usuarios.mapper.UsuarioMapper;
import cl.desafio.usuarios.model.Usuario;
import cl.desafio.usuarios.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    private static final LocalDate NACIMIENTO = LocalDate.of(1990, 5, 17);

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UsuarioServiceImpl(repository, new UsuarioMapper(), passwordEncoder);
    }

    @Test
    void crearGuardaLaContrasenaCifradaYNormalizaLosDatos() {
        when(passwordEncoder.encode("Secreta.123")).thenReturn("hash");
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponse response = service.crear(new UsuarioCreateRequest(
                " Ana ", "Pérez", 6L, "k", NACIMIENTO, " Ana.Perez@Example.com ", "Secreta.123"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getContrasena()).isEqualTo("hash");
        assertThat(response.nombres()).isEqualTo("Ana");
        assertThat(response.dv()).isEqualTo("K");
        assertThat(response.correoElectronico()).isEqualTo("ana.perez@example.com");
    }

    @Test
    void crearRechazaUnRutYaRegistrado() {
        when(repository.existsByRut(12345678L)).thenReturn(true);

        assertThatThrownBy(() -> service.crear(new UsuarioCreateRequest(
                "Ana", "Pérez", 12345678L, "5", NACIMIENTO, "ana@example.com", "Secreta.123")))
                .isInstanceOf(RecursoDuplicadoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void actualizarSinContrasenaConservaLaActual() {
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setContrasena("hash-original");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.actualizar(1L, new UsuarioUpdateRequest(
                "Ana", "Pérez", 12345678L, "5", NACIMIENTO, "ana@example.com", null));

        assertThat(existente.getContrasena()).isEqualTo("hash-original");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void eliminarUnUsuarioInexistenteLanzaNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(99L)).isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
