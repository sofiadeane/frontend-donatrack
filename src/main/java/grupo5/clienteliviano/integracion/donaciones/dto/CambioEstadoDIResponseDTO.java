package grupo5.clienteliviano.integracion.donaciones.dto;

import java.time.LocalDateTime;

/**
 * Copia de {@code donaciones-service/.../dto/donacionesIndependientes/CambioEstadoDIResponseDTO}.
 */
public record CambioEstadoDIResponseDTO(
    String estadoAnterior,
    String estadoNuevo,
    LocalDateTime timestamp,
    String justificacion,
    String actor) {}
