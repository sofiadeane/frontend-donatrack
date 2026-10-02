package grupo5.clienteliviano.aplicacion;

import grupo5.clienteliviano.aplicacion.Seccion.ErrorVista;
import grupo5.clienteliviano.aplicacion.VistaDonaciones.DonacionResumen;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort.FiltroDonaciones;
import grupo5.clienteliviano.integracion.donaciones.dto.BienNormalizadoDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.CambioEstadoDIResponseDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.CategoriaOutputDTO;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionIndependienteResponseDTO;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.session.SesionDemo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

/** "Mis donaciones" del donante (H2.1) y el detalle con su historial de estados (H2.2). */
@Service
public class MisDonacionesService {

  /**
   * Filtros pedidos; los que no corresponden (p. ej. subcategoría de otra categoría) se descartan.
   */
  public record Filtros(String estado, UUID categoriaId, UUID subcategoriaId) {
    public boolean activos() {
      return estado != null || categoriaId != null || subcategoriaId != null;
    }
  }

  public record OpcionEstado(String codigo, String etiqueta) {}

  public record OpcionCategoria(UUID id, String nombre, List<OpcionSubcategoria> subcategorias) {}

  public record OpcionSubcategoria(UUID id, String nombre) {}

  /**
   * Página de la lista.
   *
   * @param categorias vacía si no se pudieron cargar (la lista funciona igual, sin ese filtro)
   */
  public record MisDonaciones(
      Seccion<DonacionResumen> donaciones,
      List<OpcionEstado> estados,
      List<OpcionCategoria> categorias,
      Filtros filtros) {}

  public record ItemVista(
      String descripcion,
      String subcategoria,
      String categoria,
      String iconoCategoria,
      Integer cantidad,
      String unidad,
      String estadoBien,
      LocalDate vencimiento) {}

  /** Un cambio de estado; {@code actual} marca el último. */
  public record PasoHistorial(
      EstadoDonacionVista estado, LocalDateTime fecha, String justificacion, boolean actual) {}

  public record DetalleDonacion(
      UUID id,
      String descripcion,
      EstadoDonacionVista estado,
      LocalDateTime fechaRegistro,
      int cantidad,
      List<ItemVista> items,
      List<PasoHistorial> historial) {}

  /** Resultado del detalle: la donación, o el error si el servicio falló. Vacío = no existe. */
  public record CargaDetalle(Optional<DetalleDonacion> detalle, ErrorVista error, boolean demo) {}

  private final DonacionesPort donaciones;
  private final CargadorSecciones cargador;
  private final TraductorErrores traductor;
  private final VistaDonaciones vista;

  public MisDonacionesService(
      DonacionesPort donaciones,
      CargadorSecciones cargador,
      TraductorErrores traductor,
      VistaDonaciones vista) {
    this.donaciones = donaciones;
    this.cargador = cargador;
    this.traductor = traductor;
    this.vista = vista;
  }

  public MisDonaciones listar(SesionDemo sesion, Filtros pedidos) {
    List<OpcionCategoria> categorias = categorias();
    Filtros filtros = sanear(pedidos, categorias);
    Seccion<DonacionResumen> lista =
        cargador.cargar(
            donaciones.esDemo(),
            () ->
                donaciones
                    .donacionesIndependientes(
                        new FiltroDonaciones(
                            sesion.donanteId(), filtros.estado(), filtros.subcategoriaId()))
                    .stream()
                    .filter(d -> filtros.categoriaId() == null || esDeCategoria(d, filtros))
                    .sorted(VistaDonaciones.MAS_RECIENTE_PRIMERO)
                    .map(vista::resumen)
                    .toList());
    List<OpcionEstado> estados =
        VistaDonaciones.ESTADOS.stream()
            .map(e -> new OpcionEstado(e, vista.estado(e).etiqueta()))
            .toList();
    return new MisDonaciones(lista, estados, categorias, filtros);
  }

