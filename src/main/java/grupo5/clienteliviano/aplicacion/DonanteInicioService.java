package grupo5.clienteliviano.aplicacion;

import grupo5.clienteliviano.aplicacion.VistaDonaciones.DonacionResumen;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort.FiltroDonaciones;
import grupo5.clienteliviano.integracion.donaciones.dto.DonacionIndependienteResponseDTO;
import grupo5.clienteliviano.session.SesionDemo;
import java.util.Comparator;
import org.springframework.stereotype.Service;

/** Casos de uso del inicio del donante. */
@Service
public class DonanteInicioService {

  private static final int ULTIMAS = 3;

  private final DonacionesPort donaciones;
  private final CargadorSecciones cargador;
  private final VistaDonaciones vista;

  public DonanteInicioService(
      DonacionesPort donaciones, CargadorSecciones cargador, VistaDonaciones vista) {
    this.donaciones = donaciones;
    this.cargador = cargador;
    this.vista = vista;
  }

  public Seccion<DonacionResumen> ultimasDonaciones(SesionDemo sesion) {
    return cargador.cargar(
        donaciones.esDemo(),
        () ->
            donaciones
                .donacionesIndependientes(FiltroDonaciones.delDonante(sesion.donanteId()))
                .stream()
                .sorted(MAS_RECIENTE_PRIMERO)
                .limit(ULTIMAS)
                .map(vista::resumen)
                .toList());
  }

  static final Comparator<DonacionIndependienteResponseDTO> MAS_RECIENTE_PRIMERO =
      Comparator.comparing(
              DonacionIndependienteResponseDTO::fechaRegistro,
              Comparator.nullsFirst(Comparator.<java.time.LocalDateTime>naturalOrder()))
          .reversed();
}
