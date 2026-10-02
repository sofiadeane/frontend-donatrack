package grupo5.clienteliviano.integracion.http;

import grupo5.clienteliviano.integracion.BackendProperties;
import grupo5.clienteliviano.integracion.BackendProperties.ServicioConfig;
import grupo5.clienteliviano.integracion.Servicio;
import java.net.http.HttpClient;
import java.util.UUID;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Crea el {@link ClienteHttp} de cada servicio con su URL base y tiempo de espera. */
@Component
public class FabricaClientesHttp {

  /** Encabezado de trazabilidad que el backend propaga (common-lib, TraceResponseHeaderFilter). */
  public static final String TRACE_HEADER = "X-Trace-Id";

  private final RestClient.Builder builder;
  private final BackendProperties properties;

  public FabricaClientesHttp(RestClient.Builder builder, BackendProperties properties) {
    this.builder = builder;
    this.properties = properties;
  }

  public ClienteHttp para(Servicio servicio) {
    ServicioConfig config = properties.de(servicio);
    if (config.url() == null) {
      throw new IllegalStateException(
          "Falta donatrack.backend.servicios." + servicio.name().toLowerCase() + ".url");
    }
    HttpClient http = HttpClient.newBuilder().connectTimeout(config.timeout()).build();
    JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(http);
    fabrica.setReadTimeout(config.timeout());
    RestClient cliente =
        builder
            .clone()
            .baseUrl(config.url().toString())
            .requestFactory(fabrica)
            .requestInterceptor(
                (request, body, execution) -> {
                  if (request.getHeaders().getFirst(TRACE_HEADER) == null) {
                    request
                        .getHeaders()
                        .add(TRACE_HEADER, UUID.randomUUID().toString().replace("-", ""));
                  }
                  return execution.execute(request, body);
                })
            .build();
    return new ClienteHttp(servicio, cliente);
  }
}
