package grupo5.clienteliviano.integracion.donaciones.dto;

import java.time.LocalDate;

/**
 * Copia de {@code donaciones-service/.../dto/donacionesIndependientes/BienResumenDTO}. {@code
 * estado} es el enum {@code Estado} del backend (NUEVO, USADO, DESGASTADO); se recibe como texto.
 */
public record BienResumenDTO(
    String descripcion, String fotoUrl, LocalDate fechaVencimiento, String estado) {}
