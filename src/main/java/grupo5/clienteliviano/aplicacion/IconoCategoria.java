package grupo5.clienteliviano.aplicacion;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Ícono para una categoría de bienes. Las categorías las administra el backend (texto libre), así
 * que se reconocen por palabras clave; si no hay coincidencia se usa el ícono genérico de caja.
 */
public final class IconoCategoria {

  private IconoCategoria() {}

  public static String de(String categoria) {
    String c = normalizar(categoria);
    if (c.contains("aliment") || c.contains("comida")) {
      return "cat-alimentos";
    }
    if (c.contains("bebida") || c.contains("leche") || c.contains("agua")) {
      return "cat-bebidas";
    }
    if (c.contains("vestim")
        || c.contains("ropa")
        || c.contains("abrigo")
        || c.contains("calzado")) {
      return "cat-vestimenta";
    }
    if (c.contains("mobil") || c.contains("mueble")) {
      return "cat-mobiliario";
    }
    if (c.contains("escolar") || c.contains("util") || c.contains("libro")) {
      return "cat-escolares";
    }
    if (c.contains("higiene") || c.contains("limpieza")) {
      return "cat-higiene";
    }
    return "donaciones";
  }

  private static String normalizar(String texto) {
    if (texto == null) {
      return "";
    }
    return Normalizer.normalize(texto, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase(Locale.ROOT);
  }
}
