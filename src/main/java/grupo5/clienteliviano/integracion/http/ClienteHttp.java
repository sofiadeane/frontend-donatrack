package grupo5.clienteliviano.integracion.http;

import grupo5.clienteliviano.integracion.Servicio;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.error.BackendException.Tipo;
import grupo5.clienteliviano.integracion.error.ErrorBackend;
import java.util.function.Function;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Envoltura de {@link RestClient} para un servicio: toda falla sale como {@link BackendException}
 * con el cuerpo de error estándar del backend cuando existe.
 */
public class ClienteHttp {

  private final Servicio servicio;
  private final RestClient restClient;

  public ClienteHttp(Servicio servicio, RestClient restClient) {
    this.servicio = servicio;
    this.restClient = restClient;
  }

  public <T> T ejecutar(Function<RestClient, T> llamada) {
    try {
      return llamada.apply(restClient);
    } catch (RestClientResponseException e) {
      throw new BackendException(
          servicio, tipoDe(e.getStatusCode().value()), e.getStatusCode().value(), cuerpo(e), e);
    } catch (ResourceAccessException e) {
      throw new BackendException(servicio, Tipo.NO_DISPONIBLE, 0, null, e);
    }
  }

  private static Tipo tipoDe(int status) {
    return switch (status) {
      case 400 -> Tipo.VALIDACION;
      case 404 -> Tipo.NO_ENCONTRADO;
      case 409 -> Tipo.CONFLICTO;
      case 502, 503, 504 -> Tipo.NO_DISPONIBLE;
      default -> Tipo.ERROR;
    };
  }

  private static ErrorBackend cuerpo(RestClientResponseException e) {
    if (e.getResponseBodyAsByteArray().length == 0) {
      return null;
    }
    try {
      return e.getResponseBodyAs(ErrorBackend.class);
    } catch (RuntimeException noEsElFormatoEstandar) {
      return null;
    }
  }
}
