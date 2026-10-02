package grupo5.clienteliviano.aplicacion.registro;

import static grupo5.clienteliviano.aplicacion.registro.Texto.conValor;

import grupo5.clienteliviano.aplicacion.registro.FormularioRegistro.Representante;
import grupo5.clienteliviano.aplicacion.registro.FormularioRegistro.Telefono;
import grupo5.clienteliviano.integracion.personas.dto.Genero;
import grupo5.clienteliviano.integracion.personas.dto.TipoDocumento;
import grupo5.clienteliviano.integracion.personas.dto.TipoJuridico;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Validación en el cliente: las mismas reglas que el backend (para avisar antes de enviar) más las
 * propias de la interfaz (correo obligatorio, un representante como mínimo, privacidad aceptada).
 */
@Component
public class ValidadorRegistro {

  private static final Pattern CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
  private static final Pattern NUMERO_TELEFONO = Pattern.compile("^[0-9 ()-]{6,20}$");
  static final int MAX_APODO = 40;

  private final Clock reloj;

  public ValidadorRegistro(Clock reloj) {
    this.reloj = reloj;
  }

  public ErroresFormulario validar(FormularioRegistro f) {
    ErroresFormulario errores = new ErroresFormulario();
    TipoCuenta tipo = TipoCuenta.desde(f.getTipo()).orElse(null);
    if (tipo == null) {
      errores.agregarGeneral("Elegí cómo te querés registrar.");
      return errores;
    }
    if (tipo.esOrganizacion()) {
      datosOrganizacion(f, errores);
      representantes(f.getRepresentantes(), errores);
    } else {
      datosPersona(f, errores);
    }
    enumOpcional(f.getTipoDocumento(), TipoDocumento.class, "tipoDocumento", errores);
    contacto(f, errores);
    domicilio(f, errores);
    if (conValor(f.getApodo()) && f.getApodo().strip().length() > MAX_APODO) {
      errores.agregar("apodo", "El apodo puede tener hasta " + MAX_APODO + " caracteres.");
    }
    if (!f.isAceptaPrivacidad()) {
      errores.agregar("aceptaPrivacidad", "Para crear la cuenta tenés que aceptar la política.");
    }
    return errores;
  }

  private void datosPersona(FormularioRegistro f, ErroresFormulario errores) {
    if (!conValor(f.getNombre())) {
      errores.agregar("nombre", "El nombre es obligatorio.");
    }
    if (!conValor(f.getApellido())) {
      errores.agregar("apellido", "El apellido es obligatorio.");
    }
    if (conValor(f.getFechaNacimiento())) {
      try {
        if (LocalDate.parse(f.getFechaNacimiento()).isAfter(LocalDate.now(reloj))) {
          errores.agregar("fechaNacimiento", "La fecha de nacimiento no puede ser futura.");
        }
      } catch (DateTimeParseException e) {
        errores.agregar("fechaNacimiento", "Escribí una fecha válida.");
      }
    }
    enumOpcional(f.getGenero(), Genero.class, "genero", errores);
  }

  private void datosOrganizacion(FormularioRegistro f, ErroresFormulario errores) {
    if (!conValor(f.getRazonSocial())) {
      errores.agregar("razonSocial", "La razón social es obligatoria.");
    }
    if (!conValor(f.getTipoJuridico())) {
      errores.agregar("tipoJuridico", "Elegí el tipo de organización.");
    } else {
      enumOpcional(f.getTipoJuridico(), TipoJuridico.class, "tipoJuridico", errores);
    }
  }

  private void representantes(List<Representante> lista, ErroresFormulario errores) {
    if (lista == null || lista.isEmpty()) {
      errores.agregar("representantes[0].nombre", "Cargá al menos un representante.");
      return;
    }
    for (int i = 0; i < lista.size(); i++) {
      Representante r = lista.get(i);
      String p = "representantes[" + i + "].";
      if (!conValor(r.getNombre())) {
        errores.agregar(p + "nombre", "El nombre es obligatorio.");
      }
      if (!conValor(r.getApellido())) {
        errores.agregar(p + "apellido", "El apellido es obligatorio.");
      }
      enumOpcional(r.getTipoDocumento(), TipoDocumento.class, p + "tipoDocumento", errores);
      if (conValor(r.getCorreo()) && !CORREO.matcher(r.getCorreo().strip()).matches()) {
        errores.agregar(p + "correo", "Escribí un correo con el formato nombre@dominio.com.");
      }
    }
  }

  private void contacto(FormularioRegistro f, ErroresFormulario errores) {
    if (!conValor(f.getCorreo())) {
      errores.agregar("correo", "El correo electrónico es obligatorio.");
    } else if (!CORREO.matcher(f.getCorreo().strip()).matches()) {
      errores.agregar("correo", "Escribí un correo con el formato nombre@dominio.com.");
    }
    telefono(f.getTelefono(), "telefono.numero", errores);
    telefono(f.getWhatsapp(), "whatsapp.numero", errores);
    String preferido = f.getPreferido();
    if ("TELEFONO".equals(preferido) && !conValor(f.getTelefono().getNumero())) {
      errores.agregar("preferido", "Cargá un teléfono o elegí otro medio preferido.");
    } else if ("WHATSAPP".equals(preferido) && !conValor(f.getWhatsapp().getNumero())) {
      errores.agregar("preferido", "Cargá un WhatsApp o elegí otro medio preferido.");
    }
  }

  private static void telefono(Telefono t, String campo, ErroresFormulario errores) {
    if (conValor(t.getNumero()) && !NUMERO_TELEFONO.matcher(t.getNumero().strip()).matches()) {
      errores.agregar(campo, "Escribí solo números (podés usar espacios o guiones).");
    }
  }

  private static void domicilio(FormularioRegistro f, ErroresFormulario errores) {
    if (!f.tieneDomicilio()) {
      return;
    }
    if (!conValor(f.getCalle())) {
      errores.agregar("calle", "La calle es obligatoria si cargás un domicilio.");
    }
    entero(f.getAltura(), "altura", "La altura debe ser un número mayor a cero.", true, errores);
    entero(f.getPiso(), "piso", "El piso debe ser un número.", false, errores);
    if (!conValor(f.getLocalidad())) {
      errores.agregar("localidad", "La localidad es obligatoria si cargás un domicilio.");
    }
    if (!conValor(f.getProvincia())) {
      errores.agregar("provincia", "La provincia es obligatoria si cargás un domicilio.");
    }
    if (!conValor(f.getPais())) {
      errores.agregar("pais", "El país es obligatorio si cargás un domicilio.");
    }
  }

  private static void entero(
      String valor, String campo, String mensaje, boolean positivo, ErroresFormulario errores) {
    if (!conValor(valor)) {
      return;
    }
    try {
      int n = Integer.parseInt(valor.strip());
      if (positivo && n <= 0) {
        errores.agregar(campo, mensaje);
      }
    } catch (NumberFormatException e) {
      errores.agregar(campo, mensaje);
    }
  }

  private static <E extends Enum<E>> void enumOpcional(
      String valor, Class<E> tipo, String campo, ErroresFormulario errores) {
    if (conValor(valor)
        && Arrays.stream(tipo.getEnumConstants()).noneMatch(e -> e.name().equals(valor))) {
      errores.agregar(campo, "Elegí una de las opciones.");
    }
  }
}
