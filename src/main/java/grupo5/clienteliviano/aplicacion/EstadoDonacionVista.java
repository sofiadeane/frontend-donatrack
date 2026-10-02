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

  /** Ícono del pill: refuerza el estado con una forma, no solo con el texto. */
  public String icono() {
    return switch (codigo == null ? "" : codigo) {
      case "EN_DEPOSITO" -> "donaciones";
      case "ASIGNACION_REALIZADA" -> "asignaciones";
      case "LISTA_PARA_ENTREGAR" -> "necesidades";
      case "EN_TRASLADO" -> "camiones";
      case "ENTREGADA" -> "check";
      case "VENCIDA" -> "reloj";
      default -> "alerta";
    };
  }

  public String claseFase() {
    return fase.name().toLowerCase().replace('_', '-');
  }
}
