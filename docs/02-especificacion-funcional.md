# 02 · Especificación funcional y alcance del MVP (Etapa 2)

> Fecha: 2026-10-01 · Estado: **borrador para aprobación**
> Base: [`01-auditoria.md`](01-auditoria.md). Requisitos `R1–R20` = Enunciado-1 (ver auditoría §2).
> Etiquetas: **[OBS]** verificado en código · **[DOC]** enunciado/docs · **[INF]** inferencia · **[PROP]** propuesta · **[TBD]** pendiente de decisión o de backend.

## 1. Stakeholders y necesidades

| Stakeholder | Necesidad principal | Requisitos |
|---|---|---|
| Persona donante (humana/jurídica) | Saber qué pasó con lo que donó; reconocimiento | R5–R9 |
| Entidad beneficiaria | Declarar lo que necesita y coordinar la recepción | R10–R14 |
| Persona administradora (depósito) | Registrar rápido, asignar con criterio, gestionar flota | R15–R20 |
| Visitante público | Confiar en la plataforma antes de sumarse | R1–R4 |
| Cátedra | Cumplir R1–R20 y la arquitectura de la E5 (MVC/SSR) | todos |
| Equipo | Código portable al stack que pida la E5 | — |

## 2. Principios de la especificación

1. **El backend define el vocabulario.** Estados, categorías y campos usan los valores del código (auditoría §3). Las etiquetas de UI son traducciones de esos valores, no estados nuevos.
2. **Todo dato que la UI muestre tiene origen declarado:** API (endpoint), derivado (calculado por la capa de orquestación a partir de endpoints existentes) o fixture (marcado visualmente como demo).
3. **Consistencia eventual visible.** Las transiciones que dependen de eventos AMQP (p. ej. confirmar recepción → `entrega.exitosa` → donación `ENTREGADA`) se muestran como "Actualizando…" y no como éxito inmediato del estado derivado [OBS `donaciones-service/.../LogisticaEventListener.java`].
4. **Sesión demo.** Hasta la E6, el ingreso elige rol e identidad existente (persona/donante/entidad) desde la API; la UI lo indica siempre.

## 3. Navegación e inventario de páginas

Se fusionan pantallas del v0 cuando reduce pasos o duplicación (marcado **⇢**).

### Público
| Página | Contenido | Req | Cambio vs v0 |
|---|---|---|---|
| Inicio | Hero, Cómo funciona, Destacadas del último mes, Transparencia en números, CTA, footer | R1 | Identidad Figma; "Nosotros" del sitemap E1 ⇢ sección del inicio |
| Donaciones entregadas | Galería pública con fotos, filtro por categoría | R1.i | Nueva (v0 solo ancla `/#donaciones` rota) |
| Mapa de impacto | Placeholder con estado "Próximamente" + listado de entregas | R2 | **[TBD]** mapa |
| Privacidad y legal | Contenido legal | R1.iii | Igual |
| Ingresar | Login demo con selector de rol | R4 | Igual al v0 + selección de identidad |
| Registrarse | Selector Humana / Jurídica / Entidad → formulario | R3 | Igual al v0, campos alineados al DTO |

### Persona donante
| Página | Contenido | Req |
|---|---|---|
| Inicio | Resumen: categoría, misión activa, últimas donaciones, notificaciones recientes | R7, R8 |
| Mis donaciones | Lista filtrable ⇢ **detalle con historial de estados** (trazabilidad) y entrega asociada | R5, R9 |
| Entidades beneficiarias | Listado ⇢ detalle con necesidades activas | R6 |
| Incentivos | Categoría, misiones con progreso, insignias (visibilidad), métricas | R7 |
| Notificaciones | Lista cronológica | R8 |
| Seguimiento | Entregas activas (sin mapa) + enlace `urlSeguimiento` | R9 **[TBD mapa]** |

