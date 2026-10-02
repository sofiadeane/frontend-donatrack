package grupo5.clienteliviano.navegacion;

/**
 * Entrada de la navegación privada.
 *
 * @param etiquetaCorta etiqueta para la barra inferior del celular (si es nula, se usa etiqueta)
 * @param enBarraMovil si aparece en la barra inferior del celular (máximo 5 por rol)
 */
public record ItemNavegacion(
    String etiqueta, String etiquetaCorta, String href, String icono, boolean enBarraMovil) {

  public ItemNavegacion(String etiqueta, String href, String icono, boolean enBarraMovil) {
    this(etiqueta, null, href, icono, enBarraMovil);
  }

  public String etiquetaMovil() {
    return etiquetaCorta != null ? etiquetaCorta : etiqueta;
  }

  public boolean activoEn(String ruta, String inicioDelRol) {
    if (href.equals(inicioDelRol)) {
      return ruta.equals(href);
    }
    return ruta.equals(href) || ruta.startsWith(href + "/");
  }
}
