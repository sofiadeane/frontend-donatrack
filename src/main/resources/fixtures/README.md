# Fixtures de demostración

Datos **ficticios** para trabajar sin backend (adapters en modo `fixture`). La interfaz los muestra siempre con la etiqueta "Datos de demostración".

- Cada archivo replica la forma exacta de una respuesta del backend (DTOs copiados del código de `DonaTrack/`).
- La lectura es estricta: un campo que no existe en el DTO hace fallar el test `FixturesContratoTest`.
- No usar datos personales reales ni marcas reales.

| Archivo | Endpoint que simula |
|---|---|
| `sesion/identidades.json` | Identidades del ingreso de demostración (personas, donantes y entidades) |
| `donaciones/donaciones-independientes.json` | `GET /donaciones-independientes` (cada registro agrega `donanteId`, que la respuesta real no incluye, para poder filtrar) |
| `donaciones/donaciones.json` | `GET /api/donaciones/{id}` (copia parcial del DTO: id y donante) |
| `metricas/publicas.json` | Sin endpoint en el backend (brecha G7): métricas de la landing, cifras del Figma |