### Entidad beneficiaria
| Página | Contenido | Req |
|---|---|---|
| Inicio | Necesidades activas con cobertura, donaciones próximas, notificaciones | R10–R13 |
| Necesidades | Lista + alta/edición/baja | R10 |
| Donaciones asignadas | Lista ⇢ detalle con **acción "Confirmar recepción"** (se elimina la página separada del v0) | R11, R12 |
| Seguimiento | Igual que donante | R14 **[TBD mapa]** |
| Notificaciones | Lista cronológica | R13 |

### Persona administradora
| Página | Contenido | Req |
|---|---|---|
| Inicio | Indicadores operativos + accesos rápidos + top 3 del ranking | R19 |
| Donantes | Lista ⇢ alta (humana/jurídica) ⇢ **Importar CSV** como acción de la lista | R15, R20 |
| Entidades | Lista ⇢ alta (falta en v0; está en el sitemap E1) | R3 |
| Donaciones | **Registrar donación** · **Revisión de normalización** (nueva, ver §5.3) · Inventario en depósito con "Marcar vencida" | R15, R16 |
| Asignaciones | Ejecutar algoritmos · revisar propuestas · aprobar/descartar | R17 |
| Camiones | Lista ⇢ alta ⇢ cambio de estado ⇢ baja | R18 |
| Rankings | Ranking del mes + historial por período | R19 |

## 4. Priorización MoSCoW del MVP

Criterio: **Must** = cumple un requisito de cátedra y el backend lo soporta hoy; **Should** = valor alto pero depende de datos derivados o de una brecha menor; **Could** = mejora; **Won't (MVP)** = depende de una capacidad inexistente en backend o de la E6.

| ID | Funcionalidad | Prioridad | Req | Dependencia / justificación |
|---|---|---|---|---|
| F01 | Shell público y privado, navegación por rol, estados de UI base | Must | — | Base de todos los journeys |
| F02 | Sesión demo (rol + identidad) y guardas de UI por rol | Must | R4 | Sin auth hasta E6 |
| F03 | Inicio público (sin mapa) | Must | R1 | Métricas derivadas de listas (F03b Should) |
| F04 | Privacidad y legal | Must | R1.iii | Contenido estático |
| F05 | Registro humana / jurídica / entidad | Must | R3 | `POST /api/personas` → `/api/donantes` o `/api/entidades` |
| F06 | Mis donaciones: filtros estado + categoría/subcategoría, detalle con historial | Must | R5 | `GET /donaciones-independientes?donanteId&estado&subcategoriaId` |
| F07 | Explorar entidades | Must | R6 | `GET /api/entidades` + `GET /api/necesidades?entidadId` |
| F08 | Incentivos: categoría, misiones, insignias | Must | R7 | Endpoints de incentivos |
| F09 | Notificaciones (donante y entidad) | Must | R8, R13 | `GET /api/notificaciones/persona/{personaId}` |
| F10 | Necesidades: alta, edición, baja, cobertura | Must | R10 | CRUD `/api/necesidades` |
| F11 | Donaciones asignadas a la entidad | Must | R11 | Derivado: necesidades de la entidad → propuestas aprobadas → donaciones (ver §5.2) |
| F12 | Confirmar recepción / informar no recepción | Must | R12 | `PATCH /api/entregas/{id}/estado`; fotos en F25 |
| F13 | Registrar donante (admin) | Must | R15 | Igual a F05 |
| F14 | Registrar donación en depósito con ítems | Must | R15 | `POST /api/donaciones` |
| F15 | Revisión de normalización de ítems | Must | R15 | Sin esto la donación queda `CARGADA` [OBS] |
| F16 | Inventario en depósito + marcar vencida | Must | R16 | `PATCH /donaciones-independientes/{id}/estado` |
| F17 | Asignaciones: ejecutar, revisar, aprobar/descartar | Must | R17 | `/api/asignaciones/*` |
| F18 | Camiones: alta, estado, baja | Must | R18 | `/api/camiones` |
| F19 | Ranking mensual e historial | Must | R19 | `/api/incentivos/ranking/*` |
| F20 | Destacadas del último mes desde API | Should | R1 | Sin filtro por fecha: se filtra en la capa de orquestación |
| F21 | Galería pública de entregadas con fotos | Should | R1.i | Fotos = URL en `Entrega`; sin subida real |
| F22 | Seguimiento sin mapa (entregas activas + enlace) | Should | R9, R14 | Requiere cruzar donación↔entrega (§5.2) |
| F23 | Métricas de donante y panel admin | Should | — | `/metricas`, `/admin/resumen` |
| F24 | Importar CSV (UI + seguimiento de estado) | Should | R20 | Backend espera ruta en servidor; UI con adapter |
| F25 | Visibilidad de insignias | Could | R7 | `PUT .../visibilidad` |
| F26 | Catálogo de categorías/subcategorías/alias | Could | — | CRUD disponible |
| F27 | Gestión de entidades por admin | Should | R3 | Mismos endpoints que F05 |
| W01 | Mapas interactivos (impacto y seguimiento) | Won't (MVP) **[TBD]** | R2, R9, R14 | Sin coordenadas ni posición de camión |
| W02 | Subida real de fotos | Won't (MVP) **[TBD]** | R12, R1.i | Sin multipart |
| W03 | Auth real, recuperación de contraseña | Won't (MVP) | R4 | E6 |
| W04 | Choferes, rutas y planificación | Won't (MVP) | — | No es requisito de UI de la cátedra |

