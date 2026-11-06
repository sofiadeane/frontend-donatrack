package grupo5.clienteliviano.aplicacion;

import grupo5.clienteliviano.integracion.donaciones.DonacionesPort;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort.FiltroDonaciones;
import grupo5.clienteliviano.integracion.donaciones.dto.CambioEstadoDIResponseDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.entidades.EntidadesPort;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.DonantePerfilDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.MetricasDonanteDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosPort;
import grupo5.clienteliviano.integracion.notificaciones.NotificacionDTO;
import grupo5.clienteliviano.integracion.notificaciones.NotificacionesPort;
import grupo5.clienteliviano.session.SesionDemo;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Inicio del donante: tablero con su impacto. Combina donaciones-service (recorrido y entregas),
 * incentivos-service (totales, categoría y misión) y notificaciones-service. Cada bloque se carga
 * por separado: si un servicio falla, el resto del tablero sigue.
 */
@Service
public class PanelDonanteService {

  private static final Locale ES = Locale.forLanguageTag("es-AR");
  private static final int MESES_GRAFICO = 6;
  private static final int NOTIFICACIONES = 3;

  /** Recorrido estándar de una donación (los estados de interrupción se agregan al final). */
  private static final List<String> RECORRIDO =
      List.of(
          "EN_DEPOSITO", "ASIGNACION_REALIZADA", "LISTA_PARA_ENTREGAR", "EN_TRASLADO", "ENTREGADA");

  private static final Map<String, String> CATEGORIAS =
      Map.of(
          "COLABORADOR",
          "Colaborador",
          "SOSTENEDOR",
          "Sostenedor",
          "TRANSFORMADOR",
          "Transformador");
  private static final Map<String, String> PROXIMA =
      Map.of("COLABORADOR", "SOSTENEDOR", "SOSTENEDOR", "TRANSFORMADOR");

  // ===== Modelo de la vista =====

  /** {@code mensajeCercano}: el mismo aviso, nombrando la donación (variante /donante/cercano). */
  public record Panel(
      String mensaje,
      String mensajeCercano,
      Bloque<Recorrido> recorrido,
      Bloque<Entregas> entregas,
      Bloque<Impacto> impacto,
      Seccion<NotificacionVista> notificaciones) {}

  /**
   * Totales de donaciones-service (fuente de las cifras del hero) y, por cada estado del panal, las
   * donaciones que lo forman (se muestran al tocar una celda).
   */
  public record Entregas(
      int total,
      int entregadas,
      int enCamino,
      int enProceso,
      int interrumpidas,
      Map<String, List<Aporte>> detalle) {
    public int sinEntregar() {
      return enCamino + enProceso;
    }

    /**
     * Hasta dónde se ramificó lo que donó esta persona: categoría → subcategoría → entidad, con
     * cuántas donaciones recibió cada entidad y cuántas van en camino (raíces de la variante K).
     */
    public ArbolRaices raices() {
      List<ArbolRaices.Hoja> hojas = new ArrayList<>();
      for (String estado : List.of("llego", "camino")) {
        boolean llego = "llego".equals(estado);
        for (Aporte a : detalle.getOrDefault(estado, List.of())) {
          if (a.entidad() != null) {
            hojas.add(
                new ArbolRaices.Hoja(
                    a.categoria(), a.subcategoria(), a.entidad(), llego ? 1 : 0, llego ? 0 : 1));
          }
        }
      }
      return ArbolRaices.de(hojas, 600);
    }

    /** Cuántas entidades distintas recibieron o están por recibir algo de esta persona. */
    public long entidadesAlcanzadas() {
      return detalle.entrySet().stream()
          .filter(e -> List.of("llego", "camino").contains(e.getKey()))
          .flatMap(e -> e.getValue().stream())
          .map(Aporte::entidad)
          .filter(java.util.Objects::nonNull)
          .distinct()
          .count();
    }

    /**
     * Todas las donaciones de las celdas en orden: llegaron, en camino, en el depósito, cortadas.
     */
    public List<Lamina> laminas() {
      List<Lamina> todas = new ArrayList<>();
      for (String estado : List.of("llego", "camino", "proceso", "corte")) {
        for (Aporte a : detalle.getOrDefault(estado, List.of())) {
          todas.add(new Lamina(a, estado));
        }
      }
      return todas;
    }
  }

