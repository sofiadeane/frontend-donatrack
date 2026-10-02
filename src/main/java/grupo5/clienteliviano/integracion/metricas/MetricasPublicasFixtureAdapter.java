package grupo5.clienteliviano.integracion.metricas;

import grupo5.clienteliviano.integracion.fixtures.LectorFixtures;
import java.util.List;
import org.springframework.stereotype.Component;

/** Métricas ficticias de {@code fixtures/metricas/publicas.json} (cifras del Figma). */
@Component
public class MetricasPublicasFixtureAdapter implements MetricasPublicasPort {

  private final List<Metrica> metricas;

  public MetricasPublicasFixtureAdapter(LectorFixtures lector) {
    this.metricas = lector.lista("metricas/publicas.json", Metrica.class);
  }

  @Override
  public List<Metrica> metricas() {
    return metricas;
  }

  @Override
  public boolean esDemo() {
    return true;
  }
}
