package grupo5.clienteliviano.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/** Identidades de demostración leídas de {@code fixtures/sesion/identidades.json}. */
@Component
public class IdentidadesDemoFixtureAdapter implements IdentidadesDemoPort {

  private static final String ARCHIVO = "fixtures/sesion/identidades.json";

  private final List<IdentidadDemo> identidades;

  public IdentidadesDemoFixtureAdapter(JsonMapper jsonMapper) {
    try (InputStream in = new ClassPathResource(ARCHIVO).getInputStream()) {
      this.identidades =
          List.copyOf(jsonMapper.readValue(in, new TypeReference<List<IdentidadDemo>>() {}));
    } catch (IOException e) {
      throw new UncheckedIOException("No se pudo leer " + ARCHIVO, e);
    }
  }

  @Override
  public List<IdentidadDemo> listar(Rol rol) {
    return identidades.stream().filter(i -> i.rol() == rol).toList();
  }

  @Override
  public Optional<IdentidadDemo> buscar(String id) {
    return identidades.stream().filter(i -> i.id().equals(id)).findFirst();
  }

  @Override
  public boolean esDemo() {
    return true;
  }
}
