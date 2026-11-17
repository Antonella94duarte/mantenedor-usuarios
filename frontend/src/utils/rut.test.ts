import { describe, expect, it } from 'vitest';
import { calcularDv, esRutValido, formatearRut } from './rut';

describe('rut', () => {
  it.each([
    [12345678, '5'],
    [11111111, '1'],
    [6, 'K'],
    [14, '0'],
  ])('calcula el dígito verificador de %i', (rut, dv) => {
    expect(calcularDv(rut)).toBe(dv);
  });

  it('valida la combinación rut + dv sin distinguir mayúsculas', () => {
    expect(esRutValido(6, 'k')).toBe(true);
    expect(esRutValido(12345678, '9')).toBe(false);
    expect(esRutValido(0, '0')).toBe(false);
  });

  it('formatea con puntos y guion', () => {
    expect(formatearRut(12345678, '5')).toBe('12.345.678-5');
  });
});
