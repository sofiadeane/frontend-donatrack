package grupo5.clienteliviano.web;

import grupo5.clienteliviano.aplicacion.Seccion.ErrorVista;
import grupo5.clienteliviano.aplicacion.registro.ErroresFormulario;
import grupo5.clienteliviano.aplicacion.registro.FormularioRegistro;
import grupo5.clienteliviano.aplicacion.registro.FormularioRegistro.Representante;
import grupo5.clienteliviano.aplicacion.registro.RegistroService;
import grupo5.clienteliviano.aplicacion.registro.ResultadoRegistro;
import grupo5.clienteliviano.aplicacion.registro.ResultadoRegistro.Exito;
import grupo5.clienteliviano.aplicacion.registro.ResultadoRegistro.Fallido;
import grupo5.clienteliviano.aplicacion.registro.ResultadoRegistro.Invalido;
import grupo5.clienteliviano.aplicacion.registro.ResultadoRegistro.Parcial;
import grupo5.clienteliviano.aplicacion.registro.ResultadoRegistro.Pendiente;
import grupo5.clienteliviano.aplicacion.registro.TipoCuenta;
import jakarta.servlet.http.HttpSession;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Registro público (F05, J1). Funciona sin JS: agregar o quitar representantes vuelve a mostrar el
 * formulario con lo cargado; el envío exitoso redirige (PRG) a la confirmación.
 */
@Controller
public class RegistroController {

  static final String PENDIENTE = "registroPendiente";
  private static final String VOLVER_AL_FORMULARIO = "redirect:/registro";

  private final RegistroService registro;
  private final Clock reloj;

  public RegistroController(RegistroService registro, Clock reloj) {
    this.registro = registro;
    this.reloj = reloj;
  }

  @GetMapping("/registro")
  public String formulario(@RequestParam(required = false) String tipo, Model model) {
    FormularioRegistro form = new FormularioRegistro();
    TipoCuenta.desde(tipo).ifPresent(t -> form.setTipo(t.parametro()));
    return mostrar(form, new ErroresFormulario(), model);
  }

  @PostMapping("/registro")
  public String enviar(
      @ModelAttribute("form") FormularioRegistro form,
      @RequestParam(required = false) String accion,
      @RequestParam(required = false) Integer quitar,
      Model model,
      HttpSession sesion,
      RedirectAttributes redirect) {
    if ("agregar-representante".equals(accion)) {
      form.getRepresentantes().add(new Representante());
      model.addAttribute("foco", "representantes[" + (form.getRepresentantes().size() - 1) + "]");
      return mostrar(form, new ErroresFormulario(), model);
    }
    if (quitar != null) {
      if (form.getRepresentantes().size() > 1 && quitar < form.getRepresentantes().size()) {
        form.getRepresentantes().remove((int) quitar);
      }
      return mostrar(form, new ErroresFormulario(), model);
    }
    return resolver(registro.registrar(form), form, model, sesion, redirect);
  }

  @GetMapping("/registro/listo")
  public String listo(Model model) {
    return model.containsAttribute("exito") ? "publico/registro-listo" : VOLVER_AL_FORMULARIO;
  }

  @GetMapping("/registro/pendiente")
  public String pendiente(HttpSession sesion, Model model) {
    if (!(sesion.getAttribute(PENDIENTE) instanceof Pendiente pendiente)) {
      return VOLVER_AL_FORMULARIO;
    }
    model.addAttribute("pendiente", pendiente);
    return "publico/registro-pendiente";
  }

  @PostMapping("/registro/reintentar")
  public String reintentar(HttpSession sesion, Model model, RedirectAttributes redirect) {
    if (!(sesion.getAttribute(PENDIENTE) instanceof Pendiente pendiente)) {
      return VOLVER_AL_FORMULARIO;
    }
    ResultadoRegistro resultado = registro.completar(pendiente);
    if (resultado instanceof Parcial(Pendiente mismo, ErrorVista error)) {
      model.addAttribute("pendiente", mismo);
      model.addAttribute("error", error);
      return "publico/registro-pendiente";
    }
    return resolver(resultado, null, model, sesion, redirect);
  }

  private String resolver(
      ResultadoRegistro resultado,
      FormularioRegistro form,
      Model model,
      HttpSession sesion,
      RedirectAttributes redirect) {
    return switch (resultado) {
      case Exito exito -> {
        sesion.removeAttribute(PENDIENTE);
        redirect.addFlashAttribute("exito", exito);
        yield "redirect:/registro/listo";
      }
      case Parcial(Pendiente pendiente, ErrorVista error) -> {
        sesion.setAttribute(PENDIENTE, pendiente);
        yield "redirect:/registro/pendiente";
      }
      case Invalido(ErroresFormulario errores) -> mostrar(form, errores, model);
      case Fallido(ErrorVista error) -> {
        model.addAttribute("falla", error);
        yield mostrar(form, new ErroresFormulario(), model);
      }
    };
  }

  private String mostrar(FormularioRegistro form, ErroresFormulario errores, Model model) {
    model.addAttribute("form", form);
    model.addAttribute("errores", errores);
    model.addAttribute("demo", registro.esDemo());
    model.addAttribute("hoy", LocalDate.now(reloj));
    return "publico/registro";
  }
}
