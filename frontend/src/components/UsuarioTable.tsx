import type { Usuario } from '../types/usuario';
import { formatearRut } from '../utils/rut';

interface Props {
  usuarios: Usuario[];
  onEditar: (usuario: Usuario) => void;
  onEliminar: (usuario: Usuario) => void;
}

/** yyyy-MM-dd → dd-MM-yyyy, sin pasar por Date para evitar desfases de zona horaria. */
function formatearFecha(iso: string): string {
  return iso.split('-').reverse().join('-');
}

export function UsuarioTable({ usuarios, onEditar, onEliminar }: Props) {
  if (usuarios.length === 0) {
    return <p className="vacio">Aún no hay usuarios registrados.</p>;
  }

  return (
    <div className="tabla-contenedor">
      <table className="tabla">
        <thead>
          <tr>
            <th>Nombres</th>
            <th>Apellidos</th>
            <th>RUT</th>
            <th>Fecha de nacimiento</th>
            <th>Correo electrónico</th>
            <th aria-label="Acciones" />
          </tr>
        </thead>
        <tbody>
          {usuarios.map((usuario) => (
            <tr key={usuario.id}>
              <td>{usuario.nombres}</td>
              <td>{usuario.apellidos}</td>
              <td className="sin-corte">{formatearRut(usuario.rut, usuario.dv)}</td>
              <td className="sin-corte">{formatearFecha(usuario.fechaNacimiento)}</td>
              <td>{usuario.correoElectronico}</td>
              <td className="celda-acciones">
                <button type="button" className="boton boton-chico" onClick={() => onEditar(usuario)}>
                  Editar
                </button>
                <button
                  type="button"
                  className="boton boton-chico boton-peligro"
                  onClick={() => onEliminar(usuario)}
                >
                  Eliminar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
