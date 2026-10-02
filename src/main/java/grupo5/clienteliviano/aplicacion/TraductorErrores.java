package grupo5.clienteliviano.aplicacion;

import grupo5.clienteliviano.aplicacion.Seccion.ErrorVista;
import grupo5.clienteliviano.integracion.error.BackendException;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * Convierte fallas del backend en mensajes comprensibles ({@code messages.properties}). Primero
 * busca un texto para el código del backend ({@code error.codigo.<CODE>}); si no existe, usa uno
 * por tipo de falla. Nunca muestra el mensaje técnico del servidor.
 */
@Component
public class TraductorErrores {

  private static final Logger log = LoggerFactory.getLogger(TraductorErrores.class);
  private static final Locale ES = Locale.forLanguageTag("es-AR");

  private final MessageSource mensajes;

  public TraductorErrores(MessageSource mensajes) {
    this.mensajes = mensajes;
  }

  public ErrorVista traducir(BackendException e) {
    log.warn("Falla de backend: {} (traceId={})", e.getMessage(), e.traceId());
    String servicio = mensajes.getMessage("servicio." + e.servicio().name(), null, ES);
    String porTipo =
        mensajes.getMessage("error.tipo." + e.tipo().name(), new Object[] {servicio}, ES);
    String texto =
        e.codigo() == null
            ? porTipo
            : mensajes.getMessage(
                "error.codigo." + e.codigo(), new Object[] {servicio}, porTipo, ES);
    return new ErrorVista(texto, e.traceId());
  }
}
