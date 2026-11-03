package grupo5.clienteliviano.integracion.entidades;

import grupo5.clienteliviano.integracion.fixtures.LectorFixtures;
import java.util.List;
import org.springframework.stereotype.Component;

/** Entidades y asignaciones ficticias de {@code fixtures/entidades/}. */
@Component
public class EntidadesFixtureAdapter implements EntidadesPort {

  private final List<Entidad> entidades;
  private final List<Asignacion> asignaciones;

  public EntidadesFixtureAdapter(LectorFixtures lector) {
    this.entidades = lector.lista("entidades/entidades.json", Entidad.class);
    this.asignaciones = lector.lista("entidades/asignaciones.json", Asignacion.class);
  }

  @Override
  public List<Entidad> entidades() {
    return entidades;
  }

  @Override
  public List<Asignacion> asignaciones() {
    return asignaciones;
  }

  @Override
  public boolean esDemo() {
    return true;
  }
}