**Rationale del corte:** los 17 Must cubren 18 de los 20 requisitos con el backend actual; R2 y R9/R14 quedan parciales (listado sin mapa) y R12 sin fotos reales. Las Should no bloquean journeys completos.

## 5. Journeys del MVP con criterios de aceptación

Formato: precondición · pasos · estados de UI · errores · dependencias. Los criterios (CA) son verificables en QA.

### Estados de UI comunes (aplican a toda vista con datos)
- **Cargando:** esqueleto o indicador con texto accesible (`aria-busy`).
- **Vacío:** mensaje + acción siguiente (p. ej. "Todavía no registraste necesidades · Registrar necesidad").
- **Error de servicio:** mensaje comprensible traducido desde `ErrorResponse.code`, opción de reintentar, el resto de la página sigue usable si otro servicio responde.
- **Éxito:** confirmación anunciada (`role="status"`) y foco gestionado.
- **Demo:** cualquier dato de fixture lleva la etiqueta "Dato de demostración".

### J1 · Visitante conoce la plataforma y se registra (R1, R3)
- Pasos: Inicio → CTA "Quiero donar" / "Soy entidad beneficiaria" → Registrarse con tipo preseleccionado → formulario → confirmación → Ingresar.
- Campos [OBS DTO]: Humana `nombre*, apellido*, tipoDocumento, documento, fechaNacimiento, genero, direccion, mediosDeContacto (correo obligatorio; teléfono/WhatsApp opcionales; uno predeterminado)`; Jurídica `razonSocial*, tipoJuridico* (GUBERNAMENTAL|ONG|EMPRESA|INSTITUCION), rubro, mediosDeContacto`. Entidad = Jurídica + `POST /api/entidades {juridicaId}`.
- CA1: el tipo elegido en el CTA llega preseleccionado al formulario.
- CA2: errores de validación se muestran por campo, vinculados con `aria-describedby`, y el foco va al primer error.
- CA3: si el segundo paso (`/donantes` o `/entidades`) falla tras crear la persona, la UI informa el estado parcial y permite reintentar solo ese paso.
- CA4: el formulario es usable con teclado y en 360 px.

