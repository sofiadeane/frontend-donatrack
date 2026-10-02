package grupo5.clienteliviano.integracion.notificaciones;

import java.time.LocalDateTime;
import java.util.UUID;

/** Copia de {@code NotificacionDTO}. Estado: PENDIENTE, ENVIADA o FALLIDA. */
public record NotificacionDTO(
    UUID id, String mensaje, String estado, LocalDateTime fechaCreacion) {}
