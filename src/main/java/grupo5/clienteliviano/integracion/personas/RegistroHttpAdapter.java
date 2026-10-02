package grupo5.clienteliviano.integracion.personas;

import grupo5.clienteliviano.integracion.http.ClienteHttp;
import grupo5.clienteliviano.integracion.personas.dto.PersonaInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.RegistroDTOs.DonanteCreadoDTO;
import grupo5.clienteliviano.integracion.personas.dto.RegistroDTOs.DonanteInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.RegistroDTOs.EntidadBeneficiariaInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.RegistroDTOs.EntidadCreadaDTO;
import grupo5.clienteliviano.integracion.personas.dto.RegistroDTOs.PersonaCreadaDTO;
import java.util.UUID;
import org.springframework.http.MediaType;

/** Adapter real de las altas en donaciones-service. */
public class RegistroHttpAdapter implements RegistroPort {

  private final ClienteHttp cliente;

  public RegistroHttpAdapter(ClienteHttp cliente) {
    this.cliente = cliente;
  }

  @Override
  public PersonaCreadaDTO crearPersona(PersonaInputDTO persona) {
    return cliente.ejecutar(
        rest ->
            rest.post()
                .uri("/api/personas")
                .contentType(MediaType.APPLICATION_JSON)
                .body(persona)
                .retrieve()
                .body(PersonaCreadaDTO.class));
  }

  @Override
  public UUID crearDonante(UUID personaId) {
    return cliente
        .ejecutar(
            rest ->
                rest.post()
                    .uri("/api/donantes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new DonanteInputDTO(personaId))
                    .retrieve()
                    .body(DonanteCreadoDTO.class))
        .idDonante();
  }

  @Override
  public UUID crearEntidad(UUID juridicaId) {
    return cliente
        .ejecutar(
            rest ->
                rest.post()
                    .uri("/api/entidades")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new EntidadBeneficiariaInputDTO(juridicaId))
                    .retrieve()
                    .body(EntidadCreadaDTO.class))
        .id();
  }

  @Override
  public boolean esDemo() {
    return false;
  }
}
