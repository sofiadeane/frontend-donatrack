package grupo5.clienteliviano.integracion.personas;

import grupo5.clienteliviano.integracion.personas.dto.PersonaInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.RegistroDTOs.PersonaCreadaDTO;
import java.util.UUID;

/** Modo demostración: acepta las altas pero no guarda nada (ids nuevos en cada llamada). */
public class RegistroFixtureAdapter implements RegistroPort {

  @Override
  public PersonaCreadaDTO crearPersona(PersonaInputDTO persona) {
    return new PersonaCreadaDTO(persona.tipo(), UUID.randomUUID());
  }

  @Override
  public UUID crearDonante(UUID personaId) {
    return UUID.randomUUID();
  }

  @Override
  public UUID crearEntidad(UUID juridicaId) {
    return UUID.randomUUID();
  }

  @Override
  public boolean esDemo() {
    return true;
  }
}
