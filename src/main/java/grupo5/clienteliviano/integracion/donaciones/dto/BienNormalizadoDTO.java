package grupo5.clienteliviano.integracion.donaciones.dto;

/** Copia de {@code donaciones-service/.../dto/donacionesIndependientes/BienNormalizadoDTO}. */
public record BienNormalizadoDTO(
    BienResumenDTO bien, SubcategoriaResumenDTO subcategoria, CategoriaResumenDTO categoria) {}
