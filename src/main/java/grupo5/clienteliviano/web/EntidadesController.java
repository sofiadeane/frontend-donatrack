package grupo5.clienteliviano.web;

import grupo5.clienteliviano.aplicacion.EntidadesService;
import grupo5.clienteliviano.aplicacion.EntidadesService.Filtros;
import grupo5.clienteliviano.session.SessionPort;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/**
 * Entidades del donante. Todo se resuelve en el servidor: elegir una cámara del hormiguero es un
 * enlace con {@code ?entidad=}, y los filtros de la tabla son un formulario GET.
 */
@Controller
public class EntidadesController {

  private final SessionPort sessionPort;
  private final EntidadesService servicio;

  public EntidadesController(SessionPort sessionPort, EntidadesService servicio) {
    this.sessionPort = sessionPort;
    this.servicio = servicio;
  }

  @GetMapping("/donante/entidades")
  public String entidades(
      @RequestParam(required = false) String entidad,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String tipo,
      @RequestParam(required = false) String categoria,
      @RequestParam(required = false) String localidad,
      HttpServletRequest request,
      Model model) {
    var sesion =
        sessionPort
            .actual(request)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    Filtros filtros = new Filtros(texto(q), texto(tipo), texto(categoria), texto(localidad));
    model.addAttribute("pagina", servicio.pagina(sesion, filtros, uuid(entidad)));
    return "donante/entidades";
  }

  private static String texto(String s) {
    return s == null || s.isBlank() ? null : s.strip();
  }

  private static UUID uuid(String s) {
    try {
      return s == null || s.isBlank() ? null : UUID.fromString(s);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
