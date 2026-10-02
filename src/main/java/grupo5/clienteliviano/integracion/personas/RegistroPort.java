package grupo5.clienteliviano.integracion.personas;

import grupo5.clienteliviano.integracion.personas.dto.PersonaInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.RegistroDTOs.PersonaCreadaDTO;
import java.util.UUID;

/** Altas de personas, donantes y entidades beneficiarias en donaciones-service. */
public interface RegistroPort {

  /** {@code POST /api/personas}. */
  PersonaCreadaDTO crearPersona(PersonaInputDTO persona);

  /** {@code POST /api/donantes}: devuelve el id del donante. */
  UUID crearDonante(UUID personaId);

  /** {@code POST /api/entidades}: devuelve el id de la entidad beneficiaria. */
  UUID crearEntidad(UUID juridicaId);

  boolean esDemo();
}
