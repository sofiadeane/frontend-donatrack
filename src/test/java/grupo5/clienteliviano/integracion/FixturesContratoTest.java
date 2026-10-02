package grupo5.clienteliviano.integracion;

import static org.assertj.core.api.Assertions.assertThat;

import grupo5.clienteliviano.integracion.donaciones.DonacionesFixtureAdapter;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort.FiltroDonaciones;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionOutputDTO;
import grupo5.clienteliviano.integracion.fixtures.LectorFixtures;
import grupo5.clienteliviano.integracion.metricas.MetricasPublicasPort.Metrica;
import grupo5.clienteliviano.session.IdentidadDemo;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

/**
 * Los fixtures deben tener exactamente la forma de los DTOs copiados del backend. Si el backend
 * cambia un DTO y se actualiza la copia, este test marca los fixtures que quedaron desalineados.
 */
class FixturesContratoTest {

  private static final UUID NICOLAS = UUID.fromString("a3b40000-0000-4000-8000-000000000001");
  private static final UUID ARROZ = UUID.fromString("5b000000-0000-4000-8000-000000000001");

  private final LectorFixtures lector = new LectorFixtures(JsonMapper.builder().build());

  @Test
  @DisplayName("Los fixtures de donaciones respetan el DTO del backend")
  void donaciones() {
    List<DonacionesFixtureAdapter.RegistroFixture> registros =
        lector.lista(
            "donaciones/donaciones-independientes.json",
            DonacionesFixtureAdapter.RegistroFixture.class);
    assertThat(registros).isNotEmpty();
    assertThat(registros)
        .allSatisfy(
            r -> {
              assertThat(r.donanteId()).isNotNull();
              assertThat(r.donacion().items()).isNotEmpty();
              assertThat(r.donacion().historial().getLast().estadoNuevo())
                  .isEqualTo(r.donacion().estadoActual());
            });
  }

  @Test
  @DisplayName("Cada donación independiente tiene su donación original con persona donante")
  void donacionesOriginales() {
    List<DonacionOutputDTO> originales =
        lector.lista("donaciones/donaciones.json", DonacionOutputDTO.class);
    assertThat(originales).allSatisfy(o -> assertThat(o.donante().persona()).isNotNull());
    DonacionesFixtureAdapter adapter = new DonacionesFixtureAdapter(lector);
    assertThat(adapter.donacionesIndependientes(new FiltroDonaciones(null, null, null)))
        .allSatisfy(d -> assertThat(adapter.donacion(d.donacionOriginalId())).isPresent());
  }

  @Test
  @DisplayName("Las métricas públicas de demostración respetan su formato")
  void metricas() {
    assertThat(lector.lista("metricas/publicas.json", Metrica.class))
        .hasSize(4)
        .allSatisfy(m -> assertThat(m.etiqueta()).isNotBlank());
  }

  @Test
  @DisplayName("Las identidades de demostración respetan su formato")
  void identidades() {
    assertThat(lector.lista("sesion/identidades.json", IdentidadDemo.class)).isNotEmpty();
  }

  @Test
  @DisplayName("El adapter de demostración filtra como el backend")
  void filtros() {
    DonacionesFixtureAdapter adapter = new DonacionesFixtureAdapter(lector);
    assertThat(adapter.donacionesIndependientes(FiltroDonaciones.delDonante(NICOLAS))).hasSize(6);
    assertThat(adapter.donacionesIndependientes(new FiltroDonaciones(NICOLAS, "ENTREGADA", null)))
        .hasSize(2);
    assertThat(adapter.donacionesIndependientes(new FiltroDonaciones(null, null, ARROZ)))
        .singleElement()
        .satisfies(d -> assertThat(d.estadoActual()).isEqualTo("EN_TRASLADO"));
    assertThat(adapter.esDemo()).isTrue();
  }
}
