const BASE_URL: string = import.meta.env.VITE_API_URL ?? '/api/v1';

export interface ErrorCampo {
  campo: string;
  mensaje: string;
}

/** Error de la API (RFC 7807) con el detalle por campo cuando existe. */
export class ApiError extends Error {
  readonly status: number;
  readonly errores: ErrorCampo[];

  constructor(status: number, mensaje: string, errores: ErrorCampo[] = []) {
    super(mensaje);
    this.name = 'ApiError';
    this.status = status;
    this.errores = errores;
  }
}

export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  let response: Response;
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      ...init,
      headers: {
        Accept: 'application/json',
        ...(init.body ? { 'Content-Type': 'application/json' } : {}),
        ...init.headers,
      },
    });
  } catch {
    throw new ApiError(0, 'No fue posible conectar con el servidor');
  }

  if (!response.ok) {
    const problema = await response.json().catch(() => null);
    throw new ApiError(
      response.status,
      problema?.detail ?? `Error inesperado (${response.status})`,
      Array.isArray(problema?.errores) ? problema.errores : [],
    );
  }

  return response.status === 204 ? (undefined as T) : ((await response.json()) as T);
}
