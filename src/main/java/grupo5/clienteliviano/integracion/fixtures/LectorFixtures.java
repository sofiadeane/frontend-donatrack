package grupo5.clienteliviano.integracion.fixtures;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lee fixtures de demostración desde {@code src/main/resources/fixtures}. Es estricto: un campo que
 * no existe en el DTO hace fallar la lectura, para detectar fixtures desalineados con el backend.
 */
@Component
public class LectorFixtures {

  private final JsonMapper mapper;

  public LectorFixtures(JsonMapper base) {
    this.mapper = base.rebuild().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
  }

  public <T> List<T> lista(String ruta, Class<T> tipo) {
    try (InputStream in = new ClassPathResource("fixtures/" + ruta).getInputStream()) {
      return List.copyOf(
          mapper.readValue(in, mapper.getTypeFactory().constructCollectionType(List.class, tipo)));
    } catch (IOException e) {
      throw new UncheckedIOException("No se pudo leer el fixture " + ruta, e);
    }
  }
}
