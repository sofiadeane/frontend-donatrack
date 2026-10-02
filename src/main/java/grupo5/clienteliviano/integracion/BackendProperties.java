package grupo5.clienteliviano.integracion;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración por servicio ({@code donatrack.backend.<servicio>.*}). El modo elige entre el
 * adapter real (HTTP) y el de demostración (fixtures) sin tocar el resto del código (D2).
 */
@ConfigurationProperties(prefix = "donatrack.backend")
public record BackendProperties(Map<String, ServicioConfig> servicios) {

  /** Modo de un adapter. */
  public enum Modo {
    HTTP,
    FIXTURE
  }

  /** Configuración de un servicio. */
  public record ServicioConfig(Modo modo, URI url, Duration timeout) {
    public ServicioConfig {
      modo = modo == null ? Modo.FIXTURE : modo;
      timeout = timeout == null ? Duration.ofSeconds(3) : timeout;
    }
  }

  public BackendProperties {
    servicios = servicios == null ? Map.of() : Map.copyOf(servicios);
  }

  public ServicioConfig de(Servicio servicio) {
    return servicios.getOrDefault(
        servicio.name().toLowerCase(), new ServicioConfig(Modo.FIXTURE, null, null));
  }
}
