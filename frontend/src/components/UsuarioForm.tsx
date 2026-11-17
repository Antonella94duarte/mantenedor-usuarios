import { useState, type FormEvent, type InputHTMLAttributes } from 'react';
import { ApiError } from '../api/httpClient';
import type { CampoUsuario, Usuario, UsuarioPayload } from '../types/usuario';
import {
  aPayload,
  desdeUsuario,
  FORMULARIO_VACIO,
  validar,
  type ErroresFormulario,
  type ValoresFormulario,
} from '../utils/usuarioForm';

interface Props {
  /** Usuario a editar; si no se indica, el formulario crea uno nuevo. */
  usuario?: Usuario;
  onGuardar: (payload: UsuarioPayload) => Promise<void>;
  onCancelar: () => void;
}

export function UsuarioForm({ usuario, onGuardar, onCancelar }: Props) {
  const esEdicion = usuario !== undefined;
  const [valores, setValores] = useState<ValoresFormulario>(
    usuario ? desdeUsuario(usuario) : FORMULARIO_VACIO,
  );
  const [errores, setErrores] = useState<ErroresFormulario>({});
  const [errorGeneral, setErrorGeneral] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);

  const cambiar = (campo: CampoUsuario, valor: string) => {
    setValores((previos) => ({ ...previos, [campo]: valor }));
    setErrores((previos) => ({ ...previos, [campo]: undefined }));
  };

  const enviar = async (evento: FormEvent) => {
    evento.preventDefault();
    const encontrados = validar(valores, esEdicion);
    setErrores(encontrados);
    setErrorGeneral(null);
    if (Object.keys(encontrados).length > 0) return;

    setGuardando(true);
    try {
      await onGuardar(aPayload(valores));
    } catch (error) {
      // Errores de validación del backend (400/409) asociados a su campo.
      if (error instanceof ApiError && error.errores.length > 0) {
        setErrores(Object.fromEntries(error.errores.map((e) => [e.campo, e.mensaje])));
      } else {
        setErrorGeneral(error instanceof Error ? error.message : 'No fue posible guardar');
      }
      setGuardando(false);
    }
  };

  const campo = (
    nombre: CampoUsuario,
    etiqueta: string,
    props: InputHTMLAttributes<HTMLInputElement> = {},
  ) => (
    <label className={`campo campo-${nombre}`}>
      <span>{etiqueta}</span>
      <input
        name={nombre}
        value={valores[nombre]}
        onChange={(evento) => cambiar(nombre, evento.target.value)}
        aria-invalid={errores[nombre] ? true : undefined}
        {...props}
      />
      {errores[nombre] && <small className="error-campo">{errores[nombre]}</small>}
    </label>
  );

  return (
    <form className="formulario" onSubmit={enviar} noValidate>
      {errorGeneral && (
        <p className="alerta alerta-error" role="alert">
          {errorGeneral}
        </p>
      )}
      <div className="grilla">
        {campo('nombres', 'Nombres', { autoFocus: true, maxLength: 100 })}
        {campo('apellidos', 'Apellidos', { maxLength: 100 })}
        <div className="fila-rut">
          {campo('rut', 'RUT', { inputMode: 'numeric', placeholder: '12345678', maxLength: 9 })}
          {campo('dv', 'DV', { placeholder: 'K', maxLength: 1 })}
        </div>
        {campo('fechaNacimiento', 'Fecha de nacimiento', { type: 'date' })}
        {campo('correoElectronico', 'Correo electrónico', { type: 'email', maxLength: 150 })}
        {campo('contrasena', esEdicion ? 'Nueva contraseña (opcional)' : 'Contraseña', {
          type: 'password',
          autoComplete: 'new-password',
          placeholder: esEdicion ? 'Dejar en blanco para conservarla' : 'Mínimo 8 caracteres',
        })}
      </div>
      <footer className="acciones">
        <button type="button" className="boton" onClick={onCancelar} disabled={guardando}>
          Cancelar
        </button>
        <button type="submit" className="boton boton-primario" disabled={guardando}>
          {guardando ? 'Guardando…' : esEdicion ? 'Guardar cambios' : 'Crear usuario'}
        </button>
      </footer>
    </form>
  );
}
