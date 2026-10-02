package grupo5.clienteliviano.aplicacion.registro;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Formato de correo sin expresión regular (Sonar: evitar backtracking super-lineal). */
class ValidadorRegistroTest {

  @ParameterizedTest
  @ValueSource(strings = {"lucia@ejemplo.org", " a.b@sub.dominio.com.ar ", "x@y.io"})
  @DisplayName("Acepta correos con usuario, una arroba y dominio con punto")
  void validos(String correo) {
    assertThat(ValidadorRegistro.esCorreo(correo)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"lucia@ejemplo", "@ejemplo.org", "a@@b.org", "a b@c.org", "a@.org", "a@org.", ""})
  @DisplayName("Rechaza correos mal formados")
  void invalidos(String correo) {
    assertThat(ValidadorRegistro.esCorreo(correo)).isFalse();
  }

  @Test
  @DisplayName("Una entrada larga y maliciosa se evalúa en tiempo lineal")
  void lineal() {
    String largo = "a".repeat(50_000) + "@" + "b".repeat(50_000);
    assertThat(ValidadorRegistro.esCorreo(largo)).isFalse();
  }
}