  /** Una donación en la huerta, con el estado de su celda (llego, camino, proceso o corte). */
  public record Lamina(Aporte aporte, String estado) {}

  /**
   * Una donación dentro de una celda del panal. {@code mensaje} es el aviso que le corresponde a su
   * estado; {@code entidad} es nula mientras no tenga destino.
   */
  public record Aporte(
      UUID id,
      String codigo,
      String descripcion,
      String detalle,
      String mensaje,
      String entidad,
      String cuando,
      String categoria,
      String subcategoria) {}

  /** Datos de incentivos-service. {@code mision} y {@code proximaCategoria} pueden ser nulos. */
  public record Impacto(
      int realizadas,
      Comparacion chip,
      List<Barra> grafico,
      String graficoDescripcion,
      double anchoGrafico,
      Integer entidadesAyudadas,
      String categoria,
      String proximaCategoria,
      Mision mision) {}

  /** "+3 respecto al mes pasado": la cifra va destacada; nula si es igual. */
  public record Comparacion(String cifra, String texto) {
    /** Este mes donó menos que el anterior (la variante cercana no lo muestra). */
    public boolean negativa() {
      return cifra != null && cifra.startsWith("−");
    }

    @Override
    public String toString() {
      return cifra == null ? texto : cifra + " " + texto;
    }
  }

  /** Barra del mini gráfico: coordenadas ya calculadas para el SVG (viewBox 0 0 ancho 84). */
  public record Barra(String mes, long valor, double x, double y, double alto, boolean actual) {}

  public record Mision(
      String nombre,
      String descripcion,
      String ascenso,
      int progreso,
      int objetivo,
      int porcentaje,
      String falta) {}

  public record Recorrido(
      UUID id, String descripcion, String detalle, EstadoDonacionVista estado, List<Paso> pasos) {}

  /** Paso del recorrido; clase: hecho, actual, siguiente o interrumpido. */
  public record Paso(int numero, String etiqueta, String cuando, String clase) {
    public boolean actual() {
      return "actual".equals(clase) || "interrumpido".equals(clase);
    }
  }

  public record NotificacionVista(String mensaje, String cuando, LocalDateTime fecha) {}

  // ===== Servicio =====

  static final double ANCHO_BARRA = 17;
  static final double SEPARACION = 10;
  static final double BASE = 82;

  private final DonacionesPort donaciones;
  private final IncentivosPort incentivos;
  private final NotificacionesPort notificaciones;
  private final EntidadesPort entidades;
  private final TraductorErrores traductor;
  private final CargadorSecciones cargador;
  private final VistaDonaciones vista;
  private final Clock reloj;

  public PanelDonanteService(
      DonacionesPort donaciones,
      IncentivosPort incentivos,
      NotificacionesPort notificaciones,
      EntidadesPort entidades,
      TraductorErrores traductor,
      CargadorSecciones cargador,
      VistaDonaciones vista,
      Clock reloj) {
    this.donaciones = donaciones;
    this.incentivos = incentivos;
    this.notificaciones = notificaciones;
    this.entidades = entidades;
    this.traductor = traductor;
    this.cargador = cargador;
    this.vista = vista;
    this.reloj = reloj;
  }

  public Panel panel(SesionDemo sesion) {
    Bloque<List<DonacionIndependienteResponseDTO>> propias = propias(sesion.donanteId());
    Bloque<Recorrido> recorrido = derivar(propias, this::recorrido);
    Bloque<Entregas> entregas = derivar(propias, this::entregas);
    return new Panel(
        mensaje(propias),
        mensajeCercano(propias),
        recorrido,
        entregas,
        impacto(sesion.donanteId()),
        notificaciones(sesion, NOTIFICACIONES));
  }

