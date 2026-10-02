package grupo5.clienteliviano.config;

import grupo5.clienteliviano.session.GuardaDeRolInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final GuardaDeRolInterceptor guardaDeRol;

  public WebConfig(GuardaDeRolInterceptor guardaDeRol) {
    this.guardaDeRol = guardaDeRol;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(guardaDeRol)
        .addPathPatterns(
            "/donante/**", "/donante", "/entidad/**", "/entidad", "/admin/**", "/admin");
  }
}
