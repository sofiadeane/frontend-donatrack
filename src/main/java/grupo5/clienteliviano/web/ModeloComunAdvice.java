package grupo5.clienteliviano.web;

import grupo5.clienteliviano.navegacion.CatalogoNavegacion;
import grupo5.clienteliviano.navegacion.ItemNavegacion;
import grupo5.clienteliviano.session.SesionDemo;
import grupo5.clienteliviano.session.SessionPort;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Datos que necesitan todos los layouts: ruta actual, sesión y navegación del rol. */
@ControllerAdvice
public class ModeloComunAdvice {

  /** Ítem de navegación ya resuelto para la vista. */
  public record NavVista(
      String etiqueta,
      String etiquetaMovil,
      String href,
      String icono,
      boolean activo,
      boolean enBarraMovil) {}

  private final SessionPort sessionPort;
  private final CatalogoNavegacion catalogo;

  public ModeloComunAdvice(SessionPort sessionPort, CatalogoNavegacion catalogo) {
    this.sessionPort = sessionPort;
    this.catalogo = catalogo;
  }

  @ModelAttribute
  public void agregar(HttpServletRequest request, Model model) {
    String ruta = request.getRequestURI().substring(request.getContextPath().length());
    model.addAttribute("rutaActual", ruta);
    SesionDemo sesion = sessionPort.actual(request).orElse(null);
    model.addAttribute("sesion", sesion);
    if (sesion != null) {
      String inicio = sesion.rol().inicio();
      List<NavVista> nav =
          catalogo.de(sesion.rol()).stream().map(i -> vista(i, ruta, inicio)).toList();
      model.addAttribute("nav", nav);
      model.addAttribute("navMovil", nav.stream().filter(NavVista::enBarraMovil).toList());
    }
  }

  private static NavVista vista(ItemNavegacion item, String ruta, String inicio) {
    return new NavVista(
        item.etiqueta(),
        item.etiquetaMovil(),
        item.href(),
        item.icono(),
        item.activoEn(ruta, inicio),
        item.enBarraMovil());
  }
}
