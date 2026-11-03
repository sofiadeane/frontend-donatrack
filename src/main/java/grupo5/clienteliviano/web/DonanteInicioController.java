package grupo5.clienteliviano.web;

import grupo5.clienteliviano.aplicacion.PanelDonanteService;
import grupo5.clienteliviano.session.SessionPort;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Inicio del donante (variante H). La guarda de rol garantiza que hay sesión de donante. */
@Controller
public class DonanteInicioController {

  private final SessionPort sessionPort;
  private final PanelDonanteService servicio;

  public DonanteInicioController(SessionPort sessionPort, PanelDonanteService servicio) {
    this.sessionPort = sessionPort;
    this.servicio = servicio;
  }

  @GetMapping("/donante")
  public String inicio(
      @RequestParam(required = false) String aviso, HttpServletRequest request, Model model) {
    model.addAttribute("aviso", aviso);
    sessionPort.actual(request).ifPresent(s -> model.addAttribute("panel", servicio.panel(s)));
    return "donante/inicio-h";
  }

  /** El tablero anterior (layout privado), para comparar. */
  @GetMapping("/donante/anterior")
  public String inicioAnterior(HttpServletRequest request, Model model) {
    sessionPort.actual(request).ifPresent(s -> model.addAttribute("panel", servicio.panel(s)));
    return "donante/inicio";
  }

  /** El mismo tablero con tono más cercano, para comparar con el original. */
  @GetMapping("/donante/cercano")
  public String inicioCercano(HttpServletRequest request, Model model) {
    sessionPort.actual(request).ifPresent(s -> model.addAttribute("panel", servicio.panel(s)));
    return "donante/inicio-cercano";
  }

  /** Todas las notificaciones del donante. */
  @GetMapping("/donante/notificaciones")
  public String notificaciones(HttpServletRequest request, Model model) {
    sessionPort
        .actual(request)
        .ifPresent(s -> model.addAttribute("notificaciones", servicio.notificaciones(s)));
    return "donante/notificaciones";
  }
}
