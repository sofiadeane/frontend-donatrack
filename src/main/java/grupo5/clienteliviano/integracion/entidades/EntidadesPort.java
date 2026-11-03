package grupo5.clienteliviano.integracion.entidades;

import java.util.List;
import java.util.UUID;

/**
 * Entidades beneficiarias y a cuál va cada donación. El backend no permite filtrar donaciones por
 * entidad ni expone el destino al donante (brecha G3 de la especificación), así que hoy solo existe
 * el adapter de demostración.
 */
public interface EntidadesPort {

  /** Una entidad beneficiaria con lo que necesita hoy. */
  record Entidad(
      UUID id,
      String nombre,
      String tipo,
      String localidad,
      List<String> categorias,
      int necesidadesAbiertas,
      boolean necesidadUrgente) {}

  /** La donación independiente {@code donacionId} fue asignada a la entidad {@code entidadId}. */
  record Asignacion(UUID donacionId, UUID entidadId) {}

  List<Entidad> entidades();

  List<Asignacion> asignaciones();

  boolean esDemo();
}
