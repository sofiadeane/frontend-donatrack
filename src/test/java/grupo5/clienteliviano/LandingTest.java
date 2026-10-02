package grupo5.clienteliviano;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import grupo5.clienteliviano.aplicacion.LandingService;
import grupo5.clienteliviano.aplicacion.LandingService.DonacionDestacada;
import grupo5.clienteliviano.integracion.Servicio;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionOutputDTO.PersonaResumenDTO;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.error.BackendException.Tipo;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Landing pública (R1) con fecha fija: "hoy" es 1 de octubre de 2026. */
class LandingTest {

  @TestConfiguration
  static class RelojFijo {
    @Bean
    @Primary
    Clock relojFijo() {
      return Clock.fixed(
          Instant.parse("2026-10-01T15:00:00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
    }
  }

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  @Import(RelojFijo.class)
  class ConFixtures {
    @Autowired private MockMvc mvc;
    @Autowired private LandingService landing;

    @Test
    @DisplayName("Destacadas: solo entregas de los últimos 30 días, la más reciente primero")
    void destacadas() {
      var seccion = landing.destacadasDelUltimoMes();
      assertThat(seccion.datos())
          .extracting(DonacionDestacada::titulo)
          .containsExactly(
              "Pupitres y sillas reacondicionadas para dos aulas",
              "Frazadas y mantas para personas mayores",
              "Cuadernos y lápices para el inicio de clases",
              "Fideos secos tipo mostachol",
              "Conservas de tomate para meriendas comunitarias")
          .doesNotContain("Cuadernos rayados tamaño A4");
      assertThat(seccion.datos().getFirst())
          .returns("Cooperativa La Huerta", DonacionDestacada::donante)
          .returns("cat-mobiliario", DonacionDestacada::iconoCategoria);
      assertThat(seccion.demo()).isTrue();
    }

    @Test
    @DisplayName(
        "La landing muestra hero, cómo funciona, destacadas, números y llamado a la acción")
    void pagina() throws Exception {
      mvc.perform(get("/"))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("<h1 id=\"titulo-hero\">DonaTrack</h1>")))
          .andExpect(content().string(containsString("Cómo funciona")))
          .andExpect(content().string(containsString("Pupitres y sillas reacondicionadas")))
          .andExpect(content().string(containsString("Nicolás P.")))
          .andExpect(content().string(containsString("Panadería El Molino")))
          .andExpect(content().string(not(containsString("Entregada el"))))
          .andExpect(content().string(containsString("2.450")))
          .andExpect(content().string(containsString("desplegable--arriba")))
          .andExpect(content().string(containsString("Cifras de demostración")))
          .andExpect(content().string(containsString("/registro?tipo=entidad")));
    }

    @Test
    @DisplayName("Persona humana: nombre e inicial del apellido; persona jurídica: razón social")
    void nombrePublico() {
      assertThat(LandingService.nombrePublico(persona("HUMANA", "Nicolás", "Pérez", null)))
          .isEqualTo("Nicolás P.");
      assertThat(LandingService.nombrePublico(persona("HUMANA", "Nicolás", null, null)))
          .isEqualTo("Nicolás");
      assertThat(LandingService.nombrePublico(persona("JURIDICA", null, null, "La Huerta")))
          .isEqualTo("La Huerta");
    }

    @Test
    @DisplayName("El mapa de impacto se muestra como pendiente, sin simularlo")
    void mapa() throws Exception {
      mvc.perform(get("/mapa-de-impacto"))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("todavía no está disponible")));
    }
  }

  private static PersonaResumenDTO persona(
      String tipo, String nombre, String apellido, String razonSocial) {
    return new PersonaResumenDTO(tipo, null, nombre, apellido, razonSocial);
  }

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  @Import(RelojFijo.class)
  class ConDonacionesCaido {
    @Autowired private MockMvc mvc;
    @MockitoBean private DonacionesPort donaciones;

    @Test
    @DisplayName("Si donaciones-service falla, la landing sigue y avisa en la sección")
    void error() throws Exception {
      when(donaciones.donacionesIndependientes(any()))
          .thenThrow(new BackendException(Servicio.DONACIONES, Tipo.NO_DISPONIBLE, 0, null, null));
      mvc.perform(get("/"))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("No pudimos conectarnos")))
          .andExpect(content().string(containsString("Transparencia en números")))
          .andExpect(content().string(not(containsString("tarjeta-donacion__cuerpo"))));
    }
  }
}
