package grupo5.clienteliviano.integracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import grupo5.clienteliviano.integracion.donaciones.DonacionesHttpAdapter;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort.FiltroDonaciones;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.error.BackendException.Tipo;
import grupo5.clienteliviano.integracion.http.ClienteHttp;
import java.io.IOException;
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

/** Adapter real contra un servidor simulado con respuestas con la forma exacta del backend. */
class DonacionesHttpAdapterTest {

  private static final UUID DONANTE = UUID.fromString("a3b40000-0000-4000-8000-000000000001");

  private MockRestServiceServer servidor;
  private DonacionesHttpAdapter adapter;

  @BeforeEach
  void preparar() {
    RestClient.Builder builder = RestClient.builder().baseUrl("http://donaciones");
    servidor = MockRestServiceServer.bindTo(builder).build();
    adapter = new DonacionesHttpAdapter(new ClienteHttp(Servicio.DONACIONES, builder.build()));
  }

  @Test
  @DisplayName("Envía los filtros como query params y mapea la respuesta al DTO")
  void listaConFiltros() {
    servidor
        .expect(
            requestTo(
                "http://donaciones/donaciones-independientes?donanteId="
                    + DONANTE
                    + "&estado=EN_TRASLADO"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(
            withSuccess(
                """
                [{"id":"d0000000-0000-4000-8000-000000000001",
                  "donacionOriginalId":"0d000000-0000-4000-8000-000000000001",
                  "descripcion":"Arroz","estadoActual":"EN_TRASLADO",
                  "fechaRegistro":"2026-09-28T10:15:00",
                  "historial":[{"estadoAnterior":null,"estadoNuevo":"EN_DEPOSITO",
                    "timestamp":"2026-09-28T10:15:00","justificacion":null,"actor":"SISTEMA"}],
                  "items":[{"bien":{"bien":{"descripcion":"Arroz","fotoUrl":null,
                    "fechaVencimiento":"2027-03-01","estado":"NUEVO"},
                    "subcategoria":{"id":"5b000000-0000-4000-8000-000000000001","nombre":"Arroz"},
                    "categoria":{"id":"c1000000-0000-4000-8000-000000000001","nombre":"Alimentos",
                    "unidad":"KILOGRAMO"}},"cantidad":50}],
                  "cantidad":50,
                  "campoNuevoDelBackend":"se ignora: los cambios aditivos no rompen el front"}]
                """,
                MediaType.APPLICATION_JSON));

    List<DonacionIndependienteResponseDTO> lista =
        adapter.donacionesIndependientes(new FiltroDonaciones(DONANTE, "EN_TRASLADO", null));

    assertThat(lista).hasSize(1);
    assertThat(lista.getFirst().estadoActual()).isEqualTo("EN_TRASLADO");
    assertThat(lista.getFirst().items().getFirst().bien().categoria().unidad())
        .isEqualTo("KILOGRAMO");
    servidor.verify();
  }

  @Test
  @DisplayName("Un error con el cuerpo estándar del backend conserva código y traceId")
  void errorEstandar() {
    servidor
        .expect(requestTo("http://donaciones/donaciones-independientes"))
        .andRespond(
            withStatus(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    """
                    {"code":"ERR-VAL-410","type":"BusinessStateException",
                     "details":"ESTADO_DONACION_TRANSICION_INVALIDA","traceId":"abc123",
                     "timestamp":"2026-10-01T12:00:00"}
                    """));

    assertThatThrownBy(
            () -> adapter.donacionesIndependientes(new FiltroDonaciones(null, null, null)))
        .isInstanceOfSatisfying(
            BackendException.class,
            e -> {
              assertThat(e.tipo()).isEqualTo(Tipo.VALIDACION);
              assertThat(e.codigo()).isEqualTo("ERR-VAL-410");
              assertThat(e.traceId()).isEqualTo("abc123");
            });
  }

  @Test
  @DisplayName("Un 404 sin cuerpo se clasifica igual, sin código")
  void noEncontradoSinCuerpo() {
    servidor
        .expect(requestTo("http://donaciones/donaciones-independientes"))
        .andRespond(withStatus(HttpStatus.NOT_FOUND));

    assertThatThrownBy(
            () -> adapter.donacionesIndependientes(new FiltroDonaciones(null, null, null)))
        .isInstanceOfSatisfying(
            BackendException.class,
            e -> {
              assertThat(e.tipo()).isEqualTo(Tipo.NO_ENCONTRADO);
              assertThat(e.codigo()).isNull();
            });
  }

  @Test
  @DisplayName("Si el servicio no responde, la falla es NO_DISPONIBLE")
  void servicioCaido() {
    servidor
        .expect(requestTo("http://donaciones/donaciones-independientes"))
        .andRespond(withException(new IOException("Connection refused")));

    assertThatThrownBy(
            () -> adapter.donacionesIndependientes(new FiltroDonaciones(null, null, null)))
        .isInstanceOfSatisfying(
            BackendException.class, e -> assertThat(e.tipo()).isEqualTo(Tipo.NO_DISPONIBLE));
  }
}
