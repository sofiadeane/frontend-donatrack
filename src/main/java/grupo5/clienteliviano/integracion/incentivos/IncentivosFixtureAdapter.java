package grupo5.clienteliviano.integracion.incentivos;

import grupo5.clienteliviano.integracion.fixtures.LectorFixtures;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.CambioCategoriaDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.DonantePerfilDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.MetricasDonanteDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.MisionDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Adapter de demostración: perfil y métricas por donante desde fixtures. */
public class IncentivosFixtureAdapter implements IncentivosPort {

  static final String ARCHIVO = "incentivos/donantes.json";

  /** Registro del fixture: perfil y métricas tal como las devuelve el backend. */
  public record RegistroFixture(
      DonantePerfilDTO perfil,
      MetricasDonanteDTO metricas,
      List<MisionDTO> misiones,
      List<CambioCategoriaDTO> ascensos) {}

  private final List<RegistroFixture> registros;

  public IncentivosFixtureAdapter(LectorFixtures lector) {
    this.registros = lector.lista(ARCHIVO, RegistroFixture.class);
  }

  @Override
  public Optional<DonantePerfilDTO> perfil(UUID donanteId) {
    return registro(donanteId).map(RegistroFixture::perfil);
  }

  @Override
  public Optional<MetricasDonanteDTO> metricas(UUID donanteId) {
    return registro(donanteId).map(RegistroFixture::metricas);
  }

  @Override
  public List<MisionDTO> misiones(UUID donanteId) {
    return registro(donanteId).map(RegistroFixture::misiones).orElse(List.of());
  }

  @Override
  public List<CambioCategoriaDTO> ascensos(UUID donanteId) {
    return registro(donanteId).map(RegistroFixture::ascensos).orElse(List.of());
  }

  private Optional<RegistroFixture> registro(UUID donanteId) {
    return registros.stream().filter(r -> r.perfil().donanteId().equals(donanteId)).findFirst();
  }

  @Override
  public boolean esDemo() {
    return true;
  }
}
