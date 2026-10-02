package grupo5.clienteliviano.aplicacion;

import grupo5.clienteliviano.integracion.donaciones.DonacionesPort;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort.FiltroDonaciones;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.ItemDonacionIndependienteResponseDTO;
import grupo5.clienteliviano.session.SesionDemo;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/** Casos de uso del inicio del donante. */
@Service
public class DonanteInicioService {

  private static final int ULTIMAS = 3;
  private static final Locale ES = Locale.forLanguageTag("es-AR");

  /** Fila de "Tus últimas donaciones". */
  public record DonacionResumen(
      String descripcion,
      String detalle,
      EstadoDonacionVista estado,
      LocalDateTime fechaRegistro) {}

  private final DonacionesPort donaciones;
  private final CargadorSecciones cargador;
  private final MessageSource mensajes;

  public DonanteInicioService(
      DonacionesPort donaciones, CargadorSecciones cargador, MessageSource mensajes) {
    this.donaciones = donaciones;
    this.cargador = cargador;
    this.mensajes = mensajes;
  }

  public Seccion<DonacionResumen> ultimasDonaciones(SesionDemo sesion) {
    return cargador.cargar(
        donaciones.esDemo(),
        () ->
            donaciones
                .donacionesIndependientes(FiltroDonaciones.delDonante(sesion.donanteId()))
                .stream()
                .sorted(
                    Comparator.comparing(
                            DonacionIndependienteResponseDTO::fechaRegistro,
                            Comparator.nullsFirst(Comparator.naturalOrder()))
                        .reversed())
                .limit(ULTIMAS)
                .map(this::resumen)
                .toList());
  }

  private DonacionResumen resumen(DonacionIndependienteResponseDTO d) {
    return new DonacionResumen(
        d.descripcion(), detalle(d), estado(d.estadoActual()), d.fechaRegistro());
  }

  private String detalle(DonacionIndependienteResponseDTO d) {
    Optional<ItemDonacionIndependienteResponseDTO> item = d.items().stream().findFirst();
    String subcategoria =
        item.map(i -> i.bien().subcategoria().nombre()).orElse("Sin subcategoría");
    String unidad =
        item.map(i -> i.bien().categoria().unidad())
            .map(u -> mensajes.getMessage("unidad." + u, null, u, ES))
            .orElse("");
    return subcategoria + " · " + d.cantidad() + (unidad.isEmpty() ? "" : " " + unidad);
  }

  private EstadoDonacionVista estado(String codigo) {
    String etiqueta = mensajes.getMessage("estado.donacion." + codigo, null, codigo, ES);
    return new EstadoDonacionVista(codigo, etiqueta, EstadoDonacionVista.faseDe(codigo));
  }
}
