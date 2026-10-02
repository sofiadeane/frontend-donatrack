package grupo5.clienteliviano.integracion.donaciones;

import grupo5.clienteliviano.integracion.donaciones.dto.DonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionOutputDTO;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.http.ClienteHttp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.core.ParameterizedTypeReference;

/** Adapter real de donaciones-service. */
public class DonacionesHttpAdapter implements DonacionesPort {

  private static final ParameterizedTypeReference<List<DonacionIndependienteResponseDTO>> LISTA =
      new ParameterizedTypeReference<>() {};

  private final ClienteHttp cliente;

  public DonacionesHttpAdapter(ClienteHttp cliente) {
    this.cliente = cliente;
  }

  @Override
  public List<DonacionIndependienteResponseDTO> donacionesIndependientes(FiltroDonaciones filtro) {
    List<DonacionIndependienteResponseDTO> respuesta =
        cliente.ejecutar(
            rest ->
                rest.get()
                    .uri(
                        uri ->
                            uri.path("/donaciones-independientes")
                                .queryParamIfPresent(
                                    "donanteId", Optional.ofNullable(filtro.donanteId()))
                                .queryParamIfPresent("estado", Optional.ofNullable(filtro.estado()))
                                .queryParamIfPresent(
                                    "subcategoriaId", Optional.ofNullable(filtro.subcategoriaId()))
                                .build())
                    .retrieve()
                    .body(LISTA));
    return respuesta == null ? List.of() : respuesta;
  }

  @Override
  public Optional<DonacionOutputDTO> donacion(UUID id) {
    try {
      return Optional.ofNullable(
          cliente.ejecutar(
              rest ->
                  rest.get()
                      .uri("/api/donaciones/{id}", id)
                      .retrieve()
                      .body(DonacionOutputDTO.class)));
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
