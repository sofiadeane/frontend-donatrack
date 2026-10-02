package grupo5.clienteliviano.aplicacion;

import grupo5.clienteliviano.aplicacion.Seccion.ErrorVista;

/**
 * Un dato de una sección que no es una lista (p. ej. un tile): presente, ausente o con error. Igual
 * que {@link Seccion}, si un servicio falla solo se ve afectado su bloque.
 */
public record Bloque<T>(T datos, ErrorVista error, boolean demo) {

  public static <T> Bloque<T> de(T datos, boolean demo) {
    return new Bloque<>(datos, null, demo);
  }

  public static <T> Bloque<T> fallido(ErrorVista error) {
    return new Bloque<>(null, error, false);
  }

  public boolean ok() {
    return datos != null;
  }

  public boolean vacio() {
    return datos == null && error == null;
  }

  public boolean conError() {
    return error != null;
  }
}
