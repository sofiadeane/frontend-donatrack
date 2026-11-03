package grupo5.clienteliviano;

import static grupo5.clienteliviano.DonanteInicioTest.ingresar;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/** Entidades del donante: hormiguero (una cámara por entidad) y tabla filtrable del resto. */
@SpringBootTest
@AutoConfigureMockMvc
class EntidadesTest {

  private static final String GIRASOLES = "e7a10000-0000-4000-8000-000000000001";

  @Autowired private MockMvc mvc;

  @Test
  @DisplayName(
      "Una cámara por entidad, en el orden en que llegó cada donación; abre la más reciente")
  void hormiguero() throws Exception {
    mvc.perform(get("/donante/entidades").session(ingresar(mvc)))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("3 cámaras")))
        .andExpect(
            content()
                .string(
                    stringContainsInOrder(
                        "Escuela 12 · Cooperadora", "Comedor Los Girasoles", "Hogar Esperanza")))
        .andExpect(content().string(containsString("aria-current=\"true\"")))
        .andExpect(
            content()
                .string(stringContainsInOrder("Cámara abierta", "Hogar Esperanza", "Camperas")));
  }

  @Test
  @DisplayName("Elegir una cámara es un enlace: el servidor arma su ficha")
  void seleccion() throws Exception {
    mvc.perform(get("/donante/entidades").param("entidad", GIRASOLES).session(ingresar(mvc)))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    stringContainsInOrder(
                        "Cámara abierta", "Comedor Los Girasoles", "Arroz largo fino", "Fideos")));
  }

  @Test
  @DisplayName("La tabla lista solo las entidades a las que todavía no donó y se filtra por GET")
  void tabla() throws Exception {
    mvc.perform(get("/donante/entidades").session(ingresar(mvc)))
        .andExpect(content().string(containsString("7 entidades")))
        .andExpect(content().string(containsString("Biblioteca Popular Raíces")));
    mvc.perform(get("/donante/entidades").param("tipo", "Merendero").session(ingresar(mvc)))
        .andExpect(content().string(containsString("Merendero Norte")))
        .andExpect(content().string(not(containsString("Biblioteca Popular Raíces"))));
    mvc.perform(get("/donante/entidades").param("q", "raices").session(ingresar(mvc)))
        .andExpect(content().string(containsString("1 entidad")))
        .andExpect(content().string(containsString("Biblioteca Popular Raíces")));
  }
}
