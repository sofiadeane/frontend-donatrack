package grupo5.clienteliviano.session;

import java.util.UUID;

/** Identidad elegible en el ingreso de demostración. */
public record IdentidadDemo(
    String id,
    Rol rol,
    String nombre,
    String detalle,
    UUID personaId,
    UUID donanteId,
    UUID entidadId) {

  public SesionDemo aSesion() {
    return new SesionDemo(rol, nombre, personaId, donanteId, entidadId);
  }
}
