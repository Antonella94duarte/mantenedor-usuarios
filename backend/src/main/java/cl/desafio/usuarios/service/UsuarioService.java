package cl.desafio.usuarios.service;

import cl.desafio.usuarios.dto.UsuarioCreateRequest;
import cl.desafio.usuarios.dto.UsuarioResponse;
import cl.desafio.usuarios.dto.UsuarioUpdateRequest;
import java.util.List;

public interface UsuarioService {

    List<UsuarioResponse> listar();

    UsuarioResponse obtenerPorId(Long id);

    UsuarioResponse crear(UsuarioCreateRequest request);

    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);

    void eliminar(Long id);
}
