package grupo5.clienteliviano.web;

import grupo5.clienteliviano.session.IdentidadDemo;
import grupo5.clienteliviano.session.IdentidadesDemoPort;
import grupo5.clienteliviano.session.Rol;
import grupo5.clienteliviano.session.SessionPort;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Ingreso de demostración: se elige un rol y una identidad existente (spec §2.4). */
@Controller
public class IngresoController {

  /** Grupo de identidades de un rol para el formulario. */
  public record GrupoRol(Rol rol, List<IdentidadDemo> identidades) {}

  private final SessionPort sessionPort;
  private final IdentidadesDemoPort identidades;

  public IngresoController(SessionPort sessionPort, IdentidadesDemoPort identidades) {
    this.sessionPort = sessionPort;
    this.identidades = identidades;
  }

  @GetMapping("/ingresar")
  public String formulario(@RequestParam(required = false) String motivo, Model model) {
    prepararFormulario(model);
    model.addAttribute("motivo", motivo);
    return "publico/ingresar";
  }

  @PostMapping("/ingresar")
  public String ingresar(
      @RequestParam(required = false) String identidad, HttpServletRequest request, Model model) {
    Optional<IdentidadDemo> elegida = Optional.ofNullable(identidad).flatMap(identidades::buscar);
    if (elegida.isEmpty()) {
      prepararFormulario(model);
      model.addAttribute("error", "Elegí con qué perfil querés ingresar.");
      return "publico/ingresar";
    }
    sessionPort.iniciar(request, elegida.get().aSesion());
    return "redirect:" + elegida.get().rol().inicio();
  }

  @PostMapping("/salir")
  public String salir(HttpServletRequest request) {
    sessionPort.cerrar(request);
    return "redirect:/";
  }

  private void prepararFormulario(Model model) {
    model.addAttribute(
        "grupos",
        Arrays.stream(Rol.values()).map(r -> new GrupoRol(r, identidades.listar(r))).toList());
    model.addAttribute("identidadesDemo", identidades.esDemo());
  }
}
