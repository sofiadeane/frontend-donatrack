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
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort;
import grupo5.clienteliviano.integracion.donaciones.dto.BienNormalizadoDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.BienResumenDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.CambioEstadoDIResponseDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.CategoriaResumenDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.ItemDonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.SubcategoriaResumenDTO;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.error.BackendException.Tipo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Mis donaciones (H2.1, J2) y detalle con historial (H2.2). */
class MisDonacionesTest {

  private static final String ALIMENTOS = "c1000000-0000-4000-8000-000000000001";
  private static final String FIDEOS = "5b000000-0000-4000-8000-000000000002";
  private static final String CAMPERAS = "5b000000-0000-4000-8000-000000000003";

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  class ConFixtures {
    @Autowired private MockMvc mvc;

    @Test
    @DisplayName("Lista todas mis donaciones y ofrece los 7 estados del backend en español (CA1)")
    void lista() throws Exception {
      mvc.perform(get("/donante/donaciones").session(ingresar(mvc)))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("6 donaciones")))
          .andExpect(
              content()
                  .string(
                      stringContainsInOrder(
                          "En depósito",
                          "Asignada",
                          "Lista para entregar",
                          "En traslado",
                          "Entregada",
                          "Entrega fallida",
                          "Vencida")))
          .andExpect(content().string(not(containsString("Pupitres"))));
    }

    @Test
    @DisplayName("Filtra por estado, por categoría y por subcategoría (CA2, CA3)")
    void filtros() throws Exception {
      var sesion = ingresar(mvc);
      mvc.perform(get("/donante/donaciones").param("estado", "ENTREGADA").session(sesion))
          .andExpect(content().string(containsString("2 donaciones")))
          .andExpect(content().string(containsString("Limpiar filtros")));
      mvc.perform(get("/donante/donaciones").param("categoria", ALIMENTOS).session(sesion))
          .andExpect(content().string(containsString("2 donaciones")))
          .andExpect(content().string(containsString("Arroz largo fino")))
          .andExpect(content().string(containsString("Fideos secos tipo mostachol")));
      mvc.perform(
              get("/donante/donaciones")
                  .param("categoria", ALIMENTOS)
                  .param("subcategoria", FIDEOS)
                  .session(sesion))
          .andExpect(content().string(containsString("1 donación")));
      // Subcategoría de otra categoría: se descarta y queda la categoría sola.
      mvc.perform(
              get("/donante/donaciones")
                  .param("categoria", ALIMENTOS)
                  .param("subcategoria", CAMPERAS)
                  .session(sesion))
          .andExpect(content().string(containsString("2 donaciones")));
      mvc.perform(
              get("/donante/donaciones")
                  .param("estado", "VENCIDA")
                  .param("categoria", ALIMENTOS)
                  .session(sesion))
          .andExpect(content().string(containsString("Ninguna donación coincide")));
    }

    @Test
    @DisplayName("Detalle: historial en orden cronológico y datos del ítem")
    void detalle() throws Exception {
      mvc.perform(
              get("/donante/donaciones/d0000000-0000-4000-8000-000000000001")
                  .session(ingresar(mvc)))
          .andExpect(status().isOk())
          .andExpect(
              content()
                  .string(
                      stringContainsInOrder(
                          "En depósito", "Asignada", "Lista para entregar", "En traslado")))
          .andExpect(content().string(containsString("aria-current=\"step\"")))
          .andExpect(content().string(containsString("50 kg")));
    }

    @Test
    @DisplayName("No se puede ver una donación ajena ni un id inválido")
    void ajena() throws Exception {
      var sesion = ingresar(mvc);
      mvc.perform(get("/donante/donaciones/d0000000-0000-4000-8000-000000000008").session(sesion))
          .andExpect(status().isNotFound());
      mvc.perform(get("/donante/donaciones/no-es-un-id").session(sesion))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  class ConBackendSimulado {
    @Autowired private MockMvc mvc;
    @MockitoBean private DonacionesPort donaciones;

    private static final UUID ID = UUID.fromString("d0000000-0000-4000-8000-0000000000aa");

    @Test
    @DisplayName("ENTREGA_FALLIDA muestra la justificación (CA4)")
    void fallida() throws Exception {
      var categoria = new CategoriaResumenDTO(UUID.randomUUID(), "Alimentos", "KILOGRAMO");
      var bien =
          new BienNormalizadoDTO(
              new BienResumenDTO("Arroz", null, null, "NUEVO"),
              new SubcategoriaResumenDTO(UUID.randomUUID(), "Arroz"),
              categoria);
      var donacion =
          new DonacionIndependienteResponseDTO(
              ID,
              null,
              "Arroz",
              "ENTREGA_FALLIDA",
              LocalDateTime.parse("2026-09-01T10:00:00"),
              List.of(
                  new CambioEstadoDIResponseDTO(
                      "EN_TRASLADO",
                      "ENTREGA_FALLIDA",
                      LocalDateTime.parse("2026-09-03T10:00:00"),
                      "La entidad estaba cerrada",
                      "logistica"),
                  new CambioEstadoDIResponseDTO(
                      null, "EN_DEPOSITO", LocalDateTime.parse("2026-09-01T10:00:00"), null, "x")),
              List.of(new ItemDonacionIndependienteResponseDTO(bien, 10)),
              10);
      when(donaciones.donacionesIndependientes(any())).thenReturn(List.of(donacion));
      when(donaciones.categorias()).thenReturn(List.of());

      mvc.perform(get("/donante/donaciones/" + ID).session(ingresar(mvc)))
          .andExpect(
              content()
                  .string(
                      stringContainsInOrder(
                          "En depósito", "Entrega fallida", "La entidad estaba cerrada")));
    }

    @Test
    @DisplayName("Si el servicio falla, lista y detalle muestran el error sin romper la página")
    void caido() throws Exception {
      when(donaciones.donacionesIndependientes(any()))
          .thenThrow(new BackendException(Servicio.DONACIONES, Tipo.NO_DISPONIBLE, 0, null, null));
      when(donaciones.categorias())
          .thenThrow(new BackendException(Servicio.DONACIONES, Tipo.NO_DISPONIBLE, 0, null, null));
      var sesion = ingresar(mvc);
      mvc.perform(get("/donante/donaciones").session(sesion))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("No pudimos conectarnos")))
          .andExpect(content().string(not(containsString("f-categoria"))));
      mvc.perform(get("/donante/donaciones/" + ID).session(sesion))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("No pudimos conectarnos")));
    }
  }
}
