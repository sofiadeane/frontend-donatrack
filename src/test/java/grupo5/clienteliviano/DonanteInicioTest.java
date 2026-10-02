package grupo5.clienteliviano;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import grupo5.clienteliviano.aplicacion.PanelDonanteService;
import grupo5.clienteliviano.integracion.Servicio;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.error.BackendException.Tipo;
import grupo5.clienteliviano.integracion.error.ErrorBackend;
import grupo5.clienteliviano.integracion.incentivos.IncentivosPort;
import grupo5.clienteliviano.integracion.notificaciones.NotificacionesPort;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Inicio del donante (tablero): cada bloque carga por separado; si un servicio falla, el resto
 * sigue.
 */
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
  @Import(LandingTest.RelojFijo.class)
  class ConFixtures {
    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("Comparación con el mes pasado: más, menos o igual")
    void comparacion() {
      org.assertj.core.api.Assertions.assertThat(PanelDonanteService.comparacion(3))
          .isEqualTo("+3 respecto al mes pasado");
      org.assertj.core.api.Assertions.assertThat(PanelDonanteService.comparacion(-2))
          .isEqualTo("−2 respecto al mes pasado");
      org.assertj.core.api.Assertions.assertThat(PanelDonanteService.comparacion(0))
          .isEqualTo("¡Igual que el mes pasado!");
    }

    @Test
    @DisplayName("Tablero: cifras, mini gráfico, categoría, recorrido, misión y notificaciones")
    void tablero() throws Exception {
      mvc.perform(get("/donante").session(ingresar(mvc)))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("Hola, Nicolás")))
          .andExpect(content().string(containsString("Tu última donación ya está en el depósito")))
          .andExpect(
              content()
                  .string(
                      containsString(
                          "Donaciones por mes: mayo 0, junio 0, julio 1, agosto 1, septiembre 4, octubre 1")))
          .andExpect(content().string(containsString("−3 respecto al mes pasado")))
          .andExpect(content().string(containsString("2 entregadas")))
          .andExpect(content().string(containsString("3 en proceso")))
          .andExpect(content().string(containsString("1 sin entregar")))
          .andExpect(content().string(containsString("Colaborador")))
          .andExpect(content().string(containsString("Sostenedor")))
          .andExpect(content().string(containsString("Lentejas secas en bolsas de 1 kg")))
          .andExpect(content().string(containsString("recorrido__paso--actual")))
          .andExpect(content().string(containsString("Donante constante")))
          .andExpect(content().string(containsString("aria-valuenow=\"66\"")))
          .andExpect(content().string(containsString("El camión salió a entregar")))
          .andExpect(content().string(containsString("hace 3 h")))
          .andExpect(content().string(containsString("Datos de demostración")));
    }
  }

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  class ConIncentivosYNotificacionesCaidos {
    @Autowired private MockMvc mvc;
    @MockitoBean private IncentivosPort incentivos;
    @MockitoBean private NotificacionesPort notificaciones;

    @Test
    @DisplayName("Si incentivos y notificaciones fallan, el recorrido y las entregas siguen")
    void parcial() throws Exception {
      BackendException caido =
          new BackendException(Servicio.INCENTIVOS, Tipo.NO_DISPONIBLE, 0, null, null);
      when(incentivos.perfil(any())).thenThrow(caido);
      when(notificaciones.dePersona(any()))
          .thenThrow(
              new BackendException(Servicio.NOTIFICACIONES, Tipo.NO_DISPONIBLE, 0, null, null));
      mvc.perform(get("/donante").session(ingresar(mvc)))
          .andExpect(status().isOk())
          .andExpect(
              content()
                  .string(containsString("No pudimos conectarnos con el servicio de incentivos")))
          .andExpect(
              content()
                  .string(
                      containsString("No pudimos conectarnos con el servicio de notificaciones")))
          .andExpect(content().string(containsString("Lentejas secas en bolsas de 1 kg")))
          .andExpect(content().string(containsString("2 entregadas")))
          .andExpect(content().string(not(containsString("kpi--categoria"))));
    }

    @Test
    @DisplayName("Donante que todavía no está en incentivos: mensaje explicativo, no error")
    void sinIncentivos() throws Exception {
      when(incentivos.perfil(any())).thenReturn(Optional.empty());
      when(incentivos.metricas(any())).thenReturn(Optional.empty());
      when(notificaciones.dePersona(any())).thenReturn(List.of());
      mvc.perform(get("/donante").session(ingresar(mvc)))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("Tus totales aparecen cuando registres")))
          .andExpect(content().string(containsString("No tenés notificaciones")));
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
