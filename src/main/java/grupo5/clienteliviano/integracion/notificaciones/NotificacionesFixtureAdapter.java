package grupo5.clienteliviano.integracion.notificaciones;

import grupo5.clienteliviano.integracion.fixtures.LectorFixtures;
import java.util.List;
import java.util.UUID;

/** Adapter de demostración. El DTO del backend no trae la persona: el fixture la guarda aparte. */
public class NotificacionesFixtureAdapter implements NotificacionesPort {

  static final String ARCHIVO = "notificaciones/notificaciones.json";

  public record RegistroFixture(UUID personaId, NotificacionDTO notificacion) {}

  private final List<RegistroFixture> registros;

  public NotificacionesFixtureAdapter(LectorFixtures lector) {
    this.registros = lector.lista(ARCHIVO, RegistroFixture.class);
  }

  @Override
  public List<NotificacionDTO> dePersona(UUID personaId) {
    return registros.stream()
        .filter(r -> r.personaId().equals(personaId))
        .map(RegistroFixture::notificacion)
        .toList();
  }

  @Override
  public boolean esDemo() {
    return true;
  }
}
