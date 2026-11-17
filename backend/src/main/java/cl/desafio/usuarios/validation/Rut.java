package cl.desafio.usuarios.validation;

/** Utilidades para el RUT chileno (algoritmo módulo 11). */
public final class Rut {

    private Rut() {
    }

    /** Calcula el dígito verificador ("0"-"9" o "K") para el número de RUT dado. */
    public static String calcularDv(long rut) {
        int suma = 0;
        int multiplicador = 2;
        for (long resto = rut; resto > 0; resto /= 10) {
            suma += (int) (resto % 10) * multiplicador;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }
        int resultado = 11 - (suma % 11);
        if (resultado == 11) {
            return "0";
        }
        if (resultado == 10) {
            return "K";
        }
        return String.valueOf(resultado);
    }

    public static boolean esValido(Long rut, String dv) {
        if (rut == null || rut <= 0 || dv == null) {
            return false;
        }
        return calcularDv(rut).equalsIgnoreCase(dv.trim());
    }
}
