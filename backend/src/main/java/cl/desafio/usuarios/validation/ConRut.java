package cl.desafio.usuarios.validation;

/** Contrato de los objetos que transportan un RUT y su dígito verificador. */
public interface ConRut {

    Long rut();

    String dv();
}
