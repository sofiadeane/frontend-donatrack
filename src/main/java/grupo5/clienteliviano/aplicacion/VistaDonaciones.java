package grupo5.clienteliviano.aplicacion;

import grupo5.clienteliviano.integracion.donaciones.dto.DonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.ItemDonacionIndependienteResponseDTO;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/** Textos de donaciones para las vistas (estados, unidades, resumen de una fila). */
@Component
public class VistaDonaciones {

  private static final Locale ES = Locale.forLanguageTag("es-AR");

  /** Todos los valores de {@code TipoEstadoDonacion}, en el orden del recorrido. */
  public static final List<String> ESTADOS =
      List.of(
          "EN_DEPOSITO",
          "ASIGNACION_REALIZADA",
          "LISTA_PARA_ENTREGAR",
          "EN_TRASLADO",
          "ENTREGADA",
          "ENTREGA_FALLIDA",
          "VENCIDA");

  /** La donación registrada más recientemente primero. */
  public static final Comparator<DonacionIndependienteResponseDTO> MAS_RECIENTE_PRIMERO =
      Comparator.comparing(
              DonacionIndependienteResponseDTO::fechaRegistro,
              Comparator.nullsFirst(Comparator.<LocalDateTime>naturalOrder()))
          .reversed();

  /** Fila de una lista de donaciones. */
  public record DonacionResumen(
      UUID id,
      String descripcion,
      String detalle,
      EstadoDonacionVista estado,
      LocalDateTime fechaRegistro) {}

  private final MessageSource mensajes;

  public VistaDonaciones(MessageSource mensajes) {
    this.mensajes = mensajes;
  }

  public DonacionResumen resumen(DonacionIndependienteResponseDTO d) {
    return new DonacionResumen(
        d.id(), d.descripcion(), detalle(d), estado(d.estadoActual()), d.fechaRegistro());
  }

  public EstadoDonacionVista estado(String codigo) {
    String etiqueta = texto("estado.donacion." + codigo, codigo);
    return new EstadoDonacionVista(codigo, etiqueta, EstadoDonacionVista.faseDe(codigo));
  }

  public String unidad(String codigo) {
    return codigo == null ? "" : texto("unidad." + codigo, codigo);
  }

  public String estadoBien(String codigo) {
    return codigo == null ? null : texto("bien.estado." + codigo, codigo);
  }

  /** "Subcategoría · cantidad unidad" del primer ítem. */
  private String detalle(DonacionIndependienteResponseDTO d) {
    Optional<ItemDonacionIndependienteResponseDTO> item = d.items().stream().findFirst();
    String subcategoria =
        item.map(i -> i.bien().subcategoria().nombre()).orElse("Sin subcategoría");
    String unidad = item.map(i -> unidad(i.bien().categoria().unidad())).orElse("");
    return subcategoria + " · " + d.cantidad() + (unidad.isEmpty() ? "" : " " + unidad);
  }

  private String texto(String clave, String porDefecto) {
    return mensajes.getMessage(clave, null, porDefecto, ES);
  }
}
