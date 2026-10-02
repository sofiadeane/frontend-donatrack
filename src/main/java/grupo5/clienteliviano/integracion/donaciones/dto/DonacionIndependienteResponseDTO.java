package grupo5.clienteliviano.integracion.donaciones.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Copia de {@code
 * donaciones-service/.../dto/donacionesIndependientes/DonacionIndependienteResponseDTO}. Respuesta
 * de {@code GET /donaciones-independientes} y {@code GET /donaciones-independientes/{id}}.
 */
public record DonacionIndependienteResponseDTO(
    UUID id,
    UUID donacionOriginalId,
    String descripcion,
    String estadoActual,
    LocalDateTime fechaRegistro,
    List<CambioEstadoDIResponseDTO> historial,
    List<ItemDonacionIndependienteResponseDTO> items,
    int cantidad) {}
