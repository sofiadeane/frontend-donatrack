package grupo5.clienteliviano.integracion.personas.dto;

import java.util.UUID;

/** Cuerpos y respuestas de alta (copias parciales: solo los campos que usa el cliente). */
public final class RegistroDTOs {

  private RegistroDTOs() {}

  /** Copia parcial de {@code PersonaOutputDTO}. */
  public record PersonaCreadaDTO(TipoPersona tipo, UUID id) {}

  /** Copia de {@code DonanteInputDTO}. */
  public record DonanteInputDTO(UUID idPersona) {}

  /** Copia parcial de {@code DonanteOutputDTO}. */
  public record DonanteCreadoDTO(UUID idDonante) {}

  /** Copia de {@code EntidadBeneficiariaInputDTO}. */
  public record EntidadBeneficiariaInputDTO(UUID juridicaId) {}

  /** Copia parcial de {@code EntidadBeneficiariaOutputDTO}. */
  public record EntidadCreadaDTO(UUID id) {}
}
