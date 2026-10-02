package grupo5.clienteliviano.integracion.donaciones.dto;

import java.util.List;
import java.util.UUID;

/**
 * Copia parcial de {@code CategoriaOutputDTO} ({@code GET /api/categorias}): solo lo que usan los
 * filtros.
 */
public record CategoriaOutputDTO(
    UUID id, String nombre, String unidad, List<SubcategoriaOutputDTO> subcategorias) {

  /** Copia parcial de {@code SubcategoriaOutputDTO}. */
  public record SubcategoriaOutputDTO(UUID id, String nombre) {}
}
