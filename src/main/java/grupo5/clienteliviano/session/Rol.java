package grupo5.clienteliviano.session;

/** Roles de la sesión de demostración. Cada rol tiene un área privada con su propio prefijo. */
public enum Rol {
  DONANTE("/donante", "Persona donante"),
  ENTIDAD("/entidad", "Entidad beneficiaria"),
  ADMIN("/admin", "Administración");

  private final String inicio;
  private final String etiqueta;

  Rol(String inicio, String etiqueta) {
    this.inicio = inicio;
    this.etiqueta = etiqueta;
  }

  public String inicio() {
    return inicio;
  }

  public String etiqueta() {
    return etiqueta;
  }

  /** Rol dueño de una ruta privada, o {@code null} si la ruta es pública. */
  public static Rol duenioDe(String ruta) {
    for (Rol rol : values()) {
      if (ruta.equals(rol.inicio) || ruta.startsWith(rol.inicio + "/")) {
        return rol;
      }
    }
    return null;
  }
}
