package grupo5.clienteliviano.integracion.error;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Copia del contrato de error del backend ({@code common-lib/.../responses/ErrorResponse}). Los
 * mensajes son códigos de máquina; el texto para la persona lo decide el frontend.
 */
public record ErrorBackend(
    String code,
    String type,
    String details,
    String traceId,
    LocalDateTime timestamp,
    List<ErrorDeCampo> errors) {

  /** Copia de {@code FieldErrorDTO}. */
  public record ErrorDeCampo(String field, String message, Object rejectedValue) {}
}
