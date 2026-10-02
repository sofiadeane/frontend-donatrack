package grupo5.clienteliviano.web;

import grupo5.clienteliviano.aplicacion.LandingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

  @GetMapping({"/privacidad", "/registro", "/donaciones-entregadas"})
  public String enConstruccion(jakarta.servlet.http.HttpServletRequest request, Model model) {
    String ruta = request.getRequestURI();
    model.addAttribute(
        "titulo",
        switch (ruta) {
          case "/privacidad" -> "Información legal y privacidad";
          case "/registro" -> "Crear cuenta";
          default -> "Donaciones entregadas";
        });
    model.addAttribute("texto", "Esta página se habilita en la próxima iteración del prototipo.");
    return "publico/proximamente";
  }
}
