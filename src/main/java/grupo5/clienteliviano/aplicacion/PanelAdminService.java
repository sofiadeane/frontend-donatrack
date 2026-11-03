package grupo5.clienteliviano.aplicacion;

import grupo5.clienteliviano.integracion.fixtures.LectorFixtures;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

/**
 * Panel de la persona administradora (variante visual B). Todavía no hay un servicio del backend
 * que agregue estas cifras, así que salen de {@code fixtures/admin/panel.json} y la vista lo
 * indica.
 */
@Service
public class PanelAdminService {

  private static final Locale ES = Locale.forLanguageTag("es-AR");

  public record Etapa(String estado, int cantidad, String nota) {}

  public record Semana(
      String etiqueta, int recibidas, int entregadas, int kgRecibidos, int kgEntregados) {}

  public record Pendiente(
      String nivel, String titulo, String detalle, String accion, String href) {}

  public record Camion(
      String nombre,
      String estado,
      String ruta,
      int paradasHechas,
      int paradasTotal,
      String eta,
      int carga) {
    public int avance() {
      return paradasTotal == 0 ? 0 : Math.round(100f * paradasHechas / paradasTotal);
    }
  }

  public record Atencion(
      String codigo,
      String bien,
      String donante,
      String destino,
      String estado,
      String antiguedad,
      boolean urgente) {}

  public record Evento(String cuando, String estado, String texto) {}

  public record Categoria(String nombre, int porcentaje) {}

  public record Impacto(int kg, int entidades, List<Categoria> categorias) {}

  public record Necesidad(String entidad, String bien, int cubiertas, int total, boolean urgente) {
    public int porcentaje() {
      return total == 0 ? 0 : Math.round(100f * cubiertas / total);
    }
  }

  /** Datos del fixture más lo derivado que necesita la vista. */
  public record Panel(
      List<Etapa> pipeline,
      int entregadasMes,
      int entregadasDelta,
      int recibidasMes,
      int recibidasDelta,
      List<Semana> semanas,
      List<Pendiente> pendientes,
      List<Camion> camiones,
      List<Atencion> atencion,
      List<Evento> actividad,
      Impacto impacto,
      List<Necesidad> necesidades) {

    public int totalActivas() {
      return pipeline.stream().mapToInt(Etapa::cantidad).sum();
    }

    public int maxSemana() {
      return semanas.stream()
          .mapToInt(s -> Math.max(s.recibidas(), s.entregadas()))
          .max()
          .orElse(1);
    }

    public int maxSemanaKg() {
      return semanas.stream()
          .mapToInt(s -> Math.max(s.kgRecibidos(), s.kgEntregados()))
          .max()
          .orElse(1);
    }

    public long urgentes() {
      return pendientes.stream().filter(p -> "ALTA".equals(p.nivel())).count();
    }

    public long atencionUrgente() {
      return atencion.stream().filter(Atencion::urgente).count();
    }

    public long camionesEnRuta() {
      return camiones.stream().filter(c -> "EN_RUTA".equals(c.estado())).count();
    }
  }

  private final Panel panel;
  private final Clock reloj;

  public PanelAdminService(LectorFixtures lector, Clock reloj) {
    this.panel = lector.objeto("admin/panel.json", Panel.class);
    this.reloj = reloj;
  }

  public Panel panel() {
    return panel;
  }

  /** "jueves 1 de octubre", según el reloj de la aplicación. */
  public String fechaLarga() {
    LocalDate hoy = LocalDate.now(reloj);
    return hoy.getDayOfWeek().getDisplayName(TextStyle.FULL, ES)
        + " "
        + hoy.getDayOfMonth()
        + " de "
        + hoy.getMonth().getDisplayName(TextStyle.FULL, ES);
  }
}
