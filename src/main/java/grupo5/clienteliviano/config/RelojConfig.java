package grupo5.clienteliviano.config;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Reloj inyectable: las reglas que dependen de "hoy" (p. ej. el último mes) se pueden probar. Con
 * {@code donatrack.reloj.fijo} (ISO-8601 con zona) el reloj queda fijo: lo usa el export estático
 * para que los datos de demostración no "envejezcan".
 */
@Configuration
public class RelojConfig {

  @Bean
  Clock reloj(@Value("${donatrack.reloj.fijo:}") String fijo) {
    if (fijo == null || fijo.isBlank()) {
      return Clock.systemDefaultZone();
    }
    OffsetDateTime instante = OffsetDateTime.parse(fijo);
    return Clock.fixed(instante.toInstant(), ZoneId.of("America/Argentina/Buenos_Aires"));
  }
}
