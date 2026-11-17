import { useCallback, useEffect, useState } from 'react';
import { ConfirmarEliminacion } from './components/ConfirmarEliminacion';
import { Modal } from './components/Modal';
import { UsuarioForm } from './components/UsuarioForm';
import { UsuarioTable } from './components/UsuarioTable';
import { useUsuarios } from './hooks/useUsuarios';
import type { Usuario, UsuarioPayload } from './types/usuario';

/** Qué diálogo está abierto en la página. */
type Dialogo =
  | { tipo: 'crear' }
  | { tipo: 'editar'; usuario: Usuario }
  | { tipo: 'eliminar'; usuario: Usuario }
  | null;

export default function App() {
  const { usuarios, cargando, error, recargar, crear, actualizar, eliminar } = useUsuarios();
  const [dialogo, setDialogo] = useState<Dialogo>(null);
  const [aviso, setAviso] = useState<string | null>(null);

  const cerrar = useCallback(() => setDialogo(null), []);

  useEffect(() => {
    if (!aviso) return;
    const temporizador = setTimeout(() => setAviso(null), 4000);
    return () => clearTimeout(temporizador);
  }, [aviso]);

  const guardar = async (payload: UsuarioPayload) => {
    if (dialogo?.tipo === 'editar') {
      await actualizar(dialogo.usuario.id, payload);
      setAviso('Usuario actualizado');
    } else {
      await crear(payload);
      setAviso('Usuario creado');
    }
    cerrar();
  };

  const confirmarEliminacion = async () => {
    if (dialogo?.tipo !== 'eliminar') return;
    await eliminar(dialogo.usuario.id);
    setAviso('Usuario eliminado');
    cerrar();
  };

  return (
    <main className="pagina">
      <header className="cabecera">
        <div>
          <h1>Mantenedor de usuarios</h1>
          <p className="subtitulo">
            {cargando ? 'Cargando…' : `${usuarios.length} usuario${usuarios.length === 1 ? '' : 's'}`}
          </p>
        </div>
        <button type="button" className="boton boton-primario" onClick={() => setDialogo({ tipo: 'crear' })}>
          Nuevo usuario
        </button>
      </header>

      {aviso && (
        <p className="alerta alerta-exito" role="status">
          {aviso}
        </p>
      )}

      {error ? (
        <div className="alerta alerta-error" role="alert">
          <span>{error}</span>
          <button type="button" className="boton boton-chico" onClick={() => void recargar()}>
            Reintentar
          </button>
        </div>
      ) : (
        !cargando && (
          <UsuarioTable
            usuarios={usuarios}
            onEditar={(usuario) => setDialogo({ tipo: 'editar', usuario })}
            onEliminar={(usuario) => setDialogo({ tipo: 'eliminar', usuario })}
          />
        )
      )}

      {(dialogo?.tipo === 'crear' || dialogo?.tipo === 'editar') && (
        <Modal titulo={dialogo.tipo === 'editar' ? 'Editar usuario' : 'Nuevo usuario'} onCerrar={cerrar}>
          <UsuarioForm
            usuario={dialogo.tipo === 'editar' ? dialogo.usuario : undefined}
            onGuardar={guardar}
            onCancelar={cerrar}
          />
        </Modal>
      )}

      {dialogo?.tipo === 'eliminar' && (
        <ConfirmarEliminacion usuario={dialogo.usuario} onConfirmar={confirmarEliminacion} onCancelar={cerrar} />
      )}
    </main>
  );
}
