package grupo5.clienteliviano.aplicacion.registro;

import static grupo5.clienteliviano.aplicacion.registro.Texto.conValor;
import static grupo5.clienteliviano.aplicacion.registro.Texto.limpio;

import grupo5.clienteliviano.aplicacion.TraductorErrores;
import grupo5.clienteliviano.aplicacion.registro.FormularioRegistro.Representante;
import grupo5.clienteliviano.aplicacion.registro.FormularioRegistro.Telefono;
import grupo5.clienteliviano.aplicacion.registro.ResultadoRegistro.Exito;
import grupo5.clienteliviano.aplicacion.registro.ResultadoRegistro.Fallido;
import grupo5.clienteliviano.aplicacion.registro.ResultadoRegistro.Invalido;
import grupo5.clienteliviano.aplicacion.registro.ResultadoRegistro.Parcial;
import grupo5.clienteliviano.aplicacion.registro.ResultadoRegistro.Pendiente;
import grupo5.clienteliviano.integracion.error.BackendException;
import grupo5.clienteliviano.integracion.error.ErrorBackend.ErrorDeCampo;
import grupo5.clienteliviano.integracion.personas.RegistroPort;
import grupo5.clienteliviano.integracion.personas.dto.DireccionInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.Genero;
import grupo5.clienteliviano.integracion.personas.dto.MedioDeContactoInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.MedioDeContactoInputDTO.CorreoInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.MedioDeContactoInputDTO.TelefonoInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.MedioDeContactoInputDTO.WhatsAppInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.PersonaInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.PersonaInputDTO.HumanaInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.PersonaInputDTO.JuridicaInputDTO;
import grupo5.clienteliviano.integracion.personas.dto.TipoDocumento;
import grupo5.clienteliviano.integracion.personas.dto.TipoJuridico;
import grupo5.clienteliviano.integracion.personas.dto.TipoPersona;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

/**
 * Registro (F05): {@code POST /api/personas} y después {@code POST /api/donantes} (persona humana u
 * organización donante) o {@code POST /api/entidades} (entidad beneficiaria).
 */
@Service
public class RegistroService {

  /** Códigos de validación de dominio que el backend devuelve sin campo, con su campo. */
  private static final Map<String, String> CAMPO_POR_CODIGO =
      Map.of(
          "ERR-VAL-101", "nombre",
          "ERR-VAL-102", "apellido",
          "ERR-VAL-103", "fechaNacimiento",
          "ERR-VAL-108", "calle",
          "ERR-VAL-109", "altura",
          "ERR-VAL-110", "codigoPostal",
          "ERR-VAL-111", "localidad");

  private static final Pattern MEDIO = Pattern.compile("^mediosDeContacto\\[(\\d+)]\\..*$");
  private static final Pattern REPRESENTANTE = Pattern.compile("^representantes\\[(\\d+)]\\.(.+)$");

  private final RegistroPort registro;
  private final ValidadorRegistro validador;
  private final TraductorErrores traductor;

  public RegistroService(
      RegistroPort registro, ValidadorRegistro validador, TraductorErrores traductor) {
    this.registro = registro;
    this.validador = validador;
    this.traductor = traductor;
  }

  public boolean esDemo() {
    return registro.esDemo();
  }

  public ResultadoRegistro registrar(FormularioRegistro f) {
    ErroresFormulario errores = validador.validar(f);
    if (!errores.vacio()) {
      return new Invalido(errores);
    }
    TipoCuenta tipo = f.tipoCuenta();
    List<String> camposMedios = new ArrayList<>();
    PersonaInputDTO persona = aPersona(f, camposMedios);
    UUID personaId;
    try {
      personaId = registro.crearPersona(persona).id();
    } catch (BackendException e) {
      if (e.tipo() == BackendException.Tipo.VALIDACION) {
        ErroresFormulario delBackend = erroresDelBackend(e, camposMedios);
        if (!delBackend.vacio()) {
          return new Invalido(delBackend);
        }
      }
      return new Fallido(traductor.traducir(e));
    }
    return completar(new Pendiente(personaId, tipo, nombreVisible(f)));
  }

  /** Segundo paso (alta de donante o entidad), también para reintentar después de una falla. */
  public ResultadoRegistro completar(Pendiente pendiente) {
    try {
      if (pendiente.tipo() == TipoCuenta.ENTIDAD) {
        registro.crearEntidad(pendiente.personaId());
      } else {
        registro.crearDonante(pendiente.personaId());
      }
      return new Exito(pendiente.nombre(), pendiente.tipo(), registro.esDemo());
    } catch (BackendException e) {
      return new Parcial(pendiente, traductor.traducir(e));
    }
  }

  private static String nombreVisible(FormularioRegistro f) {
    return f.tipoCuenta().esOrganizacion() ? f.getRazonSocial().strip() : f.getNombre().strip();
  }

  // ===== Formulario → DTOs del backend =====

