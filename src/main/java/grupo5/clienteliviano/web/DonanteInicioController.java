package grupo5.clienteliviano.web;

import grupo5.clienteliviano.aplicacion.DonanteInicioService;
import grupo5.clienteliviano.session.SessionPort;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Inicio del donante. La guarda de rol garantiza que hay sesión de donante. */
@Controller
public class DonanteInicioController {

  private final SessionPort sessionPort;
  private final DonanteInicioService servicio;

  public DonanteInicioController(SessionPort sessionPort, DonanteInicioService servicio) {
    this.sessionPort = sessionPort;
    this.servicio = servicio;
  }

  @GetMapping("/donante")
  public String inicio(
      @RequestParam(required = false) String aviso, HttpServletRequest request, Model model) {
    model.addAttribute("aviso", aviso);
    sessionPort
        .actual(request)
        .ifPresent(s -> model.addAttribute("ultimas", servicio.ultimasDonaciones(s)));
    return "donante/inicio";
  }
}
