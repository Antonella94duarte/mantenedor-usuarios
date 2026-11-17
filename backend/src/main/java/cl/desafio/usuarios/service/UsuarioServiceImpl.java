package cl.desafio.usuarios.service;

import cl.desafio.usuarios.dto.UsuarioCreateRequest;
import cl.desafio.usuarios.dto.UsuarioResponse;
import cl.desafio.usuarios.dto.UsuarioUpdateRequest;
import cl.desafio.usuarios.exception.RecursoDuplicadoException;
import cl.desafio.usuarios.exception.UsuarioNoEncontradoException;
import cl.desafio.usuarios.mapper.UsuarioMapper;
import cl.desafio.usuarios.model.Usuario;
import cl.desafio.usuarios.repository.UsuarioRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository repository, UsuarioMapper mapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UsuarioResponse> listar() {
        return repository.findAll(Sort.by("id")).stream().map(mapper::toResponse).toList();
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscar(id));
    }

    @Override
    @Transactional
    public UsuarioResponse crear(UsuarioCreateRequest request) {
        if (repository.existsByRut(request.rut())) {
            throw rutDuplicado();
        }
        if (repository.existsByCorreoElectronicoIgnoreCase(UsuarioMapper.normalizarCorreo(request.correoElectronico()))) {
            throw correoDuplicado();
        }
        Usuario usuario = mapper.toEntity(request);
        usuario.setContrasena(passwordEncoder.encode(request.contrasena()));
        return mapper.toResponse(repository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = buscar(id);
        if (repository.existsByRutAndIdNot(request.rut(), id)) {
            throw rutDuplicado();
        }
        if (repository.existsByCorreoElectronicoIgnoreCaseAndIdNot(
                UsuarioMapper.normalizarCorreo(request.correoElectronico()), id)) {
            throw correoDuplicado();
        }
        mapper.actualizar(usuario, request);
        if (request.contrasena() != null) {
            usuario.setContrasena(passwordEncoder.encode(request.contrasena()));
        }
        return mapper.toResponse(repository.save(usuario));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.delete(buscar(id));
    }

    private Usuario buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    private static RecursoDuplicadoException rutDuplicado() {
        return new RecursoDuplicadoException("rut", "Ya existe un usuario con ese RUT");
    }

    private static RecursoDuplicadoException correoDuplicado() {
        return new RecursoDuplicadoException("correoElectronico", "Ya existe un usuario con ese correo electrónico");
    }
}