  private static PersonaInputDTO aPersona(FormularioRegistro f, List<String> camposMedios) {
    List<MedioDeContactoInputDTO> medios = medios(f, camposMedios);
    DireccionInputDTO direccion = f.tieneDomicilio() ? direccion(f) : null;
    TipoDocumento tipoDocumento = enumOpcional(f.getTipoDocumento(), TipoDocumento.class);
    if (f.tipoCuenta().esOrganizacion()) {
      return new JuridicaInputDTO(
          TipoPersona.JURIDICA,
          tipoDocumento,
          limpio(f.getDocumento()),
          direccion,
          medios,
          limpio(f.getRazonSocial()),
          TipoJuridico.valueOf(f.getTipoJuridico()),
          limpio(f.getRubro()),
          f.getRepresentantes().stream().map(RegistroService::aRepresentante).toList());
    }
    return new HumanaInputDTO(
        TipoPersona.HUMANA,
        tipoDocumento,
        limpio(f.getDocumento()),
        direccion,
        medios,
        limpio(f.getNombre()),
        limpio(f.getApellido()),
        enumOpcional(f.getGenero(), Genero.class),
        conValor(f.getFechaNacimiento()) ? LocalDate.parse(f.getFechaNacimiento()) : null);
  }

  /** Correo siempre; teléfono y WhatsApp si tienen número. Registra el campo de cada posición. */
  private static List<MedioDeContactoInputDTO> medios(
      FormularioRegistro f, List<String> camposMedios) {
    List<MedioDeContactoInputDTO> medios = new ArrayList<>();
    medios.add(new CorreoInputDTO("CORREO".equals(f.getPreferido()), limpio(f.getCorreo())));
    camposMedios.add(ErroresFormulario.CORREO);
    Telefono tel = f.getTelefono();
    if (conValor(tel.getNumero())) {
      medios.add(
          new TelefonoInputDTO(
              "TELEFONO".equals(f.getPreferido()),
              limpio(tel.getCaracteristica()),
              limpio(tel.getArea()),
              limpio(tel.getNumero())));
      camposMedios.add("telefono.numero");
    }
    Telefono wa = f.getWhatsapp();
    if (conValor(wa.getNumero())) {
      medios.add(
          new WhatsAppInputDTO(
              "WHATSAPP".equals(f.getPreferido()),
              limpio(wa.getCaracteristica()),
              limpio(wa.getArea()),
              limpio(wa.getNumero())));
      camposMedios.add("whatsapp.numero");
    }
    return medios;
  }

  private static DireccionInputDTO direccion(FormularioRegistro f) {
    return new DireccionInputDTO(
        limpio(f.getCalle()),
        conValor(f.getAltura()) ? Integer.valueOf(f.getAltura().strip()) : null,
        conValor(f.getPiso()) ? Integer.valueOf(f.getPiso().strip()) : null,
        limpio(f.getDepartamento()),
        limpio(f.getCodigoPostal()),
        limpio(f.getLocalidad()),
        limpio(f.getProvincia()),
        limpio(f.getPais()));
  }

  private static HumanaInputDTO aRepresentante(Representante r) {
    List<MedioDeContactoInputDTO> medios =
        conValor(r.getCorreo()) ? List.of(new CorreoInputDTO(true, limpio(r.getCorreo()))) : null;
    return new HumanaInputDTO(
        TipoPersona.HUMANA,
        enumOpcional(r.getTipoDocumento(), TipoDocumento.class),
        limpio(r.getDocumento()),
        null,
        medios,
        limpio(r.getNombre()),
        limpio(r.getApellido()),
        null,
        null);
  }

  private static <E extends Enum<E>> E enumOpcional(String valor, Class<E> tipo) {
    return conValor(valor) ? Enum.valueOf(tipo, valor) : null;
  }

  // ===== Errores del backend → campos del formulario =====

  private ErroresFormulario erroresDelBackend(BackendException e, List<String> camposMedios) {
    ErroresFormulario errores = new ErroresFormulario();
    if (e.error() == null) {
      return errores;
    }
    if (e.error().errors() != null) {
      for (ErrorDeCampo campo : e.error().errors()) {
        String nuestro = campoDelFormulario(campo.field(), camposMedios);
        String mensaje = campo.message() != null ? campo.message() + "." : "Revisá este dato.";
        if (nuestro != null) {
          errores.agregar(nuestro, mensaje.replace("..", "."));
        } else {
          errores.agregarGeneral(mensaje.replace("..", "."));
        }
      }
    }
    String porCodigo = CAMPO_POR_CODIGO.get(e.codigo());
    if (porCodigo != null) {
      errores.agregar(porCodigo, traductor.traducir(e).mensaje());
    }
    return errores;
  }

  /** {@code direccion.calle → calle}, {@code mediosDeContacto[1].numero → telefono.numero}, … */
  static String campoDelFormulario(String campoBackend, List<String> camposMedios) {
    if (campoBackend == null) {
      return null;
    }
    if (campoBackend.startsWith("direccion.")) {
      return campoBackend.substring("direccion.".length());
    }
    Matcher medio = MEDIO.matcher(campoBackend);
    if (medio.matches()) {
      int i = Integer.parseInt(medio.group(1));
      return i < camposMedios.size() ? camposMedios.get(i) : ErroresFormulario.CORREO;
    }
    Matcher rep = REPRESENTANTE.matcher(campoBackend);
    if (rep.matches()) {
      String resto = rep.group(2);
      String campo = resto.startsWith("mediosDeContacto") ? ErroresFormulario.CORREO : resto;
      return "representantes[" + rep.group(1) + "]." + campo;
    }
    return switch (campoBackend) {
      case "nombre",
          "apellido",
          "tipoDocumento",
          "documento",
          "fechaNacimiento",
          "genero",
          "razonSocial",
          "tipoJuridico",
          "rubro" ->
          campoBackend;
      default -> null;
    };
  }
}
