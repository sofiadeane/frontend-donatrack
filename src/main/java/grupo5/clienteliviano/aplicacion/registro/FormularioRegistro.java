package grupo5.clienteliviano.aplicacion.registro;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Lo que la persona escribe en el formulario de registro, tal como llega (texto). Se valida en
 * {@link ValidadorRegistro} y se convierte a los DTOs del backend en {@link RegistroService}.
 *
 * <p>{@code mostrarEnDestacadas} y {@code apodo} no existen todavía en el backend: se piden para
 * conversarlo con el equipo y no se envían.
 */
public class FormularioRegistro {

  private String tipo = TipoCuenta.HUMANA.parametro();
  private String nombre;
  private String apellido;
  private String tipoDocumento;
  private String documento;
  private String fechaNacimiento;
  private String genero;
  private String razonSocial;
  private String tipoJuridico;
  private String rubro;
  private List<Representante> representantes = new ArrayList<>(List.of(new Representante()));
  private String correo;
  private Telefono telefono = new Telefono();
  private Telefono whatsapp = new Telefono();
  private String preferido = "CORREO";
  private String calle;
  private String altura;
  private String piso;
  private String departamento;
  private String codigoPostal;
  private String localidad;
  private String provincia;
  private String pais = "Argentina";
  private boolean mostrarEnDestacadas;
  private String apodo;
  private boolean aceptaPrivacidad;

  public TipoCuenta tipoCuenta() {
    return TipoCuenta.desde(tipo).orElse(TipoCuenta.HUMANA);
  }

  /** Hay algún dato de domicilio cargado (país solo no cuenta: viene precargado). */
  public boolean tieneDomicilio() {
    return Stream.of(calle, altura, piso, departamento, codigoPostal, localidad, provincia)
        .anyMatch(Texto::conValor);
  }

  /** Número de teléfono (con código de área y de país) separado en tres campos. */
  public static class Telefono {
    private String caracteristica = "+54";
    private String area;
    private String numero;

    public String getCaracteristica() {
      return caracteristica;
    }

    public void setCaracteristica(String caracteristica) {
      this.caracteristica = caracteristica;
    }

    public String getArea() {
      return area;
    }

    public void setArea(String area) {
      this.area = area;
    }

    public String getNumero() {
      return numero;
    }

    public void setNumero(String numero) {
      this.numero = numero;
    }
  }

  /** Persona humana que representa a una organización. */
  public static class Representante {
    private String nombre;
    private String apellido;
    private String tipoDocumento;
    private String documento;
    private String correo;

    public String getNombre() {
      return nombre;
    }

    public void setNombre(String nombre) {
      this.nombre = nombre;
    }

    public String getApellido() {
      return apellido;
    }

    public void setApellido(String apellido) {
      this.apellido = apellido;
    }

    public String getTipoDocumento() {
      return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
      this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
      return documento;
    }

    public void setDocumento(String documento) {
      this.documento = documento;
    }

    public String getCorreo() {
      return correo;
    }

    public void setCorreo(String correo) {
      this.correo = correo;
    }
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getApellido() {
    return apellido;
  }

  public void setApellido(String apellido) {
    this.apellido = apellido;
  }

  public String getTipoDocumento() {
    return tipoDocumento;
  }

  public void setTipoDocumento(String tipoDocumento) {
    this.tipoDocumento = tipoDocumento;
  }

  public String getDocumento() {
    return documento;
  }

  public void setDocumento(String documento) {
    this.documento = documento;
  }

  public String getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(String fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }

  public String getGenero() {
    return genero;
  }

  public void setGenero(String genero) {
    this.genero = genero;
  }

  public String getRazonSocial() {
    return razonSocial;
  }

  public void setRazonSocial(String razonSocial) {
    this.razonSocial = razonSocial;
  }

  public String getTipoJuridico() {
    return tipoJuridico;
  }

  public void setTipoJuridico(String tipoJuridico) {
    this.tipoJuridico = tipoJuridico;
  }

  public String getRubro() {
    return rubro;
  }

  public void setRubro(String rubro) {
    this.rubro = rubro;
  }

  public List<Representante> getRepresentantes() {
    return representantes;
  }

  public void setRepresentantes(List<Representante> representantes) {
    this.representantes = representantes;
  }

  public String getCorreo() {
    return correo;
  }

  public void setCorreo(String correo) {
    this.correo = correo;
  }

  public Telefono getTelefono() {
    return telefono;
  }

  public void setTelefono(Telefono telefono) {
    this.telefono = telefono;
  }

  public Telefono getWhatsapp() {
    return whatsapp;
  }

  public void setWhatsapp(Telefono whatsapp) {
    this.whatsapp = whatsapp;
  }

  public String getPreferido() {
    return preferido;
  }

  public void setPreferido(String preferido) {
    this.preferido = preferido;
  }

  public String getCalle() {
    return calle;
  }

  public void setCalle(String calle) {
    this.calle = calle;
  }

  public String getAltura() {
    return altura;
  }

  public void setAltura(String altura) {
    this.altura = altura;
  }

  public String getPiso() {
    return piso;
  }

  public void setPiso(String piso) {
    this.piso = piso;
  }

  public String getDepartamento() {
    return departamento;
  }

  public void setDepartamento(String departamento) {
    this.departamento = departamento;
  }

  public String getCodigoPostal() {
    return codigoPostal;
  }

  public void setCodigoPostal(String codigoPostal) {
    this.codigoPostal = codigoPostal;
  }

  public String getLocalidad() {
    return localidad;
  }

  public void setLocalidad(String localidad) {
    this.localidad = localidad;
  }

  public String getProvincia() {
    return provincia;
  }

  public void setProvincia(String provincia) {
    this.provincia = provincia;
  }

  public String getPais() {
    return pais;
  }

  public void setPais(String pais) {
    this.pais = pais;
  }

  public boolean isMostrarEnDestacadas() {
    return mostrarEnDestacadas;
  }

  public void setMostrarEnDestacadas(boolean mostrarEnDestacadas) {
    this.mostrarEnDestacadas = mostrarEnDestacadas;
  }

  public String getApodo() {
    return apodo;
  }

  public void setApodo(String apodo) {
    this.apodo = apodo;
  }

  public boolean isAceptaPrivacidad() {
    return aceptaPrivacidad;
  }

  public void setAceptaPrivacidad(boolean aceptaPrivacidad) {
    this.aceptaPrivacidad = aceptaPrivacidad;
  }
}
