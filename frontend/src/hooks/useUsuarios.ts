import { useCallback, useEffect, useReducer } from 'react';
import { usuariosApi } from '../api/usuariosApi';
import type { Usuario, UsuarioPayload } from '../types/usuario';

interface Estado {
  usuarios: Usuario[];
  cargando: boolean;
  error: string | null;
}

type Accion =
  | { tipo: 'CARGA_INICIADA' }
  | { tipo: 'CARGA_EXITOSA'; usuarios: Usuario[] }
  | { tipo: 'CARGA_FALLIDA'; error: string }
  | { tipo: 'CREADO'; usuario: Usuario }
  | { tipo: 'ACTUALIZADO'; usuario: Usuario }
  | { tipo: 'ELIMINADO'; id: number };

const ESTADO_INICIAL: Estado = { usuarios: [], cargando: true, error: null };

function reducer(estado: Estado, accion: Accion): Estado {
  switch (accion.tipo) {
    case 'CARGA_INICIADA':
      return { ...estado, cargando: true, error: null };
    case 'CARGA_EXITOSA':
      return { usuarios: accion.usuarios, cargando: false, error: null };
    case 'CARGA_FALLIDA':
      return { ...estado, cargando: false, error: accion.error };
    case 'CREADO':
      return { ...estado, usuarios: [...estado.usuarios, accion.usuario] };
    case 'ACTUALIZADO':
      return {
        ...estado,
        usuarios: estado.usuarios.map((u) => (u.id === accion.usuario.id ? accion.usuario : u)),
      };
    case 'ELIMINADO':
      return { ...estado, usuarios: estado.usuarios.filter((u) => u.id !== accion.id) };
  }
}

/**
 * Gestión de estado del mantenedor: mantiene la lista sincronizada con la API.
 * Las operaciones propagan el error para que la vista decida cómo mostrarlo.
 */
export function useUsuarios() {
  const [estado, dispatch] = useReducer(reducer, ESTADO_INICIAL);

  const recargar = useCallback(async () => {
    dispatch({ tipo: 'CARGA_INICIADA' });
    try {
      dispatch({ tipo: 'CARGA_EXITOSA', usuarios: await usuariosApi.listar() });
    } catch (error) {
      dispatch({
        tipo: 'CARGA_FALLIDA',
        error: error instanceof Error ? error.message : 'No fue posible cargar los usuarios',
      });
    }
  }, []);

  useEffect(() => {
    void recargar();
  }, [recargar]);

  const crear = useCallback(async (payload: UsuarioPayload) => {
    dispatch({ tipo: 'CREADO', usuario: await usuariosApi.crear(payload) });
  }, []);

  const actualizar = useCallback(async (id: number, payload: UsuarioPayload) => {
    dispatch({ tipo: 'ACTUALIZADO', usuario: await usuariosApi.actualizar(id, payload) });
  }, []);

  const eliminar = useCallback(async (id: number) => {
    await usuariosApi.eliminar(id);
    dispatch({ tipo: 'ELIMINADO', id });
  }, []);

  return { ...estado, recargar, crear, actualizar, eliminar };
}
