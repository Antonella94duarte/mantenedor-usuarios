import type { CampoUsuario, Usuario, UsuarioPayload } from '../types/usuario';
import { esRutValido } from './rut';

/** Valores del formulario: todo como texto, tal como lo entregan los inputs. */
export type ValoresFormulario = Record<CampoUsuario, string>;
export type ErroresFormulario = Partial<Record<CampoUsuario, string>>;

export const FORMULARIO_VACIO: ValoresFormulario = {
  nombres: '',
  apellidos: '',
  rut: '',
  dv: '',
  fechaNacimiento: '',
  correoElectronico: '',
  contrasena: '',
};

export function desdeUsuario(usuario: Usuario): ValoresFormulario {
  return {
    nombres: usuario.nombres,
    apellidos: usuario.apellidos,
    rut: String(usuario.rut),
    dv: usuario.dv,
    fechaNacimiento: usuario.fechaNacimiento,
    correoElectronico: usuario.correoElectronico,
    contrasena: '',
  };
}

export function aPayload(valores: ValoresFormulario): UsuarioPayload {
  return {
    nombres: valores.nombres.trim(),
    apellidos: valores.apellidos.trim(),
    rut: Number(valores.rut),
    dv: valores.dv.trim().toUpperCase(),
    fechaNacimiento: valores.fechaNacimiento,
    correoElectronico: valores.correoElectronico.trim(),
    // Vacía en edición = conservar la contraseña actual.
    ...(valores.contrasena ? { contrasena: valores.contrasena } : {}),
  };
}

/** Mismas reglas que valida el backend, para dar respuesta inmediata. */
export function validar(valores: ValoresFormulario, esEdicion: boolean): ErroresFormulario {
  const errores: ErroresFormulario = {};
  const hoy = new Date().toISOString().slice(0, 10);

  if (!valores.nombres.trim()) errores.nombres = 'Los nombres son obligatorios';
  if (!valores.apellidos.trim()) errores.apellidos = 'Los apellidos son obligatorios';

  if (!/^\d{1,9}$/.test(valores.rut)) {
    errores.rut = 'Ingresa el RUT solo con números, sin puntos';
  }
  if (!/^[0-9kK]$/.test(valores.dv.trim())) {
    errores.dv = 'Debe ser un número o K';
  } else if (!errores.rut && !esRutValido(Number(valores.rut), valores.dv)) {
    errores.dv = 'El dígito verificador no corresponde al RUT';
  }

  if (!valores.fechaNacimiento) {
    errores.fechaNacimiento = 'La fecha de nacimiento es obligatoria';
  } else if (valores.fechaNacimiento >= hoy) {
    errores.fechaNacimiento = 'La fecha de nacimiento debe ser anterior a hoy';
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valores.correoElectronico.trim())) {
    errores.correoElectronico = 'Ingresa un correo electrónico válido';
  }

  const largo = valores.contrasena.length;
  if (!esEdicion && largo === 0) {
    errores.contrasena = 'La contraseña es obligatoria';
  } else if (largo > 0 && (largo < 8 || largo > 72)) {
    errores.contrasena = 'La contraseña debe tener entre 8 y 72 caracteres';
  }

  return errores;
}
