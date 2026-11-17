package cl.desafio.usuarios.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RutTest {

    @ParameterizedTest
    @CsvSource({"12345678,5", "11111111,1", "22222222,2", "6,K", "9,4", "14,0"})
    void calculaElDigitoVerificador(long rut, String dvEsperado) {
        assertThat(Rut.calcularDv(rut)).isEqualTo(dvEsperado);
    }

    @Test
    void aceptaLaKEnMinuscula() {
        assertThat(Rut.esValido(6L, "k")).isTrue();
    }

    @Test
    void rechazaDigitoIncorrectoYValoresNulos() {
        assertThat(Rut.esValido(12345678L, "9")).isFalse();
        assertThat(Rut.esValido(null, "5")).isFalse();
        assertThat(Rut.esValido(12345678L, null)).isFalse();
        assertThat(Rut.esValido(-1L, "5")).isFalse();
    }
}
