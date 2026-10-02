package grupo5.clienteliviano.integracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.error.BackendException.Tipo;
import grupo5.clienteliviano.integracion.http.ClienteHttp;
import grupo5.clienteliviano.integracion.personas.RegistroHttpAdapter;
import grupo5.clienteliviano.integracion.personas.dto.DireccionInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.MedioDeContactoInputDTO.CorreoInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.MedioDeContactoInputDTO.WhatsAppInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.PersonaInputDTO.HumanaInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.PersonaInputDTO.JuridicaInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.TipoJuridico;
import grupo5.clienteliviano.integracion.personas.dto.TipoPersona;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/** Altas contra un servidor simulado: el cuerpo enviado tiene la forma que espera el backend. */
class RegistroHttpAdapterTest {

  private static final UUID PERSONA = UUID.fromString("9e000000-0000-4000-8000-000000000001");

  private MockRestServiceServer servidor;
  private RegistroHttpAdapter adapter;

  @BeforeEach
  void preparar() {
    RestClient.Builder builder = RestClient.builder().baseUrl("http://donaciones");
    servidor = MockRestServiceServer.bindTo(builder).build();
    adapter = new RegistroHttpAdapter(new ClienteHttp(Servicio.DONACIONES, builder.build()));
  }

  @Test
  @DisplayName("Persona humana: tipo y medios de contacto polimórficos como los lee el backend")
  void humana() {
    servidor
        .expect(requestTo("http://donaciones/api/personas"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(
            content()
                .json(
                    """
                    {"tipo":"HUMANA","nombre":"Lucía","apellido":"Ferreyra",
                     "mediosDeContacto":[
                       {"tipo":"CORREO","esPredeterminado":true,"direccionCorreo":"lucia@ejemplo.org"},
                       {"tipo":"WHATSAPP","esPredeterminado":false,"caracteristica":"+54",
                        "codigoArea":"11","numero":"5555-0101"}],
                     "direccion":{"calle":"Corrientes","altura":1234,"localidad":"CABA",
                       "provincia":"Ciudad Autónoma de Buenos Aires","pais":"Argentina"}}
                    """))
        .andRespond(
            withSuccess(
                """
                {"tipo":"HUMANA","id":"%s","nombre":"Lucía","apellido":"Ferreyra",
                 "mediosDeContacto":[],"direccion":null}
                """
                    .formatted(PERSONA),
                MediaType.APPLICATION_JSON));

    var creada =
        adapter.crearPersona(
            new HumanaInputDTO(
                TipoPersona.HUMANA,
                null,
                null,
                new DireccionInputDTO(
                    "Corrientes",
                    1234,
                    null,
                    null,
                    null,
                    "CABA",
                    "Ciudad Autónoma de Buenos Aires",
                    "Argentina"),
                List.of(
                    new CorreoInputDTO(true, "lucia@ejemplo.org"),
                    new WhatsAppInputDTO(false, "+54", "11", "5555-0101")),
                "Lucía",
                "Ferreyra",
                null,
                null));

    assertThat(creada.id()).isEqualTo(PERSONA);
    servidor.verify();
  }

  @Test
  @DisplayName("Organización con representantes y después alta de entidad beneficiaria")
  void juridicaYEntidad() {
    servidor
        .expect(requestTo("http://donaciones/api/personas"))
        .andExpect(
            content()
                .json(
                    """
                    {"tipo":"JURIDICA","razonSocial":"Comedor Los Aromos","tipoJuridico":"ONG",
                     "representantes":[{"tipo":"HUMANA","nombre":"Marta","apellido":"Sosa"}]}
                    """))
        .andRespond(
            withSuccess(
                "{\"tipo\":\"JURIDICA\",\"id\":\"" + PERSONA + "\"}", MediaType.APPLICATION_JSON));
    servidor
        .expect(requestTo("http://donaciones/api/entidades"))
        .andExpect(content().json("{\"juridicaId\":\"" + PERSONA + "\"}"))
        .andRespond(
            withSuccess(
                "{\"id\":\"e0000000-0000-4000-8000-000000000001\",\"juridica\":{}}",
                MediaType.APPLICATION_JSON));

    var creada =
        adapter.crearPersona(
            new JuridicaInputDTO(
                TipoPersona.JURIDICA,
                null,
                null,
                null,
                List.of(new CorreoInputDTO(true, "aromos@ejemplo.org")),
                "Comedor Los Aromos",
                TipoJuridico.ONG,
                null,
                List.of(
                    new HumanaInputDTO(
                        TipoPersona.HUMANA, null, null, null, null, "Marta", "Sosa", null, null))));
    UUID entidad = adapter.crearEntidad(creada.id());

    assertThat(entidad).isEqualTo(UUID.fromString("e0000000-0000-4000-8000-000000000001"));
    servidor.verify();
  }

  @Test
  @DisplayName("400 con errores por campo: se conservan para mostrarlos en el formulario")
  void validacion() {
    servidor
        .expect(requestTo("http://donaciones/api/personas"))
        .andRespond(
            withStatus(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    """
                    {"code":"ERR-CSR-003","type":"MethodArgumentNotValidException",
                     "details":"Validación fallida para 1 campo(s)","traceId":"t-1",
                     "timestamp":"2026-10-01T12:00:00",
                     "errors":[{"field":"direccion.calle","message":"La calle es obligatoria",
                                "rejectedValue":""}]}
                    """));

    assertThatThrownBy(
            () ->
                adapter.crearPersona(
                    new HumanaInputDTO(
                        TipoPersona.HUMANA, null, null, null, List.of(), "A", "B", null, null)))
        .isInstanceOfSatisfying(
            BackendException.class,
            e -> {
              assertThat(e.tipo()).isEqualTo(Tipo.VALIDACION);
              assertThat(e.error().errors().getFirst().field()).isEqualTo("direccion.calle");
            });
  }
}
