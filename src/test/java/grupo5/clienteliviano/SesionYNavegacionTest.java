package grupo5.clienteliviano;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class SesionYNavegacionTest {

  @Autowired private MockMvc mvc;

  /** El ingreso renueva la sesión (evita fijación de sesión): se devuelve la sesión nueva. */
  private MockHttpSession ingresarComo(String identidad) throws Exception {
    MvcResult resultado =
        mvc.perform(post("/ingresar").param("identidad", identidad))
            .andExpect(status().is3xxRedirection())
            .andReturn();
    return (MockHttpSession) resultado.getRequest().getSession(false);
  }

  @Test
  @DisplayName("El inicio público responde sin sesión")
  void inicioPublico() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("lang=\"es\"")))
        .andExpect(content().string(containsString("Saltar al contenido")));
  }

  @Test
  @DisplayName("Sin sesión, el área privada redirige al ingreso")
  void areaPrivadaSinSesion() throws Exception {
    mvc.perform(get("/donante")).andExpect(redirectedUrl("/ingresar?motivo=sin-sesion"));
    mvc.perform(get("/admin/camiones")).andExpect(redirectedUrl("/ingresar?motivo=sin-sesion"));
  }

  @Test
  @DisplayName("Ingresar con una identidad lleva al inicio de su rol")
  void ingresoRedirigeAlRol() throws Exception {
    mvc.perform(post("/ingresar").param("identidad", "entidad-girasoles"))
        .andExpect(redirectedUrl("/entidad"));
  }

  @Test
  @DisplayName("Ingresar sin elegir identidad muestra el error en el formulario")
  void ingresoSinIdentidad() throws Exception {
    mvc.perform(post("/ingresar"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Elegí con qué perfil querés ingresar.")))
        .andExpect(content().string(containsString("role=\"alert\"")));
  }

  @Test
  @DisplayName("El panel del donante marca la sección activa y muestra su navegación")
  void panelDonante() throws Exception {
    MockHttpSession session = ingresarComo("donante-nicolas");
    mvc.perform(get("/donante").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Hola, Nicolás")))
        .andExpect(content().string(containsString("aria-current=\"page\"")))
        .andExpect(content().string(containsString("/donante/incentivos")))
        .andExpect(content().string(not(containsString("/admin/camiones"))));
  }

  @Test
  @DisplayName("Un rol no puede entrar al área de otro rol")
  void guardaDeRol() throws Exception {
    MockHttpSession session = ingresarComo("donante-nicolas");
    mvc.perform(get("/admin").session(session))
        .andExpect(redirectedUrl("/donante?aviso=sin-permiso"));
  }

  @Test
  @DisplayName("Las secciones del catálogo responden y las inexistentes dan 404")
  void secciones() throws Exception {
    MockHttpSession session = ingresarComo("admin-deposito");
    mvc.perform(get("/admin/camiones").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Camiones")));
    mvc.perform(get("/admin/no-existe").session(session)).andExpect(status().isNotFound());
    // La campana de la barra superior apunta a /<rol>/notificaciones en todos los roles.
    for (String identidad :
        new String[] {"donante-nicolas", "entidad-girasoles", "admin-deposito"}) {
      MockHttpSession s = ingresarComo(identidad);
      String inicio =
          identidad.startsWith("donante")
              ? "/donante"
              : identidad.startsWith("entidad") ? "/entidad" : "/admin";
      mvc.perform(get(inicio + "/notificaciones").session(s)).andExpect(status().isOk());
    }
  }

  @Test
  @DisplayName("Salir cierra la sesión")
  void salir() throws Exception {
    MockHttpSession session = ingresarComo("donante-nicolas");
    mvc.perform(post("/salir").session(session)).andExpect(redirectedUrl("/"));
    mvc.perform(get("/donante").session(session))
        .andExpect(redirectedUrl("/ingresar?motivo=sin-sesion"));
  }
}
