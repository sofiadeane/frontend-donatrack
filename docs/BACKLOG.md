# Backlog

Kanban: **Backlog → Listo → En curso (WIP 2) → Revisión → Hecho**. Reglas en [`METODOLOGIA.md`](METODOLOGIA.md).
Criterios de aceptación detallados por journey (`J#`) en [`02-especificacion-funcional.md`](02-especificacion-funcional.md) §5.

## Épicas

| Épica | Funcionalidades | Journeys | Orden |
|---|---|---|---|
| E0 Fundaciones | F01, F02 | — | 1 |
| E1 Público | F03, F04, F05, F20, F21 | J1 | 2 |
| E2 Donante | F06, F07, F08, F09, F22, F23, F25 | J2, J3, J12 | 3 |
| E3 Entidad beneficiaria | F10, F11, F12, F09 | J4, J5, J12 | 4 |
| E4 Administración | F13–F19, F24, F26, F27 | J6–J11 | 5 |

Orden: fundaciones primero, luego el journey público (vidriera del design system), luego roles por dependencia de datos (admin genera donaciones, pero donante y entidad son más simples para validar el patrón de listas + detalle).

## Historias

Prioridad: **M** Must · **S** Should · **C** Could. Estado inicial: Backlog.

### E0 Fundaciones
| ID | Historia | Prio | Estado |
|---|---|---|---|
| H0.1 | Como equipo, quiero tokens y componentes base del design system para construir pantallas consistentes | M | Revisión (tokens, fuente, botones, tarjeta, cabecera, avisos, opciones, pill de estado) |
| H0.2 | Como usuaria, quiero un shell público y uno privado con navegación por rol, accesible y responsive | M | Revisión |
| H0.3 | Como usuaria, quiero ingresar eligiendo rol e identidad demo para recorrer mi panel | M | Revisión (identidades desde fixtures; desde API en H0.4) |
| H0.4 | Como equipo, quiero adapters por servicio con manejo de errores estándar para integrar sin acoplar vistas | M | Revisión (infraestructura + donaciones; el resto de servicios se suma con cada épica) |
| H0.6 | Como usuaria, quiero páginas de error propias (404, 403, 500) con la identidad de DonaTrack en lugar de la respuesta por defecto de `/error` | C | Backlog (pedido 2026-10-01, no prioritario) |
| H0.5 | Como usuaria, quiero estados de carga, vacío, error y éxito coherentes en toda la app | M | Revisión (vacío, error, cargando y demo; éxito llega con el primer formulario) |

### E1 Público
| ID | Historia | Prio | Estado |
|---|---|---|---|
| H1.1 | Como visitante, quiero entender qué es DonaTrack desde el inicio | M | Revisión |
| H1.2 | Como visitante, quiero leer la información legal y de privacidad | M | Revisión (contenido del v0 ampliado con lo que se pide y lo que se ve públicamente) |
| H1.3 | Como visitante, quiero registrarme como donante humano, jurídico o como entidad | M | Revisión (personas → donantes/entidades; errores por campo y paso parcial reintentable; apodo y consentimiento público sin backend) |
| H1.4 | Como visitante, quiero ver las donaciones destacadas del último mes con datos reales | S | Revisión (adapter real listo; donante vía `/api/donaciones/{id}`; sin foto: el backend no la trae) |
| H1.5 | Como visitante, quiero ver una galería de donaciones entregadas sin iniciar sesión | S | Revisión (24 más recientes, filtro por categoría con enlaces; sin filtro en el export estático) |

### E2 Donante
| ID | Historia | Prio | Estado |
|---|---|---|---|
| H2.1 | Como donante, quiero filtrar mis donaciones por estado y categoría/subcategoría | M | Revisión (filtros GET; subcategoría dependiente con JS; sin filtros en el export estático) |
| H2.2 | Como donante, quiero ver el historial de estados de una donación | M | Revisión (solo donaciones propias; justificación en entrega fallida) |
| H2.3 | Como donante, quiero explorar entidades beneficiarias y sus necesidades | M | Backlog |
| H2.4 | Como donante, quiero ver mi categoría, misiones e insignias | M | Revisión (`/donante/incentivos`: recorrido de categorías, misión activa, insignias ganadas/pendientes, misiones por categoría) |
| H2.5 | Como donante, quiero ver mis notificaciones | M | En curso (las 3 últimas en el inicio; falta la página completa) |
| H2.6 | Como donante, quiero seguir las entregas activas (sin mapa) | S | Backlog |
| H2.7 | Como donante, quiero una sección de Estadísticas con gráficos de mi actividad (`/metricas`); el tile "Donaciones realizadas" del inicio es su previsualización y enlaza a ella | S | Backlog |
| H2.8 | Como donante, quiero elegir qué insignias son visibles | C | Backlog |

### E3 Entidad beneficiaria
| ID | Historia | Prio | Estado |
|---|---|---|---|
| H3.1 | Como entidad, quiero registrar, editar y eliminar necesidades | M | Backlog |
| H3.2 | Como entidad, quiero ver las donaciones asignadas y su estado | M | Backlog |
| H3.3 | Como entidad, quiero confirmar la recepción o informar que no recibí una entrega | M | Backlog |
| H3.4 | Como entidad, quiero ver mis notificaciones | M | Backlog |

### E4 Administración
| ID | Historia | Prio | Estado |
|---|---|---|---|
| H4.1 | Como admin, quiero registrar donantes | M | Backlog |
| H4.2 | Como admin, quiero registrar una donación con sus ítems | M | Backlog |
| H4.3 | Como admin, quiero revisar los ítems con normalización pendiente | M | Backlog |
| H4.4 | Como admin, quiero ver el inventario y marcar donaciones vencidas | M | Backlog |
| H4.5 | Como admin, quiero ejecutar la asignación y aprobar o descartar propuestas | M | Backlog |
| H4.6 | Como admin, quiero administrar camiones | M | Backlog |
| H4.7 | Como admin, quiero ver el ranking mensual y el historial | M | Backlog |
| H4.8 | Como admin, quiero importar donantes por CSV y seguir el procesamiento | S | Backlog |
| H4.9 | Como admin, quiero registrar entidades beneficiarias | S | Backlog |
| H4.10 | Como admin, quiero ver indicadores operativos en el inicio | S | Backlog |
| H4.11 | Como admin, quiero administrar categorías, subcategorías y alias | C | Backlog |

## Diferido / TBD
W01 mapas interactivos · W02 subida real de fotos · W03 auth real · W04 choferes, rutas y planificación.
