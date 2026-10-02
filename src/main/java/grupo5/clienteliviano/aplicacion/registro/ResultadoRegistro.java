package grupo5.clienteliviano.aplicacion.registro;

import grupo5.clienteliviano.aplicacion.Seccion.ErrorVista;
import java.io.Serializable;
import java.util.UUID;

/** Cómo terminó un intento de registro. */
public sealed interface ResultadoRegistro {

  /** Persona y perfil (donante o entidad) creados. */
  record Exito(String nombre, TipoCuenta tipo, boolean demo) implements ResultadoRegistro {}

  /** Datos rechazados (por el cliente o por el backend); el formulario se vuelve a mostrar. */
  record Invalido(ErroresFormulario errores) implements ResultadoRegistro {}

  /** No se pudo crear la persona: no quedó nada guardado. */
  record Fallido(ErrorVista error) implements ResultadoRegistro {}

  /**
   * La persona se creó pero falló el alta de donante o entidad (CA3): se puede reintentar solo ese
   * paso sin volver a cargar el formulario.
   */
  record Parcial(Pendiente pendiente, ErrorVista error) implements ResultadoRegistro {}

  /** Lo necesario para reintentar el segundo paso; se guarda en la sesión. */
  record Pendiente(UUID personaId, TipoCuenta tipo, String nombre) implements Serializable {}
}
