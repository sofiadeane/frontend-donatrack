package grupo5.clienteliviano.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Páginas públicas. El inicio completo (landing del Figma) se construye en la épica E1. */
@Controller
public class PublicoController {

  @GetMapping("/")
  public String inicio() {
    return "publico/inicio";
  }
}
