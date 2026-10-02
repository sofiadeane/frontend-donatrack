package grupo5.clienteliviano.web;

import grupo5.clienteliviano.aplicacion.LandingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Páginas públicas. */
@Controller
public class PublicoController {

  private final LandingService landing;

  public PublicoController(LandingService landing) {
    this.landing = landing;
  }

  @GetMapping("/")
  public String inicio(Model model) {
    model.addAttribute("destacadas", landing.destacadasDelUltimoMes());
    model.addAttribute("metricas", landing.metricas());
    return "publico/inicio";
  }

  /** Mapa interactivo (R2): sin coordenadas en el backend (brecha G4), queda como TBD visible. */
  @GetMapping("/mapa-de-impacto")
  public String mapa(Model model) {
    model.addAttribute("titulo", "Mapa de impacto");
    model.addAttribute(
        "texto",
        "El mapa interactivo de donaciones entregadas todavía no está disponible: el backend aún no"
            + " guarda la ubicación de las entregas. Mientras tanto, podés ver las donaciones"
            + " entregadas en su galería.");
    model.addAttribute("enlace", "/donaciones-entregadas");
    model.addAttribute("enlaceTexto", "Ver donaciones entregadas");
    return "publico/proximamente";
  }

  /** Información legal y privacidad (F04): contenido estático. */
  @GetMapping("/privacidad")
  public String privacidad() {
    return "publico/privacidad";
  }

  /** Galería pública de donaciones entregadas (H1.5), filtrable por categoría. */
  @GetMapping("/donaciones-entregadas")
  public String donacionesEntregadas(
      @RequestParam(required = false) String categoria, Model model) {
    model.addAttribute("galeria", landing.galeria(categoria));
    return "publico/donaciones-entregadas";
  }
}
