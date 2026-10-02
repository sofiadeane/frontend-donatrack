package grupo5.clienteliviano.integracion.metricas;

import java.util.List;

/**
 * Métricas de "Transparencia en números". El backend no expone un endpoint agregado (brecha G7 de
 * la especificación), así que hoy solo existe el adapter de demostración.
 */
public interface MetricasPublicasPort {

  /**
   * Una cifra de la landing.
   *
   * @param detalleTitulo y detalleTexto: lo que se despliega desde arriba del tile
   */
  record Metrica(
      String clave, long valor, String etiqueta, String detalleTitulo, String detalleTexto) {}

  List<Metrica> metricas();

  boolean esDemo();
}
