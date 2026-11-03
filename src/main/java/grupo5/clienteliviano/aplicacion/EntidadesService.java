package grupo5.clienteliviano.aplicacion;

import grupo5.clienteliviano.aplicacion.VistaDonaciones.DonacionResumen;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort.FiltroDonaciones;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.entidades.EntidadesPort;
import grupo5.clienteliviano.integracion.entidades.EntidadesPort.Asignacion;
import grupo5.clienteliviano.integracion.entidades.EntidadesPort.Entidad;
import grupo5.clienteliviano.session.SesionDemo;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * "Entidades" del donante: el hormiguero con una cámara por cada entidad a la que donó (en el orden
 * en que llegó a cada una) y la tabla filtrable con el resto. El destino de cada donación sale de
 * las asignaciones de demostración (brecha G3).
 */
@Service
public class EntidadesService {

  /** Una cámara del hormiguero: la entidad y las donaciones que le llegaron de este donante. */
  public record Camara(Entidad entidad, List<DonacionResumen> donaciones, long entregadas) {
    public int cantidad() {
      return donaciones.size();
    }
  }

  /** Filtros de la tabla, por GET; cualquiera puede ser nulo. */
  public record Filtros(String q, String tipo, String categoria, String localidad) {
    public boolean activos() {
      return q != null || tipo != null || categoria != null || localidad != null;
    }
  }

  /**
   * @param seleccionada la cámara abierta: la pedida por {@code ?entidad=}, o la más reciente
   * @param otras entidades a las que todavía no donó, filtradas
   */
  public record Pagina(
      Seccion<Camara> hormiguero,
      Camara seleccionada,
      List<Entidad> otras,
      int totalOtras,
      List<String> tipos,
      List<String> categorias,
      List<String> localidades,
      Filtros filtros,
      boolean demo) {}

  private final DonacionesPort donaciones;
  private final EntidadesPort entidades;
  private final CargadorSecciones cargador;
  private final VistaDonaciones vista;

  public EntidadesService(
      DonacionesPort donaciones,
      EntidadesPort entidades,
      CargadorSecciones cargador,
      VistaDonaciones vista) {
    this.donaciones = donaciones;
    this.entidades = entidades;
    this.cargador = cargador;
    this.vista = vista;
  }

  public Pagina pagina(SesionDemo sesion, Filtros filtros, UUID seleccion) {
    Seccion<Camara> hormiguero =
        cargador.cargar(donaciones.esDemo() || entidades.esDemo(), () -> camaras(sesion));
    Camara seleccionada =
        hormiguero.datos().stream()
            .filter(c -> c.entidad().id().equals(seleccion))
            .findFirst()
            .orElse(hormiguero.datos().isEmpty() ? null : hormiguero.datos().getLast());
    Set<UUID> conDonaciones =
        hormiguero.datos().stream().map(c -> c.entidad().id()).collect(Collectors.toSet());
    List<Entidad> resto =
        entidades.entidades().stream().filter(e -> !conDonaciones.contains(e.id())).toList();
    List<Entidad> otras =
        resto.stream()
            .filter(
                e ->
                    filtros.q() == null || normalizar(e.nombre()).contains(normalizar(filtros.q())))
            .filter(e -> filtros.tipo() == null || e.tipo().equals(filtros.tipo()))
            .filter(
                e -> filtros.categoria() == null || e.categorias().contains(filtros.categoria()))
            .filter(e -> filtros.localidad() == null || e.localidad().equals(filtros.localidad()))
            .sorted(
                Comparator.comparing(Entidad::necesidadUrgente)
                    .reversed()
                    .thenComparing(Entidad::nombre))
            .toList();
    return new Pagina(
        hormiguero,
        seleccionada,
        otras,
        resto.size(),
        distintos(resto, e -> List.of(e.tipo())),
        distintos(resto, Entidad::categorias),
        distintos(resto, e -> List.of(e.localidad())),
        filtros,
        entidades.esDemo());
  }

  /** Una cámara por entidad, de la primera a la que donó a la más reciente. */
  private List<Camara> camaras(SesionDemo sesion) {
    Map<UUID, Entidad> porId =
        entidades.entidades().stream().collect(Collectors.toMap(Entidad::id, Function.identity()));
    Map<UUID, UUID> destino =
        entidades.asignaciones().stream()
            .collect(Collectors.toMap(Asignacion::donacionId, Asignacion::entidadId));
    Map<UUID, List<DonacionIndependienteResponseDTO>> porEntidad = new LinkedHashMap<>();
    donaciones.donacionesIndependientes(FiltroDonaciones.delDonante(sesion.donanteId())).stream()
        .filter(d -> porId.containsKey(destino.get(d.id())))
        .sorted(
            Comparator.comparing(
                DonacionIndependienteResponseDTO::fechaRegistro,
                Comparator.nullsLast(Comparator.<LocalDateTime>naturalOrder())))
        .forEach(
            d -> porEntidad.computeIfAbsent(destino.get(d.id()), k -> new ArrayList<>()).add(d));
    return porEntidad.entrySet().stream()
        .map(
            e ->
                new Camara(
                    porId.get(e.getKey()),
                    e.getValue().stream()
                        .sorted(VistaDonaciones.MAS_RECIENTE_PRIMERO)
                        .map(vista::resumen)
                        .toList(),
                    e.getValue().stream()
                        .filter(d -> "ENTREGADA".equals(d.estadoActual()))
                        .count()))
        .toList();
  }

  private static List<String> distintos(
      List<Entidad> lista, Function<Entidad, List<String>> campo) {
    return lista.stream()
        .flatMap(e -> campo.apply(e).stream())
        .filter(Objects::nonNull)
        .distinct()
        .sorted()
        .toList();
  }

  private static String normalizar(String s) {
    return Normalizer.normalize(s, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase(Locale.ROOT)
        .strip();
  }
}
