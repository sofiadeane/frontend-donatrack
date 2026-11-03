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

  /** Landing de la variante visual G, "Cartografía" (v6 de la exploración 2026-10-30). */
  @GetMapping("/")
  public String inicio(Model model) {
    cargarLanding(model);
    return "publico/inicio-g";
  }

  /** Landing de la variante visual H, "Hormiguero" (v7): hormigas y granos de arena. */
  @GetMapping("/h")
  public String inicioH(Model model) {
    cargarLanding(model);
    return "publico/inicio-h";
  }

  /** Landing de la variante visual F, "Cristal" (v5). */
  @GetMapping("/f")
  public String inicioF(Model model) {
    cargarLanding(model);
    return "publico/inicio-f";
  }

  /** Landing de la variante visual E, "Calma" (v4). */
  @GetMapping("/e")
  public String inicioE(Model model) {
    cargarLanding(model);
    return "publico/inicio-e";
  }

  /** Landing de la variante visual D, "Trazo" (v3). */
  @GetMapping("/d")
  public String inicioD(Model model) {
    cargarLanding(model);
    return "publico/inicio-d";
  }

  /** Landing de la variante visual C, "Suave" (v2). */
  @GetMapping("/c")
  public String inicioC(Model model) {
    cargarLanding(model);
    return "publico/inicio-c";
  }

  /** Landing de la variante visual B, "Ruta" (v1 de la exploración, neobrutalista). */
  @GetMapping("/b")
  public String inicioB(Model model) {
    cargarLanding(model);
    return "publico/inicio-b";
  }

  /** Landing de la versión anterior, para comparar. */
  @GetMapping("/v1")
  public String inicioAnterior(Model model) {
    cargarLanding(model);
    return "publico/inicio";
  }

  /** La versión anterior con tono más cercano: copy desde quien dona y neutros cálidos. */
  @GetMapping("/v1-cercana")
  public String inicioCercana(Model model) {
    cargarLanding(model);
    return "publico/inicio-cercana";
  }

  private void cargarLanding(Model model) {
    model.addAttribute("destacadas", landing.destacadasDelUltimoMes());
    model.addAttribute("metricas", landing.metricas());
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
