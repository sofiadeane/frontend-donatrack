package grupo5.clienteliviano.aplicacion;

import grupo5.clienteliviano.integracion.error.BackendException;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

/** Carga una sección aislando sus fallas: devuelve datos, vacío o error, nunca lanza. */
@Component
public class CargadorSecciones {

  private final TraductorErrores traductor;

  public CargadorSecciones(TraductorErrores traductor) {
    this.traductor = traductor;
  }

  public <T> Seccion<T> cargar(boolean demo, Supplier<List<T>> carga) {
    try {
      return Seccion.de(carga.get(), demo);
    } catch (BackendException e) {
      return Seccion.fallida(traductor.traducir(e));
    }
  }
}
