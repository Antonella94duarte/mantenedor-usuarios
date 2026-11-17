import { useState } from 'react';
import type { Usuario } from '../types/usuario';
import { Modal } from './Modal';

interface Props {
  usuario: Usuario;
  onConfirmar: () => Promise<void>;
  onCancelar: () => void;
}

export function ConfirmarEliminacion({ usuario, onConfirmar, onCancelar }: Props) {
  const [eliminando, setEliminando] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const confirmar = async () => {
    setEliminando(true);
    setError(null);
    try {
      await onConfirmar();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No fue posible eliminar');
      setEliminando(false);
    }
  };

  return (
    <Modal titulo="Eliminar usuario" onCerrar={onCancelar}>
      <div className="formulario">
        {error && (
          <p className="alerta alerta-error" role="alert">
            {error}
          </p>
        )}
        <p>
          ¿Eliminar a{' '}
          <strong>
            {usuario.nombres} {usuario.apellidos}
          </strong>
          ? Esta acción no se puede deshacer.
        </p>
        <footer className="acciones">
          <button type="button" className="boton" onClick={onCancelar} disabled={eliminando}>
            Cancelar
          </button>
          <button
            type="button"
            className="boton boton-peligro-solido"
            onClick={confirmar}
            disabled={eliminando}
          >
            {eliminando ? 'Eliminando…' : 'Eliminar'}
          </button>
        </footer>
      </div>
    </Modal>
  );
}
