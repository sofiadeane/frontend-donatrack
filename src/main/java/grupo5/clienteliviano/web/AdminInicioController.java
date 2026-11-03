package grupo5.clienteliviano.web;

import grupo5.clienteliviano.aplicacion.PanelAdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Inicio de la persona administradora: panel operativo (variantes visuales C y B). */
@Controller
public class AdminInicioController {

  private final PanelAdminService servicio;

  public AdminInicioController(PanelAdminService servicio) {
    this.servicio = servicio;
  }

  @GetMapping("/admin")
  public String inicio(@RequestParam(required = false) String aviso, Model model) {
    cargar(aviso, model);
    return "admin/inicio-g";
  }

  /** Panel de la variante F, para comparar. */
  @GetMapping("/admin/f")
  public String inicioF(@RequestParam(required = false) String aviso, Model model) {
    cargar(aviso, model);
    return "admin/inicio-f";
  }

  /** Panel de la variante E, para comparar. */
  @GetMapping("/admin/e")
  public String inicioE(@RequestParam(required = false) String aviso, Model model) {
    cargar(aviso, model);
    return "admin/inicio-e";
  }

  /** Panel de la variante D, para comparar. */
  @GetMapping("/admin/d")
  public String inicioD(@RequestParam(required = false) String aviso, Model model) {
    cargar(aviso, model);
    return "admin/inicio-d";
  }

  /** Panel de la variante C, para comparar. */
  @GetMapping("/admin/c")
  public String inicioC(@RequestParam(required = false) String aviso, Model model) {
    cargar(aviso, model);
    return "admin/inicio-c";
  }

  /** Versión anterior del panel (variante B), para comparar. */
  @GetMapping("/admin/b")
  public String inicioB(@RequestParam(required = false) String aviso, Model model) {
    cargar(aviso, model);
    return "admin/inicio";
  }

  private void cargar(String aviso, Model model) {
    model.addAttribute("aviso", aviso);
    model.addAttribute("panel", servicio.panel());
    model.addAttribute("fecha", servicio.fechaLarga());
  }
}
