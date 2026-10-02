package grupo5.clienteliviano.navegacion;

import grupo5.clienteliviano.session.Rol;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Navegación por rol (spec §3). Notificaciones se abre desde la campana en celular. */
@Component
public class CatalogoNavegacion {

  private static final Map<Rol, List<ItemNavegacion>> ITEMS =
      Map.of(
          Rol.DONANTE,
          List.of(
              new ItemNavegacion("Inicio", "/donante", "inicio", true),
              new ItemNavegacion(
                  "Mis donaciones", "Donaciones", "/donante/donaciones", "donaciones", true),
              new ItemNavegacion("Entidades", "/donante/entidades", "entidades", true),
              new ItemNavegacion("Incentivos", "/donante/incentivos", "incentivos", true),
              new ItemNavegacion("Seguimiento", "/donante/seguimiento", "seguimiento", true),
              new ItemNavegacion(
                  "Notificaciones", "/donante/notificaciones", "notificaciones", false)),
          Rol.ENTIDAD,
          List.of(
              new ItemNavegacion("Inicio", "/entidad", "inicio", true),
              new ItemNavegacion("Necesidades", "/entidad/necesidades", "necesidades", true),
              new ItemNavegacion(
                  "Donaciones asignadas", "Asignadas", "/entidad/donaciones", "donaciones", true),
              new ItemNavegacion("Seguimiento", "/entidad/seguimiento", "seguimiento", true),
              new ItemNavegacion(
                  "Notificaciones", "/entidad/notificaciones", "notificaciones", false)),
          Rol.ADMIN,
          List.of(
              new ItemNavegacion("Inicio", "/admin", "inicio", true),
              new ItemNavegacion("Donantes", "/admin/donantes", "personas", false),
              new ItemNavegacion("Entidades", "/admin/entidades", "entidades", false),
              new ItemNavegacion("Donaciones", "/admin/donaciones", "donaciones", true),
              new ItemNavegacion("Asignaciones", "/admin/asignaciones", "asignaciones", true),
              new ItemNavegacion("Camiones", "/admin/camiones", "camiones", true),
              new ItemNavegacion("Rankings", "/admin/rankings", "incentivos", true)));

  public List<ItemNavegacion> de(Rol rol) {
    return ITEMS.get(rol);
  }

  public List<ItemNavegacion> movil(Rol rol) {
    return ITEMS.get(rol).stream().filter(ItemNavegacion::enBarraMovil).toList();
  }
}
