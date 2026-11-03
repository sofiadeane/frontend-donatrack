package grupo5.clienteliviano;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** Variante visual C, "Suave": landing en {@code /c} y panel en {@code /admin/c}. */
@SpringBootTest
@AutoConfigureMockMvc
class VarianteCTest {

  @Autowired private MockMvc mvc;

  @Test
  @DisplayName("La landing C muestra el hero, el recorrido y las cifras públicas")
  void landing() throws Exception {
    mvc.perform(get("/c"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("de punta a punta")))
        .andExpect(content().string(containsString("data-recorrido")))
        .andExpect(content().string(containsString("Transparencia")))
        .andExpect(content().string(containsString("Preguntas frecuentes")))
        .andExpect(content().string(containsString("Donaciones entregadas")))
        .andExpect(content().string(containsString("/css/c-landing.css")));
  }

  @Test
  @DisplayName("Las versiones anteriores siguen disponibles en /b y /v1")
  void versionesAnteriores() throws Exception {
    mvc.perform(get("/v1"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("titulo-hero")));
  }

  @Test
  @DisplayName("El panel de administración C muestra flujo, pendientes, logística y tabla")
  void panelAdmin() throws Exception {
    MvcResult ingreso =
        mvc.perform(post("/ingresar").param("identidad", "admin-deposito")).andReturn();
    MockHttpSession session = (MockHttpSession) ingreso.getRequest().getSession(false);
    mvc.perform(get("/admin/c").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("El depósito")))
        .andExpect(content().string(containsString("En circulación")))
        .andExpect(content().string(containsString("Pendientes")))
        .andExpect(content().string(containsString("Logística")))
        .andExpect(content().string(containsString("tabla-atencion")))
        .andExpect(content().string(containsString("Datos de demostración")))
        .andExpect(content().string(containsString("/css/c-panel.css")));
  }
}