  /** Todas las notificaciones de la persona, de la más reciente a la más vieja. */
  public Seccion<NotificacionVista> notificaciones(SesionDemo sesion) {
    return notificaciones(sesion, Long.MAX_VALUE);
  }

  private Seccion<NotificacionVista> notificaciones(SesionDemo sesion, long limite) {
    return cargador.cargar(
        notificaciones.esDemo(),
        () ->
            notificaciones.dePersona(sesion.personaId()).stream()
                .sorted(
                    Comparator.comparing(
                            NotificacionDTO::fechaCreacion,
                            Comparator.nullsFirst(Comparator.<LocalDateTime>naturalOrder()))
                        .reversed())
                .limit(limite)
                .map(
                    n ->
                        new NotificacionVista(
                            n.mensaje(), hace(n.fechaCreacion()), n.fechaCreacion()))
                .toList());
  }

  private Bloque<List<DonacionIndependienteResponseDTO>> propias(UUID donanteId) {
    try {
      return Bloque.de(
          donaciones.donacionesIndependientes(FiltroDonaciones.delDonante(donanteId)).stream()
              .sorted(VistaDonaciones.MAS_RECIENTE_PRIMERO)
              .toList(),
          donaciones.esDemo());
    } catch (BackendException e) {
      return Bloque.fallido(traductor.traducir(e));
    }
  }

  private static <T> Bloque<T> derivar(
      Bloque<List<DonacionIndependienteResponseDTO>> propias,
      java.util.function.Function<List<DonacionIndependienteResponseDTO>, T> f) {
    if (propias.conError()) {
      return Bloque.fallido(propias.error());
    }
    return new Bloque<>(f.apply(propias.datos()), null, propias.demo());
  }

  // ----- Hero -----

  private static String mensaje(Bloque<List<DonacionIndependienteResponseDTO>> propias) {
    if (!propias.ok()) {
      return "Seguí el recorrido de tus donaciones hasta que llegan a quienes las necesitan.";
    }
    if (propias.datos().isEmpty()) {
      return "Todavía no registraste donaciones. Cuando lleves una al depósito, vas a poder"
          + " seguirla desde acá.";
    }
    String estado = propias.datos().getFirst().estadoActual();
    return switch (estado) {
      case "EN_DEPOSITO" -> "Tu última donación ya está en el depósito, esperando su asignación.";
      case "ASIGNACION_REALIZADA" ->
          "Tu última donación ya fue asignada a una entidad beneficiaria.";
      case "LISTA_PARA_ENTREGAR" -> "Tu última donación está lista para salir hacia su entidad.";
      case "EN_TRASLADO" -> "Tu última donación ya está en camino a su entidad beneficiaria.";
      case "ENTREGADA" -> "Tu última donación llegó a destino. ¡Gracias por sumarte!";
      case "ENTREGA_FALLIDA" ->
          "Hubo un problema con la entrega de tu última donación. Mirá el detalle.";
      case "VENCIDA" -> "Tu última donación venció antes de poder entregarse.";
      default -> "Seguí el recorrido de tus donaciones hasta que llegan a quienes las necesitan.";
    };
  }

