package grupo5.clienteliviano.integracion.donaciones.dto;

import java.util.UUID;

/**
 * Copia <b>parcial</b> de {@code donaciones-service/.../dto/donaciones/outputs/DonacionOutputDTO}
 * (respuesta de {@code GET /api/donaciones/{id}}): solo los campos que usa el frontend. Los demás
 * se ignoran al leer la respuesta real.
 */
public record DonacionOutputDTO(UUID id, DonanteResumenDTO donante) {

  /** Copia parcial de {@code DonanteResumenDTO}. */
  public record DonanteResumenDTO(UUID idDonante, UUID personaId, PersonaResumenDTO persona) {}

  /**
   * Copia parcial de {@code PersonaOutputDTO} (polimórfico por {@code tipo}: HUMANA con nombre y
   * apellido, JURIDICA con razón social).
   */
  public record PersonaResumenDTO(
      String tipo, UUID id, String nombre, String apellido, String razonSocial) {}
}
