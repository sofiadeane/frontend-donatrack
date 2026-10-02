package grupo5.clienteliviano;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import grupo5.clienteliviano.integracion.Servicio;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.error.BackendException.Tipo;
import grupo5.clienteliviano.integracion.error.ErrorBackend;
import grupo5.clienteliviano.integracion.error.ErrorBackend.ErrorDeCampo;
import grupo5.clienteliviano.integracion.personas.RegistroPort;
import grupo5.clienteliviano.integracion.personas.dto.RegistroDTOs.PersonaCreadaDTO;
import grupo5.clienteliviano.integracion.personas.dto.TipoPersona;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Registro (F05, J1): tipo preseleccionado, errores por campo, representantes y paso parcial. */
class RegistroTest {

  static MockHttpServletRequestBuilder personaValida() {
    return post("/registro")
        .param("accion", "crear")
        .param("tipo", "humana")
        .param("nombre", "Lucía")
        .param("apellido", "Ferreyra")
        .param("correo", "lucia@ejemplo.org")
        .param("preferido", "CORREO")
        .param("pais", "Argentina")
        .param("aceptaPrivacidad", "true");
  }

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  class ConFixtures {
    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("CA1: el tipo elegido en el CTA llega preseleccionado")
    void preseleccion() throws Exception {
      mvc.perform(get("/registro").param("tipo", "entidad"))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("id=\"tipo-entidad\" checked")))
          .andExpect(content().string(containsString("Modo demostración: no se guarda nada")));
    }

    @Test
    @DisplayName("CA2: sin datos, errores por campo vinculados y secciones marcadas en rojo")
    void errores() throws Exception {
      mvc.perform(post("/registro").param("tipo", "juridica").param("accion", "crear"))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("Revisá 6 datos antes de continuar")))
          .andExpect(content().string(containsString("id=\"error-razonSocial\"")))
          .andExpect(content().string(containsString("aria-describedby=\"error-razonSocial\"")))
          .andExpect(content().string(containsString("href=\"#rep-0-nombre\"")))
          .andExpect(content().string(containsString("2 datos para revisar")));
    }

    @Test
    @DisplayName("Representantes: se agregan sin perder lo cargado y nunca quedan menos de uno")
    void representantes() throws Exception {
      String agregado =
          mvc.perform(
                  post("/registro")
                      .param("tipo", "juridica")
                      .param("razonSocial", "Comedor Los Aromos")
                      .param("representantes[0].nombre", "Marta")
                      .param("accion", "agregar-representante"))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();
      assertThat(agregado)
          .contains("value=\"Comedor Los Aromos\"", "value=\"Marta\"", "Representante 2")
          .doesNotContain("resumen-errores");

      mvc.perform(
              post("/registro")
                  .param("tipo", "juridica")
                  .param("representantes[0].nombre", "Marta")
                  .param("quitar", "0"))
          .andExpect(content().string(containsString("value=\"Marta\"")))
          .andExpect(content().string(not(containsString("boton-quitar"))));
    }

    @Test
    @DisplayName("Envío válido: redirige a la confirmación (PRG) con el nombre")
    void exito() throws Exception {
      var resultado =
          mvc.perform(personaValida()).andExpect(redirectedUrl("/registro/listo")).andReturn();
      mvc.perform(get("/registro/listo").flashAttrs(resultado.getFlashMap()))
          .andExpect(content().string(containsString("¡Listo, Lucía!")))
          .andExpect(content().string(containsString("no se guardó nada")));
      mvc.perform(get("/registro/listo")).andExpect(redirectedUrl("/registro"));
    }
  }

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  class ConBackend {
    @Autowired private MockMvc mvc;
    @MockitoBean private RegistroPort registro;

    private static final UUID PERSONA = UUID.fromString("9e000000-0000-4000-8000-000000000001");

    @Test
    @DisplayName("Errores del backend por campo vuelven al formulario en su campo")
    void validacionBackend() throws Exception {
      when(registro.crearPersona(any()))
          .thenThrow(
              new BackendException(
                  Servicio.DONACIONES,
                  Tipo.VALIDACION,
                  400,
                  new ErrorBackend(
                      "ERR-CSR-003",
                      "MethodArgumentNotValidException",
                      null,
                      "t-1",
                      null,
                      List.of(
                          new ErrorDeCampo(
                              "mediosDeContacto[0].direccionCorreo",
                              "Formato de correo electrónico inválido",
                              "x"))),
                  null));
      mvc.perform(personaValida())
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("Formato de correo electrónico inválido.")))
          .andExpect(content().string(containsString("aria-describedby=\"error-correo\"")));
    }

    @Test
    @DisplayName("CA3: si falla el alta de donante, se informa y se reintenta solo ese paso")
    void parcial() throws Exception {
      when(registro.crearPersona(any()))
          .thenReturn(new PersonaCreadaDTO(TipoPersona.HUMANA, PERSONA));
      when(registro.crearDonante(PERSONA))
          .thenThrow(new BackendException(Servicio.DONACIONES, Tipo.NO_DISPONIBLE, 0, null, null))
          .thenReturn(UUID.randomUUID());
      MockHttpSession sesion = new MockHttpSession();

      mvc.perform(personaValida().session(sesion)).andExpect(redirectedUrl("/registro/pendiente"));
      mvc.perform(get("/registro/pendiente").session(sesion))
          .andExpect(content().string(containsString("Falta un paso")))
          .andExpect(content().string(containsString("perfil de persona donante")));
      mvc.perform(post("/registro/reintentar").session(sesion))
          .andExpect(redirectedUrl("/registro/listo"));
      assertThat(sesion.getAttribute("registroPendiente")).isNull();
    }

    @Test
    @DisplayName("Si no se puede crear la persona, el formulario conserva los datos y avisa")
    void caido() throws Exception {
      when(registro.crearPersona(any()))
          .thenThrow(new BackendException(Servicio.DONACIONES, Tipo.NO_DISPONIBLE, 0, null, null));
      mvc.perform(personaValida())
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("No pudimos conectarnos")))
          .andExpect(content().string(containsString("value=\"Ferreyra\"")));
    }
  }
}
