package grupo5.clienteliviano.integracion.incentivos;

import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.http.ClienteHttp;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.CambioCategoriaDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.DonantePerfilDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.MetricasDonanteDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.MisionDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.core.ParameterizedTypeReference;

/** Adapter real de incentivos-service. Un 404 (donante sin incentivos, ERR-EST-702) es vacío. */
public class IncentivosHttpAdapter implements IncentivosPort {

  private static final ParameterizedTypeReference<List<MisionDTO>> MISIONES =
      new ParameterizedTypeReference<>() {};
  private static final ParameterizedTypeReference<List<CambioCategoriaDTO>> ASCENSOS =
      new ParameterizedTypeReference<>() {};

  private final ClienteHttp cliente;

  public IncentivosHttpAdapter(ClienteHttp cliente) {
    this.cliente = cliente;
  }

  @Override
  public Optional<DonantePerfilDTO> perfil(UUID donanteId) {
    return opcional(
        () ->
            cliente.ejecutar(
                rest ->
                    rest.get()
                        .uri("/api/incentivos/donantes/{id}", donanteId)
                        .retrieve()
                        .body(DonantePerfilDTO.class)));
  }

  @Override
  public Optional<MetricasDonanteDTO> metricas(UUID donanteId) {
    return opcional(
        () ->
            cliente.ejecutar(
                rest ->
                    rest.get()
                        .uri("/api/incentivos/donantes/{id}/metricas", donanteId)
                        .retrieve()
                        .body(MetricasDonanteDTO.class)));
  }

  @Override
  public List<MisionDTO> misiones(UUID donanteId) {
    return lista("/api/incentivos/donantes/{id}/misiones", donanteId, MISIONES);
  }

  @Override
  public List<CambioCategoriaDTO> ascensos(UUID donanteId) {
    return lista("/api/incentivos/donantes/{id}/ascensos", donanteId, ASCENSOS);
  }

  private <T> List<T> lista(String uri, UUID id, ParameterizedTypeReference<List<T>> tipo) {
    List<T> respuesta =
        opcional(() -> cliente.ejecutar(rest -> rest.get().uri(uri, id).retrieve().body(tipo)))
            .orElse(null);
    return respuesta == null ? List.of() : respuesta;
  }

  private static <T> Optional<T> opcional(Supplier<T> llamada) {
    try {
      return Optional.ofNullable(llamada.get());
    } catch (BackendException e) {
      if (e.tipo() == BackendException.Tipo.NO_ENCONTRADO) {
        return Optional.empty();
      }
      throw e;
    }
  }

  @Override
  public boolean esDemo() {
    return false;
  }
}
