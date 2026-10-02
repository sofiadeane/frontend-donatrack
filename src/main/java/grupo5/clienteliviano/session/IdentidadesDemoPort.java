package grupo5.clienteliviano.session;

import java.util.List;
import java.util.Optional;

/**
 * Fuente de identidades para el ingreso de demostración. La implementación con fixtures se
 * reemplazará por una que lea personas, donantes y entidades reales de donaciones-service.
 */
public interface IdentidadesDemoPort {

  List<IdentidadDemo> listar(Rol rol);

  Optional<IdentidadDemo> buscar(String id);

  /** {@code true} si las identidades son datos de demostración (se muestra la etiqueta). */
  boolean esDemo();
}
