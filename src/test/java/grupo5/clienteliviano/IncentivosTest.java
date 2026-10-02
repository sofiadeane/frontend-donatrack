package grupo5.clienteliviano;

import static grupo5.clienteliviano.DonanteInicioTest.ingresar;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import grupo5.clienteliviano.integracion.Servicio;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.error.BackendException.Tipo;
import grupo5.clienteliviano.integracion.incentivos.IncentivosPort;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Panel de incentivos (H2.4, J3). */
class IncentivosTest {

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  class ConFixtures {
    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("CA1: categoría, recorrido de categorías y misión activa con progreso")
    void categoriaYMision() throws Exception {
      mvc.perform(get("/donante/incentivos").session(ingresar(mvc)))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("Sos Colaborador")))
          .andExpect(
              content()
                  .string(
                      stringContainsInOrder(
                          "ruta-categorias__paso--actual",
                          "Colaborador",
                          "ruta-categorias__paso--siguiente",
                          "Sostenedor")))
          .andExpect(content().string(containsString("Donante constante")))
          .andExpect(content().string(containsString("aria-valuenow=\"66\"")))
          .andExpect(content().string(containsString("falta 1 paso")))
          .andExpect(content().string(containsString("#7")));
    }

    @Test
    @DisplayName(
        "CA2: insignias ganadas y pendientes se distinguen por texto e ícono, no solo color")
    void insignias() throws Exception {
      mvc.perform(get("/donante/incentivos").session(ingresar(mvc)))
          .andExpect(content().string(containsString("insignia--ganada")))
          .andExpect(content().string(containsString("Ganada el 20 abr 2026")))
          .andExpect(content().string(containsString("insignia--pendiente")))
          .andExpect(content().string(containsString("Pendiente · Donante constante")))
          .andExpect(
              content()
                  .string(
                      stringContainsInOrder(
                          "Colaborador", "Tu categoría", "Sostenedor", "Próxima")));
    }
  }

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  class ConBackendSimulado {
    @Autowired private MockMvc mvc;
    @MockitoBean private IncentivosPort incentivos;

    @Test
    @DisplayName("CA3: si el donante no existe en incentivos (404), estado vacío explicativo")
    void sinIncentivos() throws Exception {
      when(incentivos.perfil(any())).thenReturn(Optional.empty());
      mvc.perform(get("/donante/incentivos").session(ingresar(mvc)))
          .andExpect(status().isOk())
          .andExpect(
              content().string(containsString("Tus incentivos empiezan con tu primera donación")))
          .andExpect(content().string(not(containsString("role=\"alert\""))));
    }

    @Test
    @DisplayName("Si incentivos-service no responde, se informa sin romper la página")
    void caido() throws Exception {
      when(incentivos.perfil(any()))
          .thenThrow(new BackendException(Servicio.INCENTIVOS, Tipo.NO_DISPONIBLE, 0, null, null));
      mvc.perform(get("/donante/incentivos").session(ingresar(mvc)))
          .andExpect(status().isOk())
          .andExpect(
              content()
                  .string(containsString("No pudimos conectarnos con el servicio de incentivos")));
    }
  }
}
