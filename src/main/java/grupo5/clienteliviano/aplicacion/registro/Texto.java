package grupo5.clienteliviano.aplicacion.registro;

/** Utilidades mínimas para los valores de texto del formulario. */
final class Texto {

  private Texto() {}

  static boolean conValor(String s) {
    return s != null && !s.isBlank();
  }

  /** {@code null} si está vacío; si no, recortado. */
  static String limpio(String s) {
    return conValor(s) ? s.strip() : null;
  }
}
