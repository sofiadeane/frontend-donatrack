package grupo5.clienteliviano;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import grupo5.clienteliviano.integracion.Servicio;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.error.BackendException.Tipo;
import grupo5.clienteliviano.integracion.error.ErrorBackend;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Inicio del donante: estados OK (fixtures), vacío y error, sin romper el resto de la página. */
class DonanteInicioTest {

  static MockHttpSession ingresar(MockMvc mvc) throws Exception {
    return (MockHttpSession)
        mvc.perform(post("/ingresar").param("identidad", "donante-nicolas"))
            .andReturn()
            .getRequest()
            .getSession(false);
  }

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  class ConFixtures {
    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("Muestra las 3 últimas donaciones, con estado y etiqueta de demostración")
    void ultimas() throws Exception {
      mvc.perform(get("/donante").session(ingresar(mvc)))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("Sillas de madera para comedor")))
          .andExpect(content().string(containsString("Arroz largo fino en paquetes de 1 kg")))
          .andExpect(content().string(containsString("Arroz · 50 kg")))
          .andExpect(content().string(containsString("estado-pill--en-camino")))
          .andExpect(content().string(containsString("Datos de demostración")))
          .andExpect(content().string(not(containsString("Cuadernos rayados"))));
    }
  }

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  class ConServicioCaido {
    @Autowired private MockMvc mvc;
    @MockitoBean private DonacionesPort donaciones;

    @Test
    @DisplayName("Si donaciones-service falla, la sección muestra el error y la página sigue")
    void error() throws Exception {
      when(donaciones.donacionesIndependientes(any()))
          .thenThrow(
              new BackendException(
                  Servicio.DONACIONES,
                  Tipo.NO_DISPONIBLE,
                  0,
                  new ErrorBackend("ERR-CSR-500", "X", "detalle técnico", "traza42", null, null),
                  null));
      mvc.perform(get("/donante").session(ingresar(mvc)))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("Hola, Nicolás")))
          .andExpect(
              content()
                  .string(containsString("No pudimos conectarnos con el servicio de donaciones")))
          .andExpect(content().string(containsString("traza42")))
          .andExpect(content().string(containsString("role=\"alert\"")))
          .andExpect(content().string(not(containsString("detalle técnico"))));
    }
  }

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  class SinDonaciones {
    @Autowired private MockMvc mvc;
    @MockitoBean private DonacionesPort donaciones;

    @Test
    @DisplayName("Sin donaciones, muestra el estado vacío")
    void vacio() throws Exception {
      when(donaciones.donacionesIndependientes(any())).thenReturn(List.of());
      mvc.perform(get("/donante").session(ingresar(mvc)))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("Todavía no registraste donaciones")));
    }
  }
}
