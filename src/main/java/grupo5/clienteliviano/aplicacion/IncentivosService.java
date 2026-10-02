package grupo5.clienteliviano.aplicacion;

import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.CambioCategoriaDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.DonantePerfilDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.MetricasDonanteDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosDTOs.MisionDTO;
import grupo5.clienteliviano.integracion.incentivos.IncentivosPort;
import grupo5.clienteliviano.session.SesionDemo;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Incentivos del donante (H2.4, J3): categoría y su recorrido, misiones con progreso e insignias
 * ganadas y pendientes. Las insignias salen de las misiones (incentivos-service no tiene un
 * endpoint de pendientes): la de una misión completada está ganada; la de una no completada es su
 * vista previa.
 */
@Service
public class IncentivosService {

  /** Orden de las categorías (enum {@code CategoriaDonante}). */
  static final List<String> CATEGORIAS = List.of("COLABORADOR", "SOSTENEDOR", "TRANSFORMADOR");

  private static final Map<String, String> ETIQUETAS =
      Map.of(
          "COLABORADOR", "Colaborador",
          "SOSTENEDOR", "Sostenedor",
          "TRANSFORMADOR", "Transformador");

  public record Pagina(
      String categoria,
      List<PasoCategoria> recorrido,
      int misionesCompletadas,
      int insigniasGanadas,
      Integer posicionEnRanking,
      MisionVista activa,
      List<GrupoMisiones> misiones,
      List<InsigniaVista> insignias) {}

  /** Nodo del recorrido de categorías; estado: alcanzada, actual o siguiente. */
  public record PasoCategoria(String etiqueta, String estado, LocalDate desde) {}

  public record GrupoMisiones(String categoria, String estado, List<MisionVista> misiones) {}

  public record MisionVista(
      int numero,
      String nombre,
      String descripcion,
      int progreso,
      int objetivo,
      int porcentaje,
      String falta,
      boolean completada,
      LocalDate fechaCompletada,
      String insignia) {}

  public record InsigniaVista(
      String nombre,
      String descripcion,
      String imagenUrl,
      boolean ganada,
      LocalDate fechaObtenida,
      String mision) {}

  private final IncentivosPort incentivos;
  private final TraductorErrores traductor;

  public IncentivosService(IncentivosPort incentivos, TraductorErrores traductor) {
    this.incentivos = incentivos;
    this.traductor = traductor;
  }

  /** Vacío (no error) si el donante todavía no existe en incentivos (CA3). */
  public Bloque<Pagina> pagina(SesionDemo sesion) {
    try {
      Optional<DonantePerfilDTO> perfil = incentivos.perfil(sesion.donanteId());
      if (perfil.isEmpty()) {
        return new Bloque<>(null, null, incentivos.esDemo());
      }
      Optional<MetricasDonanteDTO> metricas = incentivos.metricas(sesion.donanteId());
      List<MisionDTO> misiones = incentivos.misiones(sesion.donanteId());
      List<CambioCategoriaDTO> ascensos = incentivos.ascensos(sesion.donanteId());
      return Bloque.de(
          armar(perfil.get(), metricas.orElse(null), misiones, ascensos), incentivos.esDemo());
    } catch (BackendException e) {
      return Bloque.fallido(traductor.traducir(e));
    }
  }

  private static Pagina armar(
      DonantePerfilDTO perfil,
      MetricasDonanteDTO metricas,
      List<MisionDTO> misiones,
      List<CambioCategoriaDTO> ascensos) {
    String actual = perfil.categoria();
    int indiceActual = Math.max(0, CATEGORIAS.indexOf(actual));

    List<PasoCategoria> recorrido =
        java.util.stream.IntStream.range(0, CATEGORIAS.size())
            .mapToObj(
                i -> {
                  String c = CATEGORIAS.get(i);
                  String estado =
                      i < indiceActual ? "alcanzada" : i == indiceActual ? "actual" : "siguiente";
                  LocalDate desde =
                      i == 0
                          ? perfil.fechaRegistro()
                          : ascensos.stream()
                              .filter(a -> c.equals(a.categoriaNueva()))
                              .map(CambioCategoriaDTO::fecha)
                              .findFirst()
                              .orElse(null);
                  return new PasoCategoria(etiqueta(c), estado, i <= indiceActual ? desde : null);
                })
            .toList();

    List<MisionVista> vistas =
        java.util.stream.IntStream.range(0, misiones.size())
            .mapToObj(i -> vista(i + 1, misiones.get(i)))
            .toList();
    List<GrupoMisiones> grupos =
        CATEGORIAS.stream()
            .map(
                c -> {
                  int i = CATEGORIAS.indexOf(c);
                  List<MisionVista> deCategoria =
                      java.util.stream.IntStream.range(0, misiones.size())
                          .filter(k -> c.equals(misiones.get(k).categoria()))
                          .mapToObj(vistas::get)
                          .toList();
                  String estado =
                      i < indiceActual ? "alcanzada" : i == indiceActual ? "actual" : "siguiente";
                  return new GrupoMisiones(etiqueta(c), estado, deCategoria);
                })
            .filter(g -> !g.misiones().isEmpty())
            .toList();

    List<InsigniaVista> insignias =
        misiones.stream()
            .filter(m -> m.insignia() != null)
            .map(
                m ->
                    new InsigniaVista(
                        m.insignia().nombre(),
                        m.insignia().descripcion(),
                        m.insignia().imagenUrl(),
                        m.completada(),
                        m.completada() ? m.insignia().fechaObtenida() : null,
                        m.nombre()))
            .sorted((a, b) -> Boolean.compare(b.ganada(), a.ganada()))
            .toList();

    MisionVista activa =
        metricas == null || metricas.misionActiva() == null
            ? null
            : vistas.stream()
                .filter(v -> v.nombre().equals(metricas.misionActiva().nombre()))
                .findFirst()
                .orElse(null);

    return new Pagina(
        etiqueta(actual),
        recorrido,
        perfil.misionesCompletadas(),
        perfil.insigniasGanadas(),
        metricas == null ? null : metricas.posicionEnRanking(),
        activa,
        grupos,
        insignias);
  }

  private static MisionVista vista(int numero, MisionDTO m) {
    int pct = Math.max(0, Math.min(100, m.porcentaje()));
    String falta =
        m.completada()
            ? "Completada"
            : m.distanciaAlObjetivo() == 1
                ? "falta 1 paso"
                : "faltan " + m.distanciaAlObjetivo() + " pasos";
    return new MisionVista(
        numero,
        m.nombre(),
        m.descripcion(),
        m.progresoActual(),
        m.objetivo(),
        m.completada() ? 100 : pct,
        falta,
        m.completada(),
        m.fechaCompletada(),
        m.insignia() != null ? m.insignia().nombre() : null);
  }

  static String etiqueta(String categoria) {
    return ETIQUETAS.getOrDefault(categoria, categoria);
  }
}
