package grupo5.clienteliviano.aplicacion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Las raíces de la variante K, ya dibujadas: desde el nudo de origen sale una raíz por categoría,
 * de cada categoría una por subcategoría y de cada subcategoría una por entidad. La geometría se
 * calcula acá (un SVG de {@code ancho} x {@link #ALTO}) para que la plantilla solo recorra trazos,
 * nudos y rótulos. La raíz principal arranca en {@code y = 0}: es el borde de la tierra, donde
 * asoma la planta.
 */
public record ArbolRaices(
    int ancho, int hojas, List<Trazo> trazos, List<Nudo> nudos, List<Rotulo> rotulos) {

  public static final int ALTO = 330;
  private static final int Y_ORIGEN = 44;
  private static final int Y_CATEGORIA = 116;
  private static final int Y_SUBCATEGORIA = 190;
  private static final int Y_ENTIDAD = 262;

  /**
   * Una entidad alcanzada por el camino categoría → subcategoría. {@code entregadas} negativo
   * significa que no se muestran cifras (la raíz ilustrativa de la landing).
   */
  public record Hoja(
      String categoria, String subcategoria, String entidad, int entregadas, int enCamino) {}

  /** {@code nivel}: 0 raíz principal, 1 a categoría, 2 a subcategoría, 3 a entidad, 4 raicilla. */
  public record Trazo(String d, int nivel, boolean enCamino) {}

  public record Nudo(double cx, double cy, double r, int nivel, boolean hueco) {}

  /** {@code clase}: categoria, subcategoria, entidad o dato. */
  public record Rotulo(double x, double y, String ancla, String texto, String clase) {}

  public boolean vacio() {
    return hojas == 0;
  }

  public int alto() {
    return ALTO;
  }

  public static ArbolRaices de(List<Hoja> hojas, int anchoMinimo) {
    Map<String, Map<String, Map<String, int[]>>> arbol = new LinkedHashMap<>();
    for (Hoja h : hojas) {
      int[] c =
          arbol
              .computeIfAbsent(texto(h.categoria(), "Otras"), k -> new LinkedHashMap<>())
              .computeIfAbsent(texto(h.subcategoria(), "Varios"), k -> new LinkedHashMap<>())
              .computeIfAbsent(h.entidad(), k -> new int[] {0, 0});
      if (h.entregadas() < 0) {
        c[0] = -1;
      } else {
        c[0] += h.entregadas();
        c[1] += h.enCamino();
      }
    }
    int n = arbol.values().stream().flatMap(s -> s.values().stream()).mapToInt(Map::size).sum();
    int ancho = Math.max(anchoMinimo, n * 132);
    List<Trazo> trazos = new ArrayList<>();
    List<Nudo> nudos = new ArrayList<>();
    List<Rotulo> rotulos = new ArrayList<>();
    if (n == 0) {
      return new ArbolRaices(ancho, 0, trazos, nudos, rotulos);
    }
    double paso = (double) ancho / n;
    double xOrigen = ancho / 2.0;
    trazos.add(
        new Trazo(
            f(
                "M%.1f 0 C%.1f 16 %.1f 28 %.1f %d",
                xOrigen, xOrigen - 3, xOrigen + 3, xOrigen, Y_ORIGEN),
            0,
            false));
    int i = 0;
    int vuelta = 0;
    for (var cat : arbol.entrySet()) {
      int inicioCat = i;
      int hojasCat = cat.getValue().values().stream().mapToInt(Map::size).sum();
      double xCat = paso * (2 * inicioCat + hojasCat) / 2.0;
      List<double[]> subs = new ArrayList<>();
      for (var sub : cat.getValue().entrySet()) {
        int inicioSub = i;
        List<Object[]> ents = new ArrayList<>();
        for (var ent : sub.getValue().entrySet()) {
          double x = paso * (i + 0.5);
          ents.add(new Object[] {x, ent.getKey(), ent.getValue()});
          i++;
        }
        double xSub = paso * (inicioSub + i) / 2.0;
        subs.add(new double[] {xSub});
        for (Object[] e : ents) {
          double x = (double) e[0];
          int[] c = (int[]) e[2];
          boolean soloCamino = c[0] == 0 && c[1] > 0;
          trazos.add(new Trazo(raiz(xSub, Y_SUBCATEGORIA, x, Y_ENTIDAD), 3, soloCamino));
          nudos.add(new Nudo(x, Y_ENTIDAD, 8, 3, soloCamino));
          rotulos.add(new Rotulo(x, Y_ENTIDAD + 30, "middle", (String) e[1], "entidad"));
          if (c[0] >= 0) {
            rotulos.add(new Rotulo(x, Y_ENTIDAD + 48, "middle", dato(c), "dato"));
          }
        }
        trazos.add(new Trazo(raicilla(xSub, Y_SUBCATEGORIA, vuelta++), 4, false));
        nudos.add(new Nudo(xSub, Y_SUBCATEGORIA, 6, 2, false));
        rotulos.add(
            rotulo(ancho, xSub, Y_SUBCATEGORIA - 9, 11, xSub < xCat, sub.getKey(), "subcategoria"));
      }
      for (double[] s : subs) {
        trazos.add(new Trazo(raiz(xCat, Y_CATEGORIA, s[0], Y_SUBCATEGORIA), 2, false));
      }
      trazos.add(new Trazo(raiz(xOrigen, Y_ORIGEN, xCat, Y_CATEGORIA), 1, false));
      trazos.add(new Trazo(raicilla(xCat, Y_CATEGORIA, vuelta++), 4, false));
      nudos.add(new Nudo(xCat, Y_CATEGORIA, 8, 1, false));
      rotulos.add(
          rotulo(ancho, xCat, Y_CATEGORIA - 10, 13, xCat < xOrigen, cat.getKey(), "categoria"));
    }
    nudos.add(new Nudo(xOrigen, Y_ORIGEN, 12, 0, false));
    // Los trazos más gruesos van debajo: primero la raíz principal y las categorías.
    trazos.sort(
        (a, b) -> Integer.compare(a.nivel() == 4 ? 5 : a.nivel(), b.nivel() == 4 ? 5 : b.nivel()));
    nudos.sort((a, b) -> Integer.compare(b.nivel(), a.nivel()));
    return new ArbolRaices(ancho, n, trazos, nudos, rotulos);
  }

  /** Una raíz que cae y se abre hacia el costado, sin ángulos rectos (no es un organigrama). */
  private static String raiz(double x1, double y1, double x2, double y2) {
    double dy = y2 - y1;
    return f(
        "M%.1f %.1f C%.1f %.1f %.1f %.1f %.1f %.1f",
        x1,
        y1,
        x1 + (x2 - x1) * 0.08,
        y1 + dy * 0.62,
        x2 - (x2 - x1) * 0.12,
        y2 - dy * 0.55,
        x2,
        y2);
  }

  /** Raicilla fina que sale de un nudo hacia abajo, alternando el lado. */
  private static String raicilla(double x, double y, int vuelta) {
    int lado = vuelta % 2 == 0 ? 1 : -1;
    return f(
        "M%.1f %.1f c%d 10 %d 16 %d 30 M%.1f %.1f c%d 6 %d 9 %d 8",
        x,
        y + 4,
        lado * 4,
        lado * 2,
        lado * 10,
        x + lado * 4,
        y + 20,
        lado * 4,
        lado * 9,
        lado * 13);
  }

  /**
   * El rótulo de un nudo va del lado contrario al que llega su raíz: si el nudo quedó a la
   * izquierda de su padre, a la izquierda; si no, a la derecha.
   */
  private static Rotulo rotulo(
      int ancho,
      double x,
      double y,
      double separacion,
      boolean izquierda,
      String texto,
      String clase) {
    // Si no entra de ese lado (ancho estimado del texto), va del otro.
    double largo = texto.length() * 7.6 + separacion;
    if (izquierda && x - largo < 4) {
      izquierda = false;
    } else if (!izquierda && x + largo > ancho - 4) {
      izquierda = true;
    }
    return izquierda
        ? new Rotulo(x - separacion, y, "end", texto, clase)
        : new Rotulo(x + separacion, y, "start", texto, clase);
  }

  private static String dato(int[] c) {
    String entregas = c[0] == 1 ? "1 entrega" : c[0] + " entregas";
    if (c[0] == 0) {
      return c[1] + " en camino";
    }
    return c[1] > 0 ? entregas + " · " + c[1] + " en camino" : entregas;
  }

  private static String texto(String valor, String siFalta) {
    return valor == null || valor.isBlank() ? siFalta : valor;
  }

  private static String f(String formato, Object... args) {
    return String.format(Locale.ROOT, formato, args);
  }
}
