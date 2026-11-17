package cl.desafio.usuarios.config;

import cl.desafio.usuarios.dto.UsuarioCreateRequest;
import cl.desafio.usuarios.service.UsuarioService;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Carga usuarios de ejemplo para que el mantenedor no parta vacío (H2 en memoria). */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataInitializer implements CommandLineRunner {

    private final UsuarioService service;

    public DataInitializer(UsuarioService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        service.crear(new UsuarioCreateRequest("María José", "González Rojas", 12345678L, "5",
                LocalDate.of(1990, 5, 17), "maria.gonzalez@example.com", "Secreta.123"));
        service.crear(new UsuarioCreateRequest("Pedro Antonio", "Soto Muñoz", 11111111L, "1",
                LocalDate.of(1985, 11, 2), "pedro.soto@example.com", "Secreta.123"));
    }
}
