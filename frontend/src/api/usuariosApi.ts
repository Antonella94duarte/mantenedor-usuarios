import type { Usuario, UsuarioPayload } from '../types/usuario';
import { request } from './httpClient';

const RECURSO = '/usuarios';

/** Servicio de acceso a la API de usuarios. */
export const usuariosApi = {
  listar: () => request<Usuario[]>(RECURSO),

  obtener: (id: number) => request<Usuario>(`${RECURSO}/${id}`),

  crear: (usuario: UsuarioPayload) =>
    request<Usuario>(RECURSO, { method: 'POST', body: JSON.stringify(usuario) }),

  actualizar: (id: number, usuario: UsuarioPayload) =>
    request<Usuario>(`${RECURSO}/${id}`, { method: 'PUT', body: JSON.stringify(usuario) }),

  eliminar: (id: number) => request<void>(`${RECURSO}/${id}`, { method: 'DELETE' }),
};
