/** Calcula el dígito verificador de un RUT chileno (módulo 11). */
export function calcularDv(rut: number): string {
  let suma = 0;
  let multiplicador = 2;
  for (let resto = Math.trunc(rut); resto > 0; resto = Math.trunc(resto / 10)) {
    suma += (resto % 10) * multiplicador;
    multiplicador = multiplicador === 7 ? 2 : multiplicador + 1;
  }
  const resultado = 11 - (suma % 11);
  if (resultado === 11) return '0';
  if (resultado === 10) return 'K';
  return String(resultado);
}

export function esRutValido(rut: number, dv: string): boolean {
  return Number.isInteger(rut) && rut > 0 && calcularDv(rut) === dv.trim().toUpperCase();
}

/** 12345678 + "5" → "12.345.678-5" */
export function formatearRut(rut: number, dv: string): string {
  return `${rut.toLocaleString('es-CL')}-${dv.toUpperCase()}`;
}
