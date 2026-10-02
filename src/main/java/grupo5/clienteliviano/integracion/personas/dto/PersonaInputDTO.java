package grupo5.clienteliviano.integracion.personas.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Copia de {@code PersonaInputDTO} y sus variantes. El backend elige la variante por {@code tipo};
 * cada record lo serializa como un campo más.
 */
public sealed interface PersonaInputDTO {
  TipoPersona tipo();

  /** Copia de {@code HumanaInputDTO}: nombre y apellido obligatorios. */
  record HumanaInputDTO(
      TipoPersona tipo,
      TipoDocumento tipoDocumento,
      String documento,
      DireccionInputDTO direccion,
      List<MedioDeContactoInputDTO> mediosDeContacto,
      String nombre,
      String apellido,
      Genero genero,
      LocalDate fechaNacimiento)
      implements PersonaInputDTO {}

  /** Copia de {@code JuridicaInputDTO}: razón social y tipo jurídico obligatorios. */
  record JuridicaInputDTO(
      TipoPersona tipo,
      TipoDocumento tipoDocumento,
      String documento,
      DireccionInputDTO direccion,
      List<MedioDeContactoInputDTO> mediosDeContacto,
      String razonSocial,
      TipoJuridico tipoJuridico,
      String rubro,
      List<HumanaInputDTO> representantes)
      implements PersonaInputDTO {}
}
