package grupo5.clienteliviano.integracion;

import grupo5.clienteliviano.integracion.BackendProperties.Modo;
import grupo5.clienteliviano.integracion.donaciones.DonacionesFixtureAdapter;
import grupo5.clienteliviano.integracion.donaciones.DonacionesHttpAdapter;
import grupo5.clienteliviano.integracion.donaciones.DonacionesPort;
import grupo5.clienteliviano.integracion.fixtures.LectorFixtures;
import grupo5.clienteliviano.integracion.http.FabricaClientesHttp;
import grupo5.clienteliviano.integracion.incentivos.IncentivosFixtureAdapter;
import grupo5.clienteliviano.integracion.incentivos.IncentivosHttpAdapter;
import grupo5.clienteliviano.integracion.incentivos.IncentivosPort;
import grupo5.clienteliviano.integracion.notificaciones.NotificacionesFixtureAdapter;
import grupo5.clienteliviano.integracion.notificaciones.NotificacionesHttpAdapter;
import grupo5.clienteliviano.integracion.notificaciones.NotificacionesPort;
import grupo5.clienteliviano.integracion.personas.RegistroFixtureAdapter;
import grupo5.clienteliviano.integracion.personas.RegistroHttpAdapter;
import grupo5.clienteliviano.integracion.personas.RegistroPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Elige, para cada servicio, el adapter real o el de demostración según la configuración. */
@Configuration
public class AdaptersConfig {

  private static final Logger log = LoggerFactory.getLogger(AdaptersConfig.class);

  private final BackendProperties properties;

  public AdaptersConfig(BackendProperties properties) {
    this.properties = properties;
  }

  @Bean
  DonacionesPort donacionesPort(FabricaClientesHttp fabrica, LectorFixtures lector) {
    Modo modo = properties.de(Servicio.DONACIONES).modo();
    log.info("donaciones-service: adapter {}", modo);
    return modo == Modo.HTTP
        ? new DonacionesHttpAdapter(fabrica.para(Servicio.DONACIONES))
        : new DonacionesFixtureAdapter(lector);
  }

  @Bean
  RegistroPort registroPort(FabricaClientesHttp fabrica) {
    Modo modo = properties.de(Servicio.DONACIONES).modo();
    return modo == Modo.HTTP
        ? new RegistroHttpAdapter(fabrica.para(Servicio.DONACIONES))
        : new RegistroFixtureAdapter();
  }

  @Bean
  IncentivosPort incentivosPort(FabricaClientesHttp fabrica, LectorFixtures lector) {
    Modo modo = properties.de(Servicio.INCENTIVOS).modo();
    log.info("incentivos-service: adapter {}", modo);
    return modo == Modo.HTTP
        ? new IncentivosHttpAdapter(fabrica.para(Servicio.INCENTIVOS))
        : new IncentivosFixtureAdapter(lector);
  }

  @Bean
  NotificacionesPort notificacionesPort(FabricaClientesHttp fabrica, LectorFixtures lector) {
    Modo modo = properties.de(Servicio.NOTIFICACIONES).modo();
    log.info("notificaciones-service: adapter {}", modo);
    return modo == Modo.HTTP
        ? new NotificacionesHttpAdapter(fabrica.para(Servicio.NOTIFICACIONES))
        : new NotificacionesFixtureAdapter(lector);
  }
}