  /**
   * Detalle de una donación del donante. Se busca entre las suyas (la respuesta del backend no dice
   * de quién es): así nadie ve donaciones ajenas cambiando el id en la URL.
   */
  public CargaDetalle detalle(SesionDemo sesion, UUID id) {
    try {
      Optional<DetalleDonacion> detalle =
          donaciones
              .donacionesIndependientes(FiltroDonaciones.delDonante(sesion.donanteId()))
              .stream()
              .filter(d -> d.id().equals(id))
              .findFirst()
              .map(this::detalle);
      return new CargaDetalle(detalle, null, donaciones.esDemo());
    } catch (BackendException e) {
      return new CargaDetalle(Optional.empty(), traductor.traducir(e), false);
    }
  }

  private DetalleDonacion detalle(DonacionIndependienteResponseDTO d) {
    List<CambioEstadoDIResponseDTO> cambios =
        d.historial().stream()
            .sorted(
                Comparator.comparing(
                    CambioEstadoDIResponseDTO::timestamp,
                    Comparator.nullsFirst(Comparator.naturalOrder())))
            .toList();
    List<PasoHistorial> historial =
        java.util.stream.IntStream.range(0, cambios.size())
            .mapToObj(
                i ->
                    new PasoHistorial(
                        vista.estado(cambios.get(i).estadoNuevo()),
                        cambios.get(i).timestamp(),
                        cambios.get(i).justificacion(),
                        i == cambios.size() - 1))
            .toList();
    List<ItemVista> items =
        d.items().stream()
            .filter(i -> i.bien() != null)
            .map(
                i -> {
                  BienNormalizadoDTO b = i.bien();
                  String categoria = b.categoria() != null ? b.categoria().nombre() : null;
                  return new ItemVista(
                      b.bien() != null ? b.bien().descripcion() : null,
                      b.subcategoria() != null ? b.subcategoria().nombre() : null,
                      categoria,
                      IconoCategoria.de(categoria),
                      i.cantidad(),
                      b.categoria() != null ? vista.unidad(b.categoria().unidad()) : "",
                      b.bien() != null ? vista.estadoBien(b.bien().estado()) : null,
                      b.bien() != null ? b.bien().fechaVencimiento() : null);
                })
            .toList();
    return new DetalleDonacion(
        d.id(),
        d.descripcion(),
        vista.estado(d.estadoActual()),
        d.fechaRegistro(),
        d.cantidad(),
        items,
        historial);
  }

  /** Categorías para el filtro; si el servicio falla, el filtro no se ofrece. */
  private List<OpcionCategoria> categorias() {
    try {
      return donaciones.categorias().stream()
          .sorted(Comparator.comparing(CategoriaOutputDTO::nombre))
          .map(
              c ->
                  new OpcionCategoria(
                      c.id(),
                      c.nombre(),
                      Optional.ofNullable(c.subcategorias()).orElse(List.of()).stream()
                          .map(s -> new OpcionSubcategoria(s.id(), s.nombre()))
                          .sorted(Comparator.comparing(OpcionSubcategoria::nombre))
                          .toList()))
          .toList();
    } catch (BackendException e) {
      return List.of();
    }
  }

  private static Filtros sanear(Filtros f, List<OpcionCategoria> categorias) {
    String estado =
        f.estado() != null && VistaDonaciones.ESTADOS.contains(f.estado()) ? f.estado() : null;
    Optional<OpcionCategoria> categoria =
        categorias.stream().filter(c -> c.id().equals(f.categoriaId())).findFirst();
    UUID subcategoria =
        categoria
            .flatMap(
                c ->
                    c.subcategorias().stream()
                        .map(OpcionSubcategoria::id)
                        .filter(id -> id.equals(f.subcategoriaId()))
                        .findFirst())
            .orElse(null);
    return new Filtros(estado, categoria.map(OpcionCategoria::id).orElse(null), subcategoria);
  }

  private static boolean esDeCategoria(DonacionIndependienteResponseDTO d, Filtros f) {
    return d.items().stream()
        .map(i -> i.bien())
        .filter(Objects::nonNull)
        .anyMatch(b -> b.categoria() != null && f.categoriaId().equals(b.categoria().id()));
  }
}
