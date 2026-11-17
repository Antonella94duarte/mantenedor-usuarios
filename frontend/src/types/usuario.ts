/** Usuario tal como lo devuelve la API (nunca incluye la contraseña). */
export interface Usuario {
  id: number;
  nombres: string;
  apellidos: string;
  rut: number;
  dv: string;
  /** Fecha ISO 8601 (yyyy-MM-dd). */
  fechaNacimiento: string;
  correoElectronico: string;
}

/** Cuerpo para crear o actualizar. En la actualización la contraseña es opcional. */
export interface UsuarioPayload extends Omit<Usuario, 'id'> {
  contrasena?: string;
}

export type CampoUsuario = keyof UsuarioPayload;
