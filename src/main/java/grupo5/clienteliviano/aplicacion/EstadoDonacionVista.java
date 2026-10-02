package grupo5.clienteliviano.aplicacion;

/**
 * Presentación de un estado de {@code TipoEstadoDonacion} del backend. La etiqueta sale de {@code
 * messages.properties}; la fase define el estilo del punto del pill (sin colores nuevos: la paleta
 * de estados está pendiente de aprobación).
 */
public record EstadoDonacionVista(String codigo, String etiqueta, Fase fase) {

  /** Fase del recorrido de una donación. */
  public enum Fase {
    EN_ESPERA,
    ASIGNADA,
    EN_CAMINO,
    COMPLETADA,
    INTERRUMPIDA
  }

  public static Fase faseDe(String codigo) {
    return switch (codigo == null ? "" : codigo) {
      case "EN_DEPOSITO" -> Fase.EN_ESPERA;
      case "ASIGNACION_REALIZADA" -> Fase.ASIGNADA;
      case "LISTA_PARA_ENTREGAR", "EN_TRASLADO" -> Fase.EN_CAMINO;
      case "ENTREGADA" -> Fase.COMPLETADA;
      default -> Fase.INTERRUMPIDA;
    };
  }

  public String claseFase() {
    return fase.name().toLowerCase().replace('_', '-');
  }
}
