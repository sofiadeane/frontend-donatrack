package grupo5.clienteliviano.aplicacion.registro;

import java.util.Arrays;
import java.util.Optional;

/** Las tres formas de registrarse; el parámetro es el que usan los CTA ({@code ?tipo=}). */
public enum TipoCuenta {
  HUMANA("humana", "persona donante"),
  JURIDICA("juridica", "organización donante"),
  ENTIDAD("entidad", "entidad beneficiaria");

  private final String parametro;
  private final String descripcion;

  TipoCuenta(String parametro, String descripcion) {
    this.parametro = parametro;
    this.descripcion = descripcion;
  }

  public String parametro() {
    return parametro;
  }

  public String descripcion() {
    return descripcion;
  }

  public boolean esOrganizacion() {
    return this != HUMANA;
  }

  public static Optional<TipoCuenta> desde(String parametro) {
    return Arrays.stream(values()).filter(t -> t.parametro.equals(parametro)).findFirst();
  }
}
