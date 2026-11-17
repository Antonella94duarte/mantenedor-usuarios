package cl.desafio.usuarios.exception;

/** Se lanza cuando el RUT o el correo ya pertenecen a otro usuario. */
public class RecursoDuplicadoException extends RuntimeException {

    private final String campo;

    public RecursoDuplicadoException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
