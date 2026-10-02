package grupo5.clienteliviano.integracion.error;

import grupo5.clienteliviano.integracion.Servicio;

/** Falla al hablar con un servicio del backend, ya clasificada para la interfaz. */
public class BackendException extends RuntimeException {

  /** Clasificación de la falla, independiente del código HTTP. */
  public enum Tipo {
    /** No se pudo conectar o se agotó el tiempo de espera. */
    NO_DISPONIBLE,
    /** 404. */
    NO_ENCONTRADO,
    /** 400: datos inválidos (con errores por campo si el backend los envía). */
    VALIDACION,
    /** 409: regla de negocio o transición de estado inválida. */
    CONFLICTO,
    /** Cualquier otra respuesta de error. */
    ERROR
  }

  private final Servicio servicio;
  private final Tipo tipo;
  private final int status;
  private final transient ErrorBackend error;

  public BackendException(
      Servicio servicio, Tipo tipo, int status, ErrorBackend error, Throwable causa) {
    super(servicio + " · " + tipo + (error != null ? " · " + error.code() : ""), causa);
    this.servicio = servicio;
    this.tipo = tipo;
    this.status = status;
    this.error = error;
  }

  public Servicio servicio() {
    return servicio;
  }

  public Tipo tipo() {
    return tipo;
  }

  public int status() {
    return status;
  }

  /**
   * Cuerpo de error del backend, o {@code null} si no hubo respuesta o no era el formato estándar.
   */
  public ErrorBackend error() {
    return error;
  }

  public String codigo() {
    return error != null ? error.code() : null;
  }

  public String traceId() {
    return error != null ? error.traceId() : null;
  }
}
