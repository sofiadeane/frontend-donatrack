package grupo5.clienteliviano.aplicacion.registro;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Errores de un envío: por campo (clave = nombre del campo en el formulario, en el orden en que
 * aparecen) y generales (sin campo). Sabe a qué sección pertenece cada campo para marcarla.
 */
public class ErroresFormulario {

  /** Nombre del campo de correo (también es el del medio de contacto predeterminado). */
  public static final String CORREO = "correo";

  private final Map<String, String> porCampo = new LinkedHashMap<>();
  private final List<String> generales = new ArrayList<>();

  public void agregar(String campo, String mensaje) {
    porCampo.putIfAbsent(campo, mensaje);
  }

  public void agregarGeneral(String mensaje) {
    generales.add(mensaje);
  }

  public boolean vacio() {
    return porCampo.isEmpty() && generales.isEmpty();
  }

  public Map<String, String> getPorCampo() {
    return porCampo;
  }

  public List<String> getGenerales() {
    return generales;
  }

  public int getTotal() {
    return porCampo.size() + generales.size();
  }

  public boolean tiene(String campo) {
    return porCampo.containsKey(campo);
  }

  public String de(String campo) {
    return porCampo.get(campo);
  }

  /** Cantidad de errores en una sección ({@code datos}, {@code representantes}, …). */
  public long enSeccion(String seccion) {
    return porCampo.keySet().stream().filter(c -> seccionDe(c).equals(seccion)).count();
  }

  /** Id del control en la página: {@code representantes[1].nombre → rep-1-nombre}. */
  public String idDe(String campo) {
    return campo.replace("representantes[", "rep-").replace("].", "-").replace('.', '-');
  }

  public String etiqueta(String campo) {
    return etiquetaDe(campo);
  }

  /** Etiqueta legible del campo para el resumen de errores. */
  public static String etiquetaDe(String campo) {
    if (campo.startsWith("representantes[")) {
      int n = Integer.parseInt(campo.substring(15, campo.indexOf(']'))) + 1;
      return "Representante " + n + " · " + etiquetaDe(campo.substring(campo.indexOf('.') + 1));
    }
    return switch (campo) {
      case "nombre" -> "Nombre";
      case "apellido" -> "Apellido";
      case "tipoDocumento" -> "Tipo de documento";
      case "documento" -> "Número de documento";
      case "fechaNacimiento" -> "Fecha de nacimiento";
      case "genero" -> "Género";
      case "razonSocial" -> "Razón social";
      case "tipoJuridico" -> "Tipo de organización";
      case "rubro" -> "Rubro";
      case CORREO -> "Correo electrónico";
      case "telefono.numero" -> "Teléfono";
      case "whatsapp.numero" -> "WhatsApp";
      case "preferido" -> "Medio de contacto preferido";
      case "calle" -> "Calle";
      case "altura" -> "Altura";
      case "piso" -> "Piso";
      case "departamento" -> "Departamento";
      case "codigoPostal" -> "Código postal";
      case "localidad" -> "Localidad";
      case "provincia" -> "Provincia";
      case "pais" -> "País";
      case "apodo" -> "Apodo";
      case "aceptaPrivacidad" -> "Política de privacidad";
      default -> campo;
    };
  }

  static String seccionDe(String campo) {
    if (campo.startsWith("representantes")) {
      return "representantes";
    }
    return switch (campo) {
      case CORREO, "telefono.numero", "whatsapp.numero", "preferido" -> "contacto";
      case "calle",
          "altura",
          "piso",
          "departamento",
          "codigoPostal",
          "localidad",
          "provincia",
          "pais" ->
          "domicilio";
      case "apodo" -> "publico";
      case "aceptaPrivacidad" -> "envio";
      default -> "datos";
    };
  }
}