  /** Como {@link #mensaje}, pero nombra la donación y dice qué viene después. */
  private static String mensajeCercano(Bloque<List<DonacionIndependienteResponseDTO>> propias) {
    if (!propias.ok() || propias.datos().isEmpty()) {
      return mensaje(propias);
    }
    DonacionIndependienteResponseDTO d = propias.datos().getFirst();
    String que = "Tu donación de " + minusculaInicial(d.descripcion());
    return switch (d.estadoActual()) {
      case "EN_DEPOSITO" ->
          que + " ya llegó al depósito. Te avisamos apenas sepamos a quién va a ayudar.";
      case "ASIGNACION_REALIZADA" -> que + " ya tiene destino: una organización la está esperando.";
      case "LISTA_PARA_ENTREGAR" -> que + " está lista para salir. Pronto va a estar en camino.";
      case "EN_TRASLADO" -> que + " está en camino. Te avisamos cuando la reciban.";
      case "ENTREGADA" -> que + " llegó y ya la recibieron. Gracias por estar.";
      case "ENTREGA_FALLIDA" ->
          "No pudimos entregar tu donación de "
              + minusculaInicial(d.descripcion())
              + ". Volvió al depósito; en el detalle te contamos qué pasó.";
      case "VENCIDA" ->
          que + " venció antes de poder entregarse. En el detalle te contamos qué pasó.";
      default -> mensaje(propias);
    };
  }

  private static String minusculaInicial(String texto) {
    if (texto == null || texto.isEmpty()) {
      return "";
    }
    return texto.substring(0, 1).toLowerCase(ES) + texto.substring(1);
  }

  // ----- Entregas (tile B) -----

