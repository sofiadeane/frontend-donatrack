package grupo5.clienteliviano.integracion.donaciones;

import grupo5.clienteliviano.integracion.donaciones.dto.CategoriaOutputDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionOutputDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto hacia donaciones-service. Cada operación corresponde a un endpoint verificado en el código
 * del backend (no en el OpenAPI, que tiene diferencias de campos).
 */
public interface DonacionesPort {

  /** Filtros de {@code GET /donaciones-independientes}; cualquiera puede ser nulo. */
  record FiltroDonaciones(UUID donanteId, String estado, UUID subcategoriaId) {
    public static FiltroDonaciones delDonante(UUID donanteId) {
      return new FiltroDonaciones(donanteId, null, null);
    }
  }

  /** {@code GET /donaciones-independientes?estado&subcategoriaId&donanteId}. */
  List<DonacionIndependienteResponseDTO> donacionesIndependientes(FiltroDonaciones filtro);

  /**
   * {@code GET /api/donaciones/{id}}: la donación original (incluye el donante). Vacío si no
   * existe.
   */
  Optional<DonacionOutputDTO> donacion(UUID id);

  /** {@code GET /api/categorias}: categorías con sus subcategorías (para filtros). */
  List<CategoriaOutputDTO> categorias();

  /** {@code true} si los datos son de demostración y deben mostrarse con su etiqueta. */
  boolean esDemo();
}
