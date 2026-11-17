package cl.desafio.usuarios.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.desafio.usuarios.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** Prueba de integración del CRUD completo contra H2 en memoria. */
@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerIntegrationTest {

    private static final String URL = "/api/v1/usuarios";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository repository;

    @BeforeEach
    void limpiar() {
        repository.deleteAll();
    }

    @Test
    void cicloCrudCompleto() throws Exception {
        String location = crear(usuario())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(URL + "/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombres").value("María José"))
                .andExpect(jsonPath("$.fechaNacimiento").value("1990-05-17"))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andReturn().getResponse().getHeader("Location");
        String urlUsuario = URL + location.substring(location.lastIndexOf('/'));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        Map<String, Object> cambios = usuario();
        cambios.put("apellidos", "González Soto");
        cambios.remove("contrasena");
        mockMvc.perform(put(urlUsuario).contentType(MediaType.APPLICATION_JSON).content(json(cambios)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellidos").value("González Soto"));

        mockMvc.perform(get(urlUsuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellidos").value("González Soto"));

        mockMvc.perform(delete(urlUsuario)).andExpect(status().isNoContent());
        mockMvc.perform(get(urlUsuario)).andExpect(status().isNotFound());
    }

    @Test
    void laContrasenaSeGuardaCifrada() throws Exception {
        crear(usuario()).andExpect(status().isCreated());

        String almacenada = repository.findAll().get(0).getContrasena();
        org.assertj.core.api.Assertions.assertThat(almacenada).startsWith("$2").isNotEqualTo("Secreta.123");
    }

    @Test
    void rechazaDigitoVerificadorIncorrecto() throws Exception {
        Map<String, Object> usuario = usuario();
        usuario.put("dv", "9");

        crear(usuario)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Solicitud inválida"))
                .andExpect(jsonPath("$.errores[*].campo", hasItem("dv")));
    }

    @Test
    void rechazaCamposObligatoriosFaltantes() throws Exception {
        crear(Map.of("nombres", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[*].campo", hasItem("nombres")))
                .andExpect(jsonPath("$.errores[*].campo", hasItem("correoElectronico")))
                .andExpect(jsonPath("$.errores[*].campo", hasItem("contrasena")));
    }

    @Test
    void rechazaRutDuplicado() throws Exception {
        crear(usuario()).andExpect(status().isCreated());

        Map<String, Object> otro = usuario();
        otro.put("correoElectronico", "otro@example.com");
        crear(otro)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errores[0].campo").value("rut"));
    }

    @Test
    void rechazaCorreoDuplicado() throws Exception {
        crear(usuario()).andExpect(status().isCreated());

        Map<String, Object> otro = usuario();
        otro.put("rut", 11111111);
        otro.put("dv", "1");
        otro.put("correoElectronico", "MARIA.GONZALEZ@example.com");
        crear(otro)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errores[0].campo").value("correoElectronico"));
    }

    @Test
    void actualizarUnUsuarioInexistenteDevuelve404() throws Exception {
        mockMvc.perform(put(URL + "/999").contentType(MediaType.APPLICATION_JSON).content(json(usuario())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Usuario no encontrado"));
    }

    private ResultActions crear(Map<String, Object> usuario) throws Exception {
        return mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json(usuario)));
    }

    private String json(Map<String, Object> cuerpo) throws Exception {
        return objectMapper.writeValueAsString(cuerpo);
    }

    private static Map<String, Object> usuario() {
        Map<String, Object> usuario = new LinkedHashMap<>();
        usuario.put("nombres", "María José");
        usuario.put("apellidos", "González Rojas");
        usuario.put("rut", 12345678);
        usuario.put("dv", "5");
        usuario.put("fechaNacimiento", "1990-05-17");
        usuario.put("correoElectronico", "maria.gonzalez@example.com");
        usuario.put("contrasena", "Secreta.123");
        return usuario;
    }
}
