package grupo5.clienteliviano.integracion.donaciones;

import grupo5.clienteliviano.integracion.donaciones.dto.CategoriaOutputDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionOutputDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.ItemDonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.fixtures.LectorFixtures;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter de demostración. Aplica los mismos filtros que el backend ({@code
 * DonacionesIndependientesController}) sobre datos ficticios.
 */
public class DonacionesFixtureAdapter implements DonacionesPort {

  static final String ARCHIVO = "donaciones/donaciones-independientes.json";
  static final String ARCHIVO_ORIGINALES = "donaciones/donaciones.json";
  static final String ARCHIVO_CATEGORIAS = "donaciones/categorias.json";

  /**
   * Registro del fixture. La respuesta del backend no incluye el donante, así que el fixture lo
   * guarda aparte para poder filtrar como lo hace el servidor.
   */
  public record RegistroFixture(UUID donanteId, DonacionIndependienteResponseDTO donacion) {}

  private final List<RegistroFixture> registros;
  private final List<DonacionOutputDTO> originales;
  private final List<CategoriaOutputDTO> categorias;

  public DonacionesFixtureAdapter(LectorFixtures lector) {
    this.registros = lector.lista(ARCHIVO, RegistroFixture.class);
    this.originales = lector.lista(ARCHIVO_ORIGINALES, DonacionOutputDTO.class);
    this.categorias = lector.lista(ARCHIVO_CATEGORIAS, CategoriaOutputDTO.class);
  }

  @Override
  public Optional<DonacionOutputDTO> donacion(UUID id) {
    return originales.stream().filter(d -> d.id().equals(id)).findFirst();
  }

  @Override
  public List<DonacionIndependienteResponseDTO> donacionesIndependientes(FiltroDonaciones filtro) {
    return registros.stream()
        .filter(r -> filtro.donanteId() == null || filtro.donanteId().equals(r.donanteId()))
        .map(RegistroFixture::donacion)
        .filter(d -> filtro.estado() == null || filtro.estado().equals(d.estadoActual()))
        .filter(
            d -> filtro.subcategoriaId() == null || tieneSubcategoria(d, filtro.subcategoriaId()))
        .toList();
  }

  private static boolean tieneSubcategoria(
      DonacionIndependienteResponseDTO d, UUID subcategoriaId) {
    return d.items().stream()
        .map(ItemDonacionIndependienteResponseDTO::bien)
        .filter(Objects::nonNull)
        .anyMatch(b -> b.subcategoria() != null && subcategoriaId.equals(b.subcategoria().id()));
  }

  @Override
  public List<CategoriaOutputDTO> categorias() {
    return categorias;
  }

  @Override
  public boolean esDemo() {
    return true;
  }
}
