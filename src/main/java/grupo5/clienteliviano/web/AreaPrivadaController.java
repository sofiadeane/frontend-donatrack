package grupo5.clienteliviano.web;

import grupo5.clienteliviano.navegacion.CatalogoNavegacion;
import grupo5.clienteliviano.navegacion.ItemNavegacion;
import grupo5.clienteliviano.session.Rol;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/**
 * Inicio de cada área privada y secciones aún no construidas. Las secciones se reemplazan por
 * controladores propios a medida que se implementan las épicas E2–E4.
 */
@Controller
public class AreaPrivadaController {

  private final CatalogoNavegacion catalogo;

  public AreaPrivadaController(CatalogoNavegacion catalogo) {
    this.catalogo = catalogo;
  }

  @GetMapping({"/donante", "/entidad", "/admin"})
  public String inicio(@RequestParam(required = false) String aviso, Model model) {
    model.addAttribute("aviso", aviso);
    return "privado/inicio";
  }

  @GetMapping({"/donante/buscar", "/entidad/buscar", "/admin/buscar"})
  public String buscar(HttpServletRequest request, Model model) {
    String ruta = request.getRequestURI().substring(request.getContextPath().length());
    model.addAttribute("seccion", new ItemNavegacion("Búsqueda", ruta, "buscar", false));
    return "privado/en-construccion";
  }

  @GetMapping({"/donante/{seccion}", "/entidad/{seccion}", "/admin/{seccion}"})
  public String seccion(@PathVariable String seccion, HttpServletRequest request, Model model) {
    Rol rol = Rol.duenioDe(request.getRequestURI().substring(request.getContextPath().length()));
    ItemNavegacion item =
        catalogo.de(rol).stream()
            .filter(i -> i.href().equals(rol.inicio() + "/" + seccion))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    model.addAttribute("seccion", item);
    return "privado/en-construccion";
  }
}
