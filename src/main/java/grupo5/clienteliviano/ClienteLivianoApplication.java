package grupo5.clienteliviano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** Frontend web MVC de DonaTrack: renderiza HTML en el servidor y orquesta los servicios. */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ClienteLivianoApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClienteLivianoApplication.class, args);
  }
}
