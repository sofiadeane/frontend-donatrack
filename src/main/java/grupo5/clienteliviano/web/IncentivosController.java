package grupo5.clienteliviano.web;

import grupo5.clienteliviano.aplicacion.IncentivosService;
import grupo5.clienteliviano.session.SessionPort;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

/** Panel de incentivos del donante (H2.4). */
@Controller
public class IncentivosController {

  private final SessionPort sessionPort;
  private final IncentivosService servicio;

  public IncentivosController(SessionPort sessionPort, IncentivosService servicio) {
    this.sessionPort = sessionPort;
    this.servicio = servicio;
  }

  @GetMapping("/donante/incentivos")
  public String incentivos(HttpServletRequest request, Model model) {
    var sesion =
        sessionPort
            .actual(request)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    model.addAttribute("incentivos", servicio.pagina(sesion));
    return "donante/incentivos";
  }
}
