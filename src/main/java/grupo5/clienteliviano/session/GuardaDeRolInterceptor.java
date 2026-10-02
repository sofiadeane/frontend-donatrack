package grupo5.clienteliviano.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Guarda de UI: impide entrar al área privada de otro rol. Es una protección de interfaz; la
 * autorización real corresponde al backend (auth-service, Entrega 6).
 */
@Component
public class GuardaDeRolInterceptor implements HandlerInterceptor {

  private final SessionPort sessionPort;

  public GuardaDeRolInterceptor(SessionPort sessionPort) {
    this.sessionPort = sessionPort;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String ruta = request.getRequestURI().substring(request.getContextPath().length());
    Rol requerido = Rol.duenioDe(ruta);
    if (requerido == null) {
      return true;
    }
    Optional<SesionDemo> sesion = sessionPort.actual(request);
    if (sesion.isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/ingresar?motivo=sin-sesion");
      return false;
    }
    if (sesion.get().rol() != requerido) {
      response.sendRedirect(
          request.getContextPath() + sesion.get().rol().inicio() + "?aviso=sin-permiso");
      return false;
    }
    return true;
  }
}
