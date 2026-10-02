package grupo5.clienteliviano.web;

import grupo5.clienteliviano.aplicacion.MisDonacionesService;
import grupo5.clienteliviano.aplicacion.MisDonacionesService.CargaDetalle;
import grupo5.clienteliviano.aplicacion.MisDonacionesService.Filtros;
import grupo5.clienteliviano.session.SesionDemo;
import grupo5.clienteliviano.session.SessionPort;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/** Mis donaciones (H2.1) y detalle con historial (H2.2). Filtros por GET: funcionan sin JS. */
@Controller
public class MisDonacionesController {

  private final SessionPort sessionPort;
  private final MisDonacionesService servicio;

  public MisDonacionesController(SessionPort sessionPort, MisDonacionesService servicio) {
    this.sessionPort = sessionPort;
    this.servicio = servicio;
  }

  @GetMapping("/donante/donaciones")
  public String lista(
      @RequestParam(required = false) String estado,
      @RequestParam(required = false) String categoria,
      @RequestParam(required = false) String subcategoria,
      HttpServletRequest request,
      Model model) {
    Filtros filtros = new Filtros(vacioANull(estado), uuid(categoria), uuid(subcategoria));
    model.addAttribute("mis", servicio.listar(sesion(request), filtros));
    return "donante/donaciones";
  }

  @GetMapping("/donante/donaciones/{id}")
  public String detalle(@PathVariable String id, HttpServletRequest request, Model model) {
    UUID uuid = uuid(id);
    if (uuid == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    CargaDetalle carga = servicio.detalle(sesion(request), uuid);
    if (carga.error() == null && carga.detalle().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    model.addAttribute("carga", carga);
    return "donante/donacion";
  }

  private SesionDemo sesion(HttpServletRequest request) {
    // La guarda de rol ya exige una sesión de donante en /donante/**.
    return sessionPort
        .actual(request)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }

  private static String vacioANull(String s) {
    return s == null || s.isBlank() ? null : s;
  }

  private static UUID uuid(String s) {
    try {
      return s == null || s.isBlank() ? null : UUID.fromString(s);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
