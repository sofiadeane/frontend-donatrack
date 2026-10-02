package grupo5.clienteliviano.integracion.personas.dto;

/** Copia de {@code DireccionInputDTO}: calle, localidad, provincia y país son obligatorios. */
public record DireccionInputDTO(
    String calle,
    Integer altura,
    Integer piso,
    String departamento,
    String codigoPostal,
    String localidad,
    String provincia,
    String pais) {}
