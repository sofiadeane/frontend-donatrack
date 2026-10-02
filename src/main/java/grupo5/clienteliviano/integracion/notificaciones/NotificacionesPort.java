package grupo5.clienteliviano.integracion.notificaciones;

import java.util.List;
import java.util.UUID;

/** Notificaciones de una persona (indexadas por {@code personaId}, no por donante o entidad). */
public interface NotificacionesPort {

  /** {@code GET /api/notificaciones/persona/{personaId}}. */
  List<NotificacionDTO> dePersona(UUID personaId);

  boolean esDemo();
}
