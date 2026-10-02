package grupo5.clienteliviano.aplicacion;

import java.util.List;

/**
 * Resultado de cargar una sección de una página. Cada sección se carga por separado: si falla un
 * servicio, el resto de la página sigue funcionando (spec §5, estados de UI comunes).
 *
 * @param demo si los datos vienen de fixtures (la vista muestra la etiqueta de demostración)
 */
public record Seccion<T>(Estado estado, List<T> datos, ErrorVista error, boolean demo) {

  /** Estado de una sección. */
  public enum Estado {
    OK,
    VACIO,
    ERROR
  }

  /** Error listo para mostrar: texto para la persona y referencia técnica para soporte. */
  public record ErrorVista(String mensaje, String referencia) {}

  public static <T> Seccion<T> de(List<T> datos, boolean demo) {
    return new Seccion<>(
        datos.isEmpty() ? Estado.VACIO : Estado.OK, List.copyOf(datos), null, demo);
  }

  public static <T> Seccion<T> fallida(ErrorVista error) {
    return new Seccion<>(Estado.ERROR, List.of(), error, false);
  }

  public boolean ok() {
    return estado == Estado.OK;
  }

  public boolean vacia() {
    return estado == Estado.VACIO;
  }

  public boolean conError() {
    return estado == Estado.ERROR;
  }
}
