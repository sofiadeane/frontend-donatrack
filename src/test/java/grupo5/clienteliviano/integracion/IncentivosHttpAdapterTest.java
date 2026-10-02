package grupo5.clienteliviano.integracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import grupo5.clienteliviano.integracion.http.ClienteHttp;
import grupo5.clienteliviano.integracion.incentivos.IncentivosHttpAdapter;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/** Incentivos contra un servidor simulado con la forma de respuesta del backend. */
class IncentivosHttpAdapterTest {

  private static final UUID DONANTE = UUID.fromString("a3b40000-0000-4000-8000-000000000001");

  private MockRestServiceServer servidor;
  private IncentivosHttpAdapter adapter;

  @BeforeEach
  void preparar() {
    RestClient.Builder builder = RestClient.builder().baseUrl("http://incentivos");
    servidor = MockRestServiceServer.bindTo(builder).build();
    adapter = new IncentivosHttpAdapter(new ClienteHttp(Servicio.INCENTIVOS, builder.build()));
  }

  @Test
  @DisplayName("Donante sin incentivos (404, ERR-EST-702): perfil vacío, no error")
  void noEncontrado() {
    servidor
        .expect(requestTo("http://incentivos/api/incentivos/donantes/" + DONANTE))
        .andRespond(
            withStatus(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"code\":\"ERR-EST-702\",\"type\":\"BusinessStateException\"}"));
    assertThat(adapter.perfil(DONANTE)).isEmpty();
  }

  @Test
  @DisplayName("Misiones: mapea progreso e insignia (vista previa sin fecha)")
  void misiones() {
    servidor
        .expect(requestTo("http://incentivos/api/incentivos/donantes/" + DONANTE + "/misiones"))
        .andRespond(
            withSuccess(
                """
                [{"nombre":"Donante constante","descripcion":"Tres meses","categoria":"COLABORADOR",
                  "progresoActual":2,"objetivo":3,"porcentaje":66,"distanciaAlObjetivo":1,
                  "completada":false,"fechaCompletada":null,
                  "insignia":{"nombre":"Constancia","descripcion":"x","imagenUrl":null,
                              "visible":true,"fechaObtenida":null}}]
                """,
                MediaType.APPLICATION_JSON));
    assertThat(adapter.misiones(DONANTE))
        .singleElement()
        .satisfies(
            m -> {
              assertThat(m.porcentaje()).isEqualTo(66);
              assertThat(m.insignia().fechaObtenida()).isNull();
            });
  }
}
