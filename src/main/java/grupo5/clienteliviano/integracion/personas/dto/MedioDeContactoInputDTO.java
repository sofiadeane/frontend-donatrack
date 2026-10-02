package grupo5.clienteliviano.integracion.personas.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** Copia de {@code MedioDeContactoInputDTO}: polimórfico por la propiedad {@code tipo}. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
  @JsonSubTypes.Type(value = MedioDeContactoInputDTO.CorreoInputDTO.class, name = "CORREO"),
  @JsonSubTypes.Type(value = MedioDeContactoInputDTO.TelefonoInputDTO.class, name = "TELEFONO"),
  @JsonSubTypes.Type(value = MedioDeContactoInputDTO.WhatsAppInputDTO.class, name = "WHATSAPP")
})
public sealed interface MedioDeContactoInputDTO {
  Boolean esPredeterminado();

  /** Copia de {@code CorreoInputDTO}. */
  record CorreoInputDTO(Boolean esPredeterminado, String direccionCorreo)
      implements MedioDeContactoInputDTO {}

  /** Copia de {@code TelefonoInputDTO}. */
  record TelefonoInputDTO(
      Boolean esPredeterminado, String caracteristica, String codigoArea, String numero)
      implements MedioDeContactoInputDTO {}

  /** Copia de {@code WhatsAppInputDTO}. */
  record WhatsAppInputDTO(
      Boolean esPredeterminado, String caracteristica, String codigoArea, String numero)
      implements MedioDeContactoInputDTO {}
}
