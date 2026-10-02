package grupo5.clienteliviano.integracion.incentivos;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/** Copias de los DTOs de incentivos-service que usa el cliente. */
public final class IncentivosDTOs {

  private IncentivosDTOs() {}

  /** Copia de {@code DonantePerfilDTO}. Categoría: COLABORADOR, SOSTENEDOR o TRANSFORMADOR. */
  public record DonantePerfilDTO(
      UUID donanteId,
      UUID idPersona,
      String nombre,
      String categoria,
      LocalDate fechaRegistro,
      int misionesCompletadas,
      int insigniasGanadas) {}

  /** Copia de {@code MetricasDonanteDTO}; {@code donacionesPorPeriodo} usa claves YYYY-MM. */
  public record MetricasDonanteDTO(
      UUID donanteId,
      String categoria,
      Integer totalDonacionesHistoricas,
      Integer totalOrganizacionesAyudadas,
      Integer totalDonacionesExitosas,
      LocalDate ultimaDonacion,
      int misionesCompletadasTotal,
      MisionActivaDTO misionActiva,
      Map<String, Long> donacionesPorPeriodo,
      long donacionesMesActual,
      long donacionesMesAnterior,
      Integer posicionEnRanking) {}

  /** Copia de {@code MetricasDonanteDTO.MisionActivaDTO}. */
  public record MisionActivaDTO(
      String nombre,
      String descripcion,
      int progresoActual,
      int objetivo,
      int porcentaje,
      int distanciaAlObjetivo) {}

  /**
   * Copia de {@code MisionDTO}: {@code insignia} es la ganada (si está completada) o una vista
   * previa.
   */
  public record MisionDTO(
      String nombre,
      String descripcion,
      String categoria,
      int progresoActual,
      int objetivo,
      int porcentaje,
      int distanciaAlObjetivo,
      boolean completada,
      LocalDate fechaCompletada,
      InsigniaDTO insignia) {}

  /** Copia de {@code InsigniaDTO}; {@code fechaObtenida} es nula en la vista previa. */
  public record InsigniaDTO(
      String nombre,
      String descripcion,
      String imagenUrl,
      boolean visible,
      LocalDate fechaObtenida) {}

  /** Copia de {@code CambioCategoriaDTO}. */
  public record CambioCategoriaDTO(
      String categoriaAnterior, String categoriaNueva, LocalDate fecha) {}
}
