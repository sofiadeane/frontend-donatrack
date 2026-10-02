package grupo5.clienteliviano.session;

import java.io.Serializable;
import java.util.UUID;

/**
 * Identidad de la sesión de demostración. Guarda los tres identificadores que usa el backend
 * (persona, donante, entidad) porque cada servicio indexa por uno distinto (spec §6 B5).
 */
public record SesionDemo(Rol rol, String nombre, UUID personaId, UUID donanteId, UUID entidadId)
    implements Serializable {

  /** Valor enviado como {@code X-Actor} / {@code actor} en operaciones que lo requieren. */
  public String actor() {
    return rol.name().toLowerCase() + ":" + nombre;
  }

  public String iniciales() {
    String[] partes = nombre.trim().split("\\s+");
    String primera = partes[0].substring(0, 1);
    String segunda = partes.length > 1 ? partes[partes.length - 1].substring(0, 1) : "";
    return (primera + segunda).toUpperCase();
  }

  public String primerNombre() {
    return nombre.trim().split("\\s+")[0];
  }
}