### J2 · Donante sigue sus donaciones (R5, R9)
- Pasos: Mis donaciones → filtrar por estado y categoría→subcategoría → abrir detalle → ver historial de estados con fecha y justificación.
- CA1: los filtros de estado listan exactamente los valores de `TipoEstadoDonacion` con etiqueta en español.
- CA2: la subcategoría depende de la categoría elegida; limpiar filtros restaura la lista completa.
- CA3: el contador de resultados se actualiza y se anuncia.
- CA4: el detalle muestra el historial en orden cronológico; `ENTREGA_FALLIDA` muestra la justificación.
- CA5 (Should F22): si hay entrega activa, se muestra estado de entrega y enlace de seguimiento; si `urlSeguimiento` es nulo, se explica que la ruta aún no inició.

### J3 · Donante consulta incentivos (R7)
- CA1: muestra la categoría (`COLABORADOR|SOSTENEDOR|TRANSFORMADOR`) y la misión activa con progreso y distancia al objetivo.
- CA2: insignias obtenidas vs. pendientes diferenciadas por algo más que color.
- CA3: si el donante no existe en incentivos (404), se muestra estado vacío explicativo, no un error genérico.

### J4 · Entidad registra una necesidad (R10)
- Campos [OBS]: `tipo* (RECURRENTE|EXTRAORDINARIA), idSubcategoria*, cantidadNecesitada* > 0, descripcion*, fechaInicio, fechaFin`.
- Regla [OBS `NecesidadesService.java:88`]: en una recurrente el período es `fechaFin − fechaInicio`. **[PROP]** la UI pide "Frecuencia" (semanal, mensual, etc.) y calcula `fechaFin`.
- CA1: el formulario cambia de ayuda contextual según el tipo.
- CA2: la lista muestra cobertura y `estaSatisfecha` sin inventar estados intermedios.
- CA3: editar y eliminar piden confirmación; eliminar informa si falla por estado.

### J5 · Entidad revisa asignaciones y confirma recepción (R11, R12)
- Pasos: Donaciones asignadas → detalle → "Confirmar recepción" (o "No la recibí") → confirmación.
- Llamada [OBS]: `PATCH /api/entregas/{id}/estado {estado: ENTREGADA | NO_RECIBIDA, actor, justificacion?}`.
- CA1: la acción solo aparece si la entrega está `EN_TRASLADO`.
- CA2: "No la recibí" exige justificación.
- CA3: tras confirmar, la entrega pasa a "Entregada" y la donación muestra "Actualizando…" hasta que el estado derivado cambie (consistencia eventual).
- CA4: las fotos se muestran como **[TBD]** con explicación; no se simula una subida que no ocurre.

### J6 · Admin registra una donación recibida (R15)
- Pasos: Registrar donación → elegir donante (o crearlo sin salir del flujo) → descripción + depósito + fecha → agregar ítems → enviar → ver resultado de normalización.
- Ítem [OBS]: `descripcionBien*, cantidad*, fechaVencimiento?, estadoBien? (NUEVO|USADO|DESGASTADO), pesoUnitario?, volumenUnitario?, fotoUrl?`.
- CA1: no se puede enviar sin al menos un ítem válido.
- CA2: el resultado indica si la donación quedó `SEGMENTADA` o si tiene ítems en `PENDIENTE_REVISION`, con enlace a la revisión.
- CA3: "Guardar y cargar otra" conserva depósito y fecha.

### J7 · Admin revisa normalización (R15)
- Llamadas [OBS]: `GET /api/items-normalizados/pendientes` → `PATCH /api/items-normalizados/{id} {estadoNormalizacion, subcategoriaId}`.
- CA1: cada ítem muestra descripción original, subcategoría sugerida y confianza.
- CA2: aceptar o rechazar, o corregir la subcategoría antes de aceptar.

### J8 · Admin gestiona el inventario (R16)
- CA1: la lista filtra por estado; por defecto `EN_DEPOSITO`.
- CA2: "Marcar vencida" solo en `EN_DEPOSITO`, con confirmación; envía `X-Actor` de la sesión.
- CA3: una transición inválida (409) se explica en lenguaje claro.

