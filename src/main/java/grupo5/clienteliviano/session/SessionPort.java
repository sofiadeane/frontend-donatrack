package grupo5.clienteliviano.session;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Puerto de sesión. Hoy lo implementa una sesión de demostración; en la Entrega 6 se reemplaza por
 * la integración con auth-service sin cambiar controladores ni vistas.
 */
public interface SessionPort {

  Optional<SesionDemo> actual(HttpServletRequest request);

  void iniciar(HttpServletRequest request, SesionDemo sesion);

  void cerrar(HttpServletRequest request);
}
