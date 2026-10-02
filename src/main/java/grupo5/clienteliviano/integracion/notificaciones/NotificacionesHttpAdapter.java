package grupo5.clienteliviano.integracion.notificaciones;

import grupo5.clienteliviano.integracion.http.ClienteHttp;
import java.util.List;
import java.util.UUID;
import org.springframework.core.ParameterizedTypeReference;

/** Adapter real de notificaciones-service. */
public class NotificacionesHttpAdapter implements NotificacionesPort {

  private static final ParameterizedTypeReference<List<NotificacionDTO>> LISTA =
      new ParameterizedTypeReference<>() {};

  private final ClienteHttp cliente;

  public NotificacionesHttpAdapter(ClienteHttp cliente) {
    this.cliente = cliente;
  }

  @Override
  public List<NotificacionDTO> dePersona(UUID personaId) {
    List<NotificacionDTO> respuesta =
        cliente.ejecutar(
            rest ->
                rest.get()
                    .uri("/api/notificaciones/persona/{id}", personaId)
                    .retrieve()
                    .body(LISTA));
    return respuesta == null ? List.of() : respuesta;
  }

  @Override
  public boolean esDemo() {
    return false;
  }
}
