package grupo5.clienteliviano.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Sesión de demostración guardada en la {@link HttpSession} del servidor. */
@Component
public class HttpSessionDemoAdapter implements SessionPort {

  static final String ATRIBUTO = "donatrack.sesionDemo";

  @Override
  public Optional<SesionDemo> actual(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) {
      return Optional.empty();
    }
    return Optional.ofNullable((SesionDemo) session.getAttribute(ATRIBUTO));
  }

  @Override
  public void iniciar(HttpServletRequest request, SesionDemo sesion) {
    HttpSession anterior = request.getSession(false);
    if (anterior != null) {
      anterior.invalidate();
    }
    request.getSession(true).setAttribute(ATRIBUTO, sesion);
  }

  @Override
  public void cerrar(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
  }
}