  /** Categoría ({@code true}) o subcategoría del primer bien de la donación; nula si no la trae. */
  private static String primerBien(DonacionIndependienteResponseDTO d, boolean categoria) {
    if (d.items() == null) {
      return null;
    }
    return d.items().stream()
        .map(i -> i.bien())
        .filter(java.util.Objects::nonNull)
        .map(
            b ->
                categoria
                    ? (b.categoria() == null ? null : b.categoria().nombre())
                    : (b.subcategoria() == null ? null : b.subcategoria().nombre()))
        .filter(java.util.Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private Entregas entregas(List<DonacionIndependienteResponseDTO> lista) {
    Map<String, String> nombres = new HashMap<>();
    Map<UUID, UUID> destino = new HashMap<>();
    if (!lista.isEmpty()) {
      entidades.entidades().forEach(e -> nombres.put(e.id().toString(), e.nombre()));
      entidades.asignaciones().forEach(a -> destino.put(a.donacionId(), a.entidadId()));
    }
    Map<String, List<Aporte>> detalle = new LinkedHashMap<>();
    for (String k : List.of("llego", "camino", "proceso", "corte")) {
      detalle.put(k, new ArrayList<>());
    }
    for (DonacionIndependienteResponseDTO d : lista) {
      String celda =
          switch (EstadoDonacionVista.faseDe(d.estadoActual())) {
            case COMPLETADA -> "llego";
            case EN_CAMINO -> "camino";
            case EN_ESPERA, ASIGNADA -> "proceso";
            case INTERRUMPIDA -> "corte";
          };
      UUID entidad = destino.get(d.id());
      detalle
          .get(celda)
          .add(
              new Aporte(
                  d.id(),
                  codigo(d.id()),
                  d.descripcion(),
                  vista.resumen(d).detalle(),
                  avisoDelEstado(d.estadoActual()),
                  entidad == null ? null : nombres.get(entidad.toString()),
                  d.fechaRegistro() == null ? "" : fechaCorta(d.fechaRegistro()),
                  primerBien(d, true),
                  primerBien(d, false)));
    }
    return new Entregas(
        lista.size(),
        detalle.get("llego").size(),
        detalle.get("camino").size(),
        detalle.get("proceso").size(),
        detalle.get("corte").size(),
        detalle);
  }

  /** "DT" y los últimos cuatro caracteres del id: alcanza para reconocer la donación. */
  private static String codigo(UUID id) {
    String s = id.toString();
    return "DT" + s.substring(s.length() - 4).toUpperCase(ES);
  }

  /** El aviso de la donación según dónde está, con el mismo tono que el del encabezado. */
  private static String avisoDelEstado(String estado) {
    return switch (estado) {
      case "EN_DEPOSITO" -> "Ya está en el depósito, esperando su asignación.";
      case "ASIGNACION_REALIZADA" -> "Ya tiene destino: una organización la espera.";
      case "LISTA_PARA_ENTREGAR" -> "Está lista para salir hacia su entidad.";
      case "EN_TRASLADO" -> "¡Tu donación está en camino!";
      case "ENTREGADA" -> "Llegó a destino. ¡Gracias por sumarte!";
      case "ENTREGA_FALLIDA" -> "No pudimos entregarla y volvió al depósito.";
      case "VENCIDA" -> "Venció antes de poder entregarse.";
      default -> "Seguimos de cerca esta donación.";
    };
  }

  // ----- Recorrido de la última donación -----

  private Recorrido recorrido(List<DonacionIndependienteResponseDTO> lista) {
    if (lista.isEmpty()) {
      return null;
    }
    DonacionIndependienteResponseDTO d = lista.getFirst();
    String actual = d.estadoActual();
    int indice = RECORRIDO.indexOf(actual);
    List<Paso> pasos = new ArrayList<>();
    if (indice >= 0) {
      for (int i = 0; i < RECORRIDO.size(); i++) {
        String clase = i < indice ? "hecho" : i == indice ? "actual" : "siguiente";
        pasos.add(paso(d, i + 1, RECORRIDO.get(i), clase));
      }
    } else {
      // Interrumpida: los pasos alcanzados y, al final, el estado de interrupción.
      int alcanzado =
          d.historial().stream()
              .map(c -> RECORRIDO.indexOf(c.estadoNuevo()))
              .max(Integer::compare)
              .orElse(-1);
      for (int i = 0; i <= alcanzado; i++) {
        pasos.add(paso(d, i + 1, RECORRIDO.get(i), "hecho"));
      }
      pasos.add(paso(d, alcanzado + 2, actual, "interrumpido"));
    }
    return new Recorrido(
        d.id(), d.descripcion(), vista.resumen(d).detalle(), vista.estado(actual), pasos);
  }

  private Paso paso(DonacionIndependienteResponseDTO d, int numero, String estado, String clase) {
    Optional<LocalDateTime> fecha =
        d.historial().stream()
            .filter(c -> estado.equals(c.estadoNuevo()))
            .map(CambioEstadoDIResponseDTO::timestamp)
            .filter(Objects::nonNull)
            .max(Comparator.naturalOrder());
    String cuando =
        fecha.map(this::fechaCorta).orElse("siguiente".equals(clase) ? "pendiente" : "");
    return new Paso(numero, vista.estado(estado).etiqueta(), cuando, clase);
  }

  private String fechaCorta(LocalDateTime f) {
    LocalDate hoy = LocalDate.now(reloj);
    return f.toLocalDate().equals(hoy)
        ? "hoy " + f.format(DateTimeFormatter.ofPattern("HH:mm"))
        : f.format(DateTimeFormatter.ofPattern("dd/MM"));
  }

  // ----- Incentivos (tile A, categoría y misión) -----

  private Bloque<Impacto> impacto(UUID donanteId) {
    try {
      Optional<DonantePerfilDTO> perfil = incentivos.perfil(donanteId);
      Optional<MetricasDonanteDTO> metricas = incentivos.metricas(donanteId);
      if (perfil.isEmpty() || metricas.isEmpty()) {
        return new Bloque<>(null, null, incentivos.esDemo());
      }
      return Bloque.de(impacto(perfil.get(), metricas.get()), incentivos.esDemo());
    } catch (BackendException e) {
      return Bloque.fallido(traductor.traducir(e));
    }
  }

  private Impacto impacto(DonantePerfilDTO perfil, MetricasDonanteDTO m) {
    String categoria = CATEGORIAS.getOrDefault(perfil.categoria(), perfil.categoria());
    String proxima =
        Optional.ofNullable(PROXIMA.get(perfil.categoria())).map(CATEGORIAS::get).orElse(null);
    List<Barra> barras = grafico(m.donacionesPorPeriodo());
    String descripcion =
        "Donaciones por mes: "
            + String.join(", ", barras.stream().map(b -> b.mes() + " " + b.valor()).toList());
    Mision mision =
        m.misionActiva() == null
            ? null
            : new Mision(
                m.misionActiva().nombre(),
                m.misionActiva().descripcion(),
                proxima == null ? categoria : categoria + " → " + proxima,
                m.misionActiva().progresoActual(),
                m.misionActiva().objetivo(),
                Math.max(0, Math.min(100, m.misionActiva().porcentaje())),
                falta(m.misionActiva().distanciaAlObjetivo()));
    Comparacion chip = comparacion(m.donacionesMesActual() - m.donacionesMesAnterior());
    int realizadas = m.totalDonacionesHistoricas() == null ? 0 : m.totalDonacionesHistoricas();
    return new Impacto(
        realizadas,
        chip,
        barras,
        descripcion,
        anchoGrafico(),
        m.totalOrganizacionesAyudadas(),
        categoria,
        proxima,
        mision);
  }

  /** Diferencia de donaciones de este mes contra el anterior. */
  public static Comparacion comparacion(long diferencia) {
    if (diferencia == 0) {
      return new Comparacion(null, "¡Igual que el mes pasado!");
    }
    return new Comparacion(
        (diferencia > 0 ? "+" : "−") + Math.abs(diferencia), "respecto al mes pasado");
  }

  private static String falta(int distancia) {
    if (distancia <= 0) {
      return "¡objetivo cumplido!";
    }
    return distancia == 1 ? "falta 1 paso" : "faltan " + distancia + " pasos";
  }

  /** Últimos 6 meses hasta el actual (los meses sin dato valen 0). */
  private List<Barra> grafico(Map<String, Long> porPeriodo) {
    Map<String, Long> datos = porPeriodo == null ? Map.of() : porPeriodo;
    YearMonth actual = YearMonth.now(reloj);
    List<YearMonth> meses = new ArrayList<>();
    for (int i = MESES_GRAFICO - 1; i >= 0; i--) {
      meses.add(actual.minusMonths(i));
    }
    long max =
        Math.max(
            1,
            meses.stream()
                .mapToLong(mes -> datos.getOrDefault(mes.toString(), 0L))
                .max()
                .orElse(1));
    List<Barra> barras = new ArrayList<>();
    for (int i = 0; i < meses.size(); i++) {
      YearMonth mes = meses.get(i);
      long valor = datos.getOrDefault(mes.toString(), 0L);
      double alto = Math.max(6, (double) valor / max * (BASE - 8));
      barras.add(
          new Barra(
              mes.getMonth().getDisplayName(TextStyle.FULL, ES),
              valor,
              i * (ANCHO_BARRA + SEPARACION),
              BASE - alto,
              alto,
              mes.equals(actual)));
    }
    return barras;
  }

  public static double anchoGrafico() {
    return MESES_GRAFICO * ANCHO_BARRA + (MESES_GRAFICO - 1) * SEPARACION;
  }

  // ----- Notificaciones -----

  private String hace(LocalDateTime fecha) {
    if (fecha == null) {
      return "";
    }
    LocalDateTime ahora = LocalDateTime.now(reloj);
    Duration d = Duration.between(fecha, ahora);
    if (d.isNegative() || d.toMinutes() < 1) {
      return "recién";
    }
    if (d.toMinutes() < 60) {
      return "hace " + d.toMinutes() + " min";
    }
    if (d.toHours() < 24 && fecha.toLocalDate().equals(ahora.toLocalDate())) {
      return "hace " + d.toHours() + " h";
    }
    long dias =
        Duration.between(fecha.toLocalDate().atStartOfDay(), ahora.toLocalDate().atStartOfDay())
            .toDays();
    if (dias == 1) {
      return "ayer";
    }
    if (dias < 7) {
      return "hace " + dias + " días";
    }
    return fecha.format(DateTimeFormatter.ofPattern("d MMM", ES));
  }
}
