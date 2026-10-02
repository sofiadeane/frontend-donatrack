package grupo5.clienteliviano.integracion.incentivos;

import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.CambioCategoriaDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.DonantePerfilDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.MetricasDonanteDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.MisionDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Lectura de incentivos de un donante (el id es el {@code donanteId} de donaciones-service). */
public interface IncentivosPort {

  /**
   * {@code GET /api/incentivos/donantes/{donanteId}}; vacío si el donante no está en incentivos.
   */
  Optional<DonantePerfilDTO> perfil(UUID donanteId);

  /** {@code GET /api/incentivos/donantes/{donanteId}/metricas}; vacío si no existe. */
  Optional<MetricasDonanteDTO> metricas(UUID donanteId);

  /** {@code GET /api/incentivos/donantes/{donanteId}/misiones}, ordenadas por número. */
  List<MisionDTO> misiones(UUID donanteId);

  /** {@code GET /api/incentivos/donantes/{donanteId}/ascensos}. */
  List<CambioCategoriaDTO> ascensos(UUID donanteId);

  boolean esDemo();
}
