package grupo5.clienteliviano;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * La paleta vive solo en {@code static/css/tokens.css} (decisión 2026-10-01). Este test falla si
 * aparece un color literal (hex, rgb, hsl) en cualquier otro CSS, JS o plantilla. Los SVG de {@code
 * static/img} son assets exportados del Figma y quedan excluidos.
 */
class FuenteUnicaDeColorTest {

  private static final Path RECURSOS = Path.of("src/main/resources");
  private static final Pattern COLOR_EN_CODIGO =
      Pattern.compile(
          "(?<![\\w&])#(?:[0-9a-fA-F]{8}|[0-9a-fA-F]{6}|[0-9a-fA-F]{3})(?![\\w-])|\\b(?:rgba?|hsla?)\\(");
  private static final Pattern COLOR_EN_PLANTILLA =
      Pattern.compile(
          "(?:style|fill|stroke|stop-color|color)\\s*=\\s*\"[^\"]*(?:#[0-9a-fA-F]{3,8}|rgba?\\(|hsla?\\()");

  @Test
  @DisplayName("No hay colores literales fuera de tokens.css")
  void sinColoresFueraDeTokens() throws IOException {
    List<String> infracciones = new ArrayList<>();
    try (Stream<Path> archivos = Files.walk(RECURSOS)) {
      for (Path archivo : archivos.filter(Files::isRegularFile).toList()) {
        String nombre = archivo.toString().replace('\\', '/');
        if (nombre.endsWith("/css/tokens.css") || nombre.contains("/static/img/")) {
          continue;
        }
        Pattern patron;
        if (nombre.endsWith(".css") || nombre.endsWith(".js")) {
          patron = COLOR_EN_CODIGO;
        } else if (nombre.endsWith(".html")) {
          patron = COLOR_EN_PLANTILLA;
        } else {
          continue;
        }
        List<String> lineas = Files.readAllLines(archivo);
        for (int i = 0; i < lineas.size(); i++) {
          Matcher m = patron.matcher(lineas.get(i));
          if (m.find()) {
            infracciones.add(nombre + ":" + (i + 1) + " → " + lineas.get(i).trim());
          }
        }
      }
    }
    assertThat(infracciones).as("Colores fuera de tokens.css").isEmpty();
  }
}
