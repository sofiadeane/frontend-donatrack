package grupo5.clienteliviano.aplicacion;

import grupo5.clienteliviano.integracion.donaciones.DonacionesPort;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort.FiltroDonaciones;
import grupo5.clienteliviano.integracion.donaciones.dto.BienNormalizadoDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.CambioEstadoDIResponseDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionOutputDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionOutputDTO.PersonaResumenDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.ItemDonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.metricas.MetricasPublicasPort;
import grupo5.clienteliviano.integracion.metricas.MetricasPublicasPort.Metrica;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Casos de uso de la landing pública (R1). */
@Service
public class LandingService {

  private static final Logger log = LoggerFactory.getLogger(LandingService.class);
  private static final int MAX_DESTACADAS = 8;
  private static final String ENTREGADA = "ENTREGADA";

  /**
   * Tarjeta de "Donaciones destacadas del último mes".
   *
   * @param donante nombre público del donante (estrategia de incentivo); nulo si no se pudo obtener
   */
  public record DonacionDestacada(
      String titulo,
      String categoria,
      String iconoCategoria,
      String donante,
      LocalDateTime entregadaEl,
      String fotoUrl) {}

  /** Métricas con su origen, para mostrar la etiqueta de demostración. */
  public record Metricas(List<Metrica> valores, boolean demo) {}

  private final DonacionesPort donaciones;
  private final MetricasPublicasPort metricas;
  private final CargadorSecciones cargador;
  private final Clock reloj;

  public LandingService(
      DonacionesPort donaciones,
      MetricasPublicasPort metricas,
      CargadorSecciones cargador,
      Clock reloj) {
    this.donaciones = donaciones;
    this.metricas = metricas;
    this.cargador = cargador;
    this.reloj = reloj;
  }

  /**
   * Donaciones entregadas en los últimos 30 días, la más reciente primero. El criterio de
   * "destacadas" no está definido en el enunciado (spec §6 B3): se usan las más recientes.
   */
  public Seccion<DonacionDestacada> destacadasDelUltimoMes() {
    LocalDateTime desde = LocalDateTime.now(reloj).minusDays(30);
    return cargador.cargar(
        donaciones.esDemo(),
        () ->
            donaciones
                .donacionesIndependientes(new FiltroDonaciones(null, ENTREGADA, null))
                .stream()
                .flatMap(d -> fechaEntrega(d).map(f -> new Entrega(d, f)).stream())
                .filter(e -> !e.fecha().isBefore(desde))
                .sorted(Comparator.comparing(Entrega::fecha).reversed())
                .limit(MAX_DESTACADAS)
                .map(e -> destacada(e.donacion(), e.fecha()))
                .toList());
  }

  private record Entrega(DonacionIndependienteResponseDTO donacion, LocalDateTime fecha) {}

  public Metricas metricas() {
    return new Metricas(metricas.metricas(), metricas.esDemo());
  }

  private static Optional<LocalDateTime> fechaEntrega(DonacionIndependienteResponseDTO d) {
    return d.historial().stream()
        .filter(c -> ENTREGADA.equals(c.estadoNuevo()))
        .map(CambioEstadoDIResponseDTO::timestamp)
        .filter(Objects::nonNull)
        .max(Comparator.naturalOrder());
  }

  private DonacionDestacada destacada(DonacionIndependienteResponseDTO d, LocalDateTime f) {
    Optional<BienNormalizadoDTO> bien =
        d.items().stream()
            .map(ItemDonacionIndependienteResponseDTO::bien)
            .filter(Objects::nonNull)
            .findFirst();
    String categoria = bien.map(b -> b.categoria().nombre()).orElse("Donación");
    String foto = bien.map(b -> b.bien() != null ? b.bien().fotoUrl() : null).orElse(null);
    return new DonacionDestacada(
        d.descripcion(), categoria, IconoCategoria.de(categoria), donante(d), f, foto);
  }

  /**
   * Nombre público del donante, desde la donación original ({@code GET /api/donaciones/{id}}).
   * Persona humana: nombre e inicial del apellido (minimiza datos personales). Persona jurídica:
   * razón social. Si la consulta falla, la tarjeta se muestra sin donante.
   */
  private String donante(DonacionIndependienteResponseDTO d) {
    if (d.donacionOriginalId() == null) {
      return null;
    }
    try {
      return donaciones
          .donacion(d.donacionOriginalId())
          .map(DonacionOutputDTO::donante)
          .map(DonacionOutputDTO.DonanteResumenDTO::persona)
          .map(LandingService::nombrePublico)
          .orElse(null);
    } catch (BackendException e) {
      log.warn("No se pudo obtener el donante de {}: {}", d.donacionOriginalId(), e.getMessage());
      return null;
    }
  }

  public static String nombrePublico(PersonaResumenDTO p) {
    if ("JURIDICA".equals(p.tipo())) {
      return p.razonSocial();
    }
    if (p.nombre() == null) {
      return null;
    }
    String inicial =
        p.apellido() == null || p.apellido().isBlank() ? "" : " " + p.apellido().charAt(0) + ".";
    return p.nombre() + inicial;
  }
}
