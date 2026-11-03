package grupo5.clienteliviano.web;

import grupo5.clienteliviano.navegacion.CatalogoNavegacion;
import grupo5.clienteliviano.navegacion.ItemNavegacion;
import grupo5.clienteliviano.session.SesionDemo;
import grupo5.clienteliviano.session.SessionPort;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
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
  private final boolean exportEstatico;

  public ModeloComunAdvice(
      SessionPort sessionPort,
      CatalogoNavegacion catalogo,
      @org.springframework.beans.factory.annotation.Value("${donatrack.export-estatico:false}")
          boolean exportEstatico) {
    this.sessionPort = sessionPort;
    this.catalogo = catalogo;
    this.exportEstatico = exportEstatico;
  }

  /**
   * Palabras que cambian entre los temas de la variante H: hormigas y arena, o abejas y polen. El
   * resto del texto es el mismo; la diferencia visual la ponen {@code ilustraciones-i} e {@code
   * i-theme.css}.
   */
  static final Map<String, Map<String, String>> VOCABULARIO =
      Map.of(
          "hormigas",
          Map.of(
              "casa", "hormiguero",
              "tuCasa", "Tu hormiguero",
              "unaMismaCasa", "un mismo hormiguero",
              "material", "arena",
              "bicho", "hormiga",
              "lugar", "cámara",
              "lugares", "cámaras"),
          "abejas",
          Map.of(
              "casa", "colmena",
              "tuCasa", "Tu colmena",
              "unaMismaCasa", "una misma colmena",
              "material", "polen",
              "bicho", "abeja",
              "lugar", "celda",
              "lugares", "celdas"));

  /**
   * Tema elegido: {@code ?tema=abejas} o {@code ?tema=hormigas} lo guarda en la sesión; por
   * defecto, hormigas.
   */
  static String tema(HttpServletRequest request) {
    String pedido = request.getParameter("tema");
    if (pedido == null && request.getRequestURI().endsWith("/i")) {
      pedido = "abejas";
    } else if (pedido == null && request.getRequestURI().endsWith("/h")) {
      pedido = "hormigas";
    }
    if (pedido != null && VOCABULARIO.containsKey(pedido)) {
      request.getSession().setAttribute("tema", pedido);
      return pedido;
    }
    var sesion = request.getSession(false);
    Object guardado = sesion == null ? null : sesion.getAttribute("tema");
    return guardado instanceof String t && VOCABULARIO.containsKey(t) ? t : "hormigas";
  }

  @ModelAttribute
  public void agregar(HttpServletRequest request, Model model) {
    String ruta = request.getRequestURI().substring(request.getContextPath().length());
    String tema = tema(request);
    model.addAttribute("tema", tema);
    model.addAttribute("abejas", "abejas".equals(tema));
    model.addAttribute("voc", VOCABULARIO.get(tema));
    model.addAttribute("rutaActual", ruta);
    // En el export para GitHub Pages no hay servidor: las acciones POST se reemplazan por enlaces.
    model.addAttribute("exportEstatico", exportEstatico);
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
