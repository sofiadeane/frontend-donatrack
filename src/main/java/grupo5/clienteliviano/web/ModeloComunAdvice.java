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
   * Palabras que cambian entre los temas de la variante H: hormigas y arena, abejas y miel, o
   * plantas y semillas (el jardín, con las abejas de apoyo). El resto del texto es el mismo; la
   * diferencia visual la ponen {@code ilustraciones-i}/{@code -j} e {@code i-theme.css}/{@code
   * j-theme.css}.
   */
  static final Map<String, Map<String, String>> VOCABULARIO =
      Map.of(
          "hormigas",
          Map.ofEntries(
              Map.entry("casa", "hormiguero"),
              Map.entry("tuCasa", "Tu hormiguero"),
              Map.entry("unaMismaCasa", "un mismo hormiguero"),
              Map.entry("bicho", "hormiga"),
              Map.entry("lugar", "cámara"),
              Map.entry("lugares", "cámaras"),
              Map.entry("unidad", "granito"),
              Map.entry("unidades", "granitos"),
              Map.entry("aportados", "aportados"),
              Map.entry("aportada", "aportado"),
              Map.entry("tuAporte", "Tu granito de arena"),
              Map.entry("suma", "Sumá tu"),
              Map.entry("aPoco", "Grano a grano"),
              Map.entry("tuProximo", "tu próximo granito"),
              Map.entry("cadaUna", "cada grano es una donación")),
          "abejas",
          Map.ofEntries(
              Map.entry("casa", "colmena"),
              Map.entry("tuCasa", "Tu colmena"),
              Map.entry("unaMismaCasa", "una misma colmena"),
              Map.entry("bicho", "abeja"),
              Map.entry("lugar", "celda"),
              Map.entry("lugares", "celdas"),
              Map.entry("unidad", "gota"),
              Map.entry("unidades", "gotas"),
              Map.entry("aportados", "aportadas"),
              Map.entry("aportada", "aportada"),
              Map.entry("tuAporte", "Tu gota de miel"),
              Map.entry("suma", "Sumá tu"),
              Map.entry("aPoco", "Gota a gota"),
              Map.entry("tuProximo", "tu próxima gota"),
              Map.entry("cadaUna", "cada celda que se llena es una donación entregada")),
          "plantas",
          Map.ofEntries(
              Map.entry("casa", "huerta"),
              Map.entry("tuCasa", "Tu huerta"),
              Map.entry("unaMismaCasa", "una misma huerta"),
              Map.entry("bicho", "abeja"),
              Map.entry("lugar", "cantero"),
              Map.entry("lugares", "canteros"),
              Map.entry("unidad", "semilla"),
              Map.entry("unidades", "semillas"),
              Map.entry("aportados", "sembradas"),
              Map.entry("aportada", "sembrada"),
              Map.entry("tuAporte", "Tu semilla"),
              Map.entry("suma", "Sembrá tu"),
              Map.entry("aPoco", "Semilla a semilla"),
              Map.entry("tuProximo", "tu próxima semilla"),
              Map.entry("cadaUna", "cada fruto es una donación entregada")));

  /**
   * Tema elegido: {@code ?tema=abejas}, {@code ?tema=plantas} o {@code ?tema=hormigas} lo guarda en
   * la sesión; las rutas {@code /h}, {@code /i} y {@code /j} lo eligen solas. Por defecto,
   * hormigas.
   */
  static String tema(HttpServletRequest request) {
    String pedido = request.getParameter("tema");
    if (pedido == null && request.getRequestURI().endsWith("/j")) {
      pedido = "plantas";
    } else if (pedido == null && request.getRequestURI().endsWith("/i")) {
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
    // El jardín usa las piezas de las abejas como apoyo: "abejas" queda activo y "plantas" cambia
    // lo protagónico (paleta, hero, recorrido, impacto y cierre).
    boolean plantas = "plantas".equals(tema);
    model.addAttribute("abejas", plantas || "abejas".equals(tema));
    model.addAttribute("plantas", plantas);
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