### J9 · Admin asigna donaciones (R17)
- Pasos: Ejecutar algoritmos → lista de propuestas `PENDIENTE` → ver necesidad y fragmentaciones → Aprobar / Descartar (con justificación).
- CA1: la ejecución muestra progreso y resultado (cantidad de propuestas); historial de ejecuciones disponible.
- CA2: aprobar o descartar actualiza la lista sin recargar toda la vista.
- CA3: si no hay propuestas, el estado vacío explica las condiciones (donaciones en depósito y necesidades activas).

### J10 · Admin gestiona camiones (R18)
- Campos [OBS]: `patente*, capacidadVolumen*, altura*, capacidadKG*`; estados `DISPONIBLE|EN_RUTA|DESHABILITADO` (+ `motivo`).
- CA1: validación de patente con mensaje del backend traducido.
- CA2: cambio de estado con confirmación; `EN_RUTA` se muestra pero no es seleccionable manualmente **[INF — validar]**.

### J11 · Admin consulta rankings (R19)
- CA1: ranking del último período con podio de 3; selector de período del historial.
- CA2: respuesta 204 → estado vacío "Todavía no hay ranking para este período".

### J12 · Notificaciones (R8, R13)
- CA1: lista cronológica con fecha relativa y absoluta accesible.
- CA2: sin indicador "no leída" (el backend no lo soporta) **[TBD]**.

## 6. Reglas de negocio faltantes o ambiguas (no se inventan)

| # | Tema | Situación | Propuesta |
|---|---|---|---|
| B1 | ¿Quién registra entidades? | R3 = autoregistro; sitemap E1 = también admin | Ambos (F05 y F27) |
| B2 | Edad vs. fecha de nacimiento | Enunciado pide edad; DTO usa `fechaNacimiento` | Usar fecha de nacimiento |
| B3 | Criterio de "destacadas" | No definido | Últimas N `ENTREGADA` del mes **[PROP]** |
| B4 | Métricas públicas | Sin endpoint | Derivar o usar fixture etiquetado |
| B5 | Identidad de sesión | `personaId` / `donanteId` / `entidadId` distintos | La sesión demo guarda los tres |
| B6 | Representantes de jurídica | En el DTO, sin UI definida | Diferido (Could) |
| B7 | Transiciones manuales de camión | No documentadas | Validar con backend |
| B8 | Quién revisa normalización | No documentado | Admin |

## 7. Brechas de API para el equipo de backend

| ID | Brecha | Afecta | Severidad |
|---|---|---|---|
| G1 | CORS / gateway (o confirmar BFF) | Todo | Alta |
| G2 | Autenticación y roles | R4, guardas | Alta (E6) |
| G3 | Filtros: donaciones por entidad; entregas por beneficiaria y por donación; donaciones por fecha | R1, R9, R11, R14 | Alta |
| G4 | Coordenadas en direcciones y posición de camión | R2, R9, R14 | Alta |
| G5 | Subida multipart de fotos | R12, R1.i | Media |
| G6 | CSV por multipart + errores por fila (`ResultadoCargaDTO`) | R20 | Media |
| G7 | Métricas públicas agregadas | R1 | Baja |
| G8 | Marcar notificación como leída | R8, R13 | Baja |
| G9 | Paginación | Listas grandes | Media |
| G10 | Corregir drift OpenAPI ↔ DTOs | Integración | Media |
| G11 | Errores con cuerpo estándar en notificaciones | Manejo de errores | Baja |

## 8. Decisiones que requieren aprobación

1. Alcance MVP = F01–F19 (Must), con F20–F24 y F27 como Should.
2. Fusiones de navegación: confirmar recepción dentro del detalle; Importar CSV dentro de Donantes; "Nosotros" como sección del inicio; nueva página Revisión de normalización; nueva página Entidades (admin).
3. Mapas y fotos como TBD visibles en la UI (no simulados).
4. Usar el vocabulario del backend y descartar el del v0 (niveles, estados de camión, prioridades, zonas, puntos).
