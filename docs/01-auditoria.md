# 01 · Auditoría inicial (Etapa 1)

> Fecha: 2026-10-01 · Estado: **cerrada, pendiente de aprobación final**
> Etiquetas: **[OBS]** verificado en código/archivo · **[DOC]** documentado · **[INF]** inferencia · **[PROP]** propuesta

## 1. Fuentes y jerarquía

| Fuente | Uso | Confiabilidad |
|---|---|---|
| Enunciados `DonaTrack/docs/entregas/1/Enunciado-1.pdf`, `4/Enunciado-4.pdf` | Requisitos mínimos de UI/UX (R1–R20), dominio, estados | Máxima (mandatorio) |
| Código `DonaTrack/` (rama `E4_donaciones_comunicaciones`, HEAD `9d80fab8`, 2026-09-19) | Contratos HTTP reales | Fuente de verdad de backend |
| Figma `4sjPaFoYE09TCAValmYhaU`, página **Prototipo v2** (`334:925`) | Identidad visual de la landing | Referencia visual principal |
| v0 desplegado `https://donatrack-frontend-mvp.v0.build/` + `FrontEnd/prototipo_v0/` | Estructura de pantallas y flujos | Referencia estructural (no de datos ni vocabulario) |
| `docs/entregas/1/interfaz/Mapa del Sitio.pdf`, `DonaTrack-Bocetos.pdf` | Navegación presentada en E1 | Referencia |
| `study/` | Dominio y arquitectura | Parcialmente desactualizado (ver §7) |

`disenioFigma.html` **no** es confiable como referencia visual: perdió tipografías, rellenos, semántica y responsive [OBS]. Usar el archivo Figma vía MCP.

## 2. Requisitos de cátedra (Enunciado-1, "Requerimientos mínimos de UI/UX")

| # | Requisito | Rol |
|---|---|---|
| R1 | Landing pública: propósito, donaciones destacadas del último mes, links a fotos de entregas sin login, registro/login, legal/privacidad | Público |
| R2 | Mapa interactivo de donaciones entregadas, detalle al clic en marcador | Público |
| R3 | Registro de donantes y entidades beneficiarias | Público |
| R4 | Login de donantes, entidades y administradores | Público |
| R5 | Filtrar donaciones propias por estado y categoría/subcategoría | Donante |
| R6 | Navegar entidades beneficiarias | Donante |
| R7 | Incentivos: misiones e insignias (persona humana) | Donante |
| R8 | Notificaciones: asignación, misión cumplida, ascenso de categoría, recepción | Donante |
| R9 | Mapa de entregas activas con camión, ubicación y última actualización | Donante |
| R10 | Registrar necesidades materiales | Beneficiaria |
| R11 | Ver estado de donaciones asignadas | Beneficiaria |
| R12 | Confirmar recepción cargando fotos | Beneficiaria |
| R13 | Notificaciones de asignación y confirmación | Beneficiaria |
| R14 | Mapa de entregas activas (igual a R9) | Beneficiaria |
| R15 | Registrar donantes y donaciones recibidas en depósito | Admin |
| R16 | Marcar donaciones vencidas | Admin |
| R17 | Elegir entidad final a partir de los algoritmos de asignación | Admin |
| R18 | Administrar camiones | Admin |
| R19 | Ranking mensual e historial | Admin |
| R20 | Importar donantes por CSV (>10.000 filas) | Admin |

Contexto de entregas [DOC]: E5 "Arquitectura Web MVC" (semana del 19/10/2026); E6 "Despliegue, Observabilidad y Seguridad" (23/11). El Enunciado-1 anuncia *"un componente de front para server-side rendering (se explicará en la entrega V)"* y el diagrama de despliegue (Fig. 1) ubica un **"Servidor Frontend — FrontEnd y Orquestación"** que consume por HTTP/REST los cuatro servicios y el de autenticación.

## 3. Backend: lo que el frontend puede consumir

**Transversal [OBS]**
- 98 endpoints REST; rutas coinciden con `docs/generated/endpoints-catalog.md`. Puertos: donaciones 8080, notificaciones 8081, incentivos 8082, logística 8083.
- **Sin autenticación ni roles** (no hay Spring Security). Actor = texto libre (`X-Actor` en `PATCH /donaciones-independientes/{id}/estado`; campo `actor` en logística). Admin sembrado: `AdminConstantes.ID_ADMIN`.
- **Sin CORS, sin gateway, sin paginación.** IDs UUID; fechas `LocalDateTime` sin zona.
- Persistencia **en memoria** en donaciones, incentivos y logística (se pierde al reiniciar); Postgres solo en notificaciones.
- Error estándar `{code, type, details, traceId, timestamp, errors[]}` (`common-lib/.../ErrorResponse.java`); los mensajes son códigos de máquina y el texto para el usuario es responsabilidad del frontend (`docs/arquitectura/shared-kernel.md`). Excepción: notificaciones devuelve 400/404 sin cuerpo.
- OpenAPI con **drift de campos/enums** (p. ej. `DonacionInputDTO`, estados de camión/ruta). Fuente de verdad para el front: DTOs del código.

**Cobertura de requisitos**

| Req | Endpoints existentes | Brecha |
|---|---|---|
| R1 destacadas | `GET /donaciones-independientes?estado=ENTREGADA` | Sin filtro por fecha ni endpoint público de métricas; fotos solo como URL en `Entrega.fotoRecepcionUrl` |
| R2, R9, R14 mapas | `GET /api/rutas`, `urlSeguimiento` | **Sin coordenadas ni posición de camión** → TBD |
| R3 registro | `POST /api/personas` → `POST /api/donantes` / `POST /api/entidades` | Dos pasos, sin credenciales |
| R4 login | — | **No existe** (auth en E6) |
| R5 | `GET /donaciones-independientes?donanteId&estado&subcategoriaId` | Categoría se filtra en cliente/servidor front |
| R6 | `GET /api/entidades` | Sin descripción/zona/necesidades agregadas |
| R7 | `GET /api/incentivos/donantes/{id}/misiones`, `/insignias`, `/metricas` | OK |
| R8, R13 | `GET /api/notificaciones/persona/{personaId}` | Clave = `personaId`; sin leído/no leído |
| R10 | CRUD `/api/necesidades` | Sin estados intermedios ni prioridad |
| R11 | `GET /donaciones-independientes` + `GET /api/entregas` | Vínculo donación↔entidad vía necesidad/propuesta |
| R12 | `PATCH /api/entregas/{id}/estado` (ENTREGADA) + `PATCH /{id}/fotos {fotoRecepcionUrl}` | **Sin subida de archivos** |
| R15 | `POST /api/personas`, `/api/donantes`, `/api/donaciones` | Normalización puede dejar ítems en `PENDIENTE_REVISION` |
| R16 | `PATCH /donaciones-independientes/{id}/estado` (VENCIDA) | OK |
| R17 | `POST/GET /api/asignaciones/ejecuciones`, `GET /propuestas`, `PUT /propuestas/{id}/estado` | OK |
| R18 | CRUD `/api/camiones`, `PATCH /{id}/estado` | OK |
| R19 | `/api/incentivos/ranking/ultimo`, `/historial`, `/{periodo}` | OK (204 si vacío) |
| R20 | `POST /api/donantes/archivos {path}` + `GET /archivos/{id}` | Recibe **ruta en servidor**, no multipart; no expone errores por fila |

**Vocabulario canónico [OBS]**
- Donación independiente: `EN_DEPOSITO → ASIGNACION_REALIZADA → LISTA_PARA_ENTREGAR → EN_TRASLADO → ENTREGADA | ENTREGA_FALLIDA`; `EN_DEPOSITO → VENCIDA`; `ENTREGA_FALLIDA → EN_DEPOSITO | ASIGNACION_REALIZADA`. Donación original: `CARGADA → NORMALIZADA → SEGMENTADA`.
- Entrega: `PENDIENTE, EN_TRASLADO, ENTREGADA, NO_RECIBIDA, REVISION`. Ruta: `PENDIENTE, EN_TRASLADO, COMPLETADA`.
- Camión/chofer: `DISPONIBLE, EN_RUTA, DESHABILITADO`.
- Categoría de donante: `COLABORADOR, SOSTENEDOR, TRANSFORMADOR` (`incentivos-service/.../CategoriaDonante.java`).
- Necesidad: `RECURRENTE` (con períodos) / `EXTRAORDINARIA`; `estaSatisfecha`.
- Propuesta: `PENDIENTE, APROBADA, DESCARTADA` (máx. 10 por ejecución; dos algoritmos con consolidación).
- Unidades: `UNIDADES, KILOGRAMO, LITROS, METRO, METRO_CUADRADO, METRO_CUBICO`.

## 4. Diseño: Figma (Prototipo v2)

[OBS] vía MCP (`get_metadata`, `get_variable_defs`, `get_screenshot` sobre `480:1914` y `493:2914`):
- Secciones: navbar blanca · hero oscuro (gradiente `#163a3a`/`#1d4d4d`, curvas topográficas blancas al 10%, ruta punteada con nodos verdes brillantes y *chips* glass con métricas) · panel blanco superpuesto "Cómo funciona" (4 pasos) + CTA "Ver mapa de impacto" · banda verde "Donaciones destacadas del último mes" (carrusel de tarjetas) · "Transparencia en números" (círculos turquesa) · CTA final con gradiente verde→turquesa · footer.
- Componente "Cómo funciona" con variante horizontal y variante zigzag (base del responsive).
- Variables: `Green[P] #50d794`, `spring green #54da8e`, `Ink[P] #292928`, `cyan/16 #163a3a`, `cyan/21 #1d4d4d`, `cyan/87 #cfefe7`, `grey/95 #eafbf3`, `grey/97 #f4f6fc`; tipografía declarada **DM Sans** (300–900); radios 5 y 999.
- [INF] Títulos de sección parecen usar una geométrica tipo Montserrat y "Ver Donaciones" una tercera fuente → validar con `get_design_context` en Etapa 3.
- **Problemas:** contraste blanco sobre `#50d794` ≈ 1,9:1 y títulos verdes sobre blanco (fallan AA); no hay sección "Mapa de impacto" (R2); datos de ejemplo con marcas reales (Fundación IRSA, Supermercados Disco) → reemplazar por ficticias; solo existe la landing (no hay pantallas privadas en Figma).

## 5. v0 (estructura)

[OBS] Next.js App Router + Tailwind v4 + shadcn + lucide + Geist. Visualmente genérico (verde shadcn claro), sin la identidad del Figma.
- Inventario: público (landing, privacidad, login con selector de rol demo, registro humana/jurídica/entidad); admin (dashboard, donantes, registrar donación, donaciones, asignaciones, camiones, rankings, importar CSV); beneficiaria (dashboard, necesidades, donaciones asignadas, confirmar recepción, notificaciones, seguimiento); donante (dashboard, donaciones, entidades, incentivos, notificaciones, seguimiento).
- Patrones reutilizables: shell privado (sidebar + topbar), stat cards, tablas con filtros + contador de resultados, pills de estado, listas de notificaciones con tabs, barras de progreso, stepper.
- **Problemas:** export local `donante/dashboard.html` = copia de `privacidad.html` (el desplegado sí existe); 4 pantallas admin con `h1` vacío, sin tildes y tuteo; datos contradictorios entre roles (DN-1043, camiones, conteos); vocabulario inventado vs backend (niveles Inicial/Bronce/Plata/Oro y escalera de 5; camión "Mantenimiento/Fuera de servicio"; prioridad y estados de necesidad; "No asignable", "Pendiente de confirmación"; zonas; puntos; estado de bienes + declaración en recepción); a11y: búsqueda sin label, toggles sin `aria-pressed` en recepción, sin `aria-current` ni skip link, mapas solo por hover.
- Sitemap E1 incluye "Nosotros" y "Registrar entidades beneficiarias" (admin), ausentes en v0.

## 6. Riesgos priorizados

| # | Riesgo | Impacto | Mitigación [PROP] |
|---|---|---|---|
| 1 | Enunciado E5 desconocido (SSR/MVC, stack) | Alto | Arquitectura con presentación portable (HTML semántico + CSS tokens + JS progresivo) y capa de orquestación separada |
| 2 | Sin auth hasta E6 | Alto | `SessionAdapter` simulado con selector de rol, marcado como demo |
| 3 | Brechas de API (mapas, fotos, CSV, métricas públicas) | Alto | Adapters por servicio; features TBD con estados explícitos "no disponible"; lista de brechas para el equipo |
| 4 | Sin CORS/gateway | Medio | Llamadas desde servidor (BFF) o proxy de desarrollo |
| 5 | Persistencia en memoria | Medio | Fixtures etiquetados para demo; seeding vía API |
| 6 | Contraste del Figma | Medio | Ajustar tokens manteniendo identidad |
| 7 | Drift OpenAPI | Medio | Tipar contra DTOs del código; tests de contrato livianos |

## 7. `study/` — vigencia

- Útil: casos de uso, máquina de estados, algoritmos de asignación, eventos.
- Desactualizado [OBS]: Feign desde donaciones (eliminado en `58654adf`); `ASIGNADA` → `ASIGNACION_REALIZADA`; estados de entrega/ruta; carpeta `estados/` inexistente; falta `donante.dado-de-baja.v1`.
- `study/app/` (Vite + React 19) es herramienta de estudio, **no reutilizable**.

## 8. Decisiones tomadas con la usuaria (2026-10-01)

1. El proyecto es un **prototipo estético/UX anticipado a la E5**; la calidad y escalabilidad del código son prioritarias porque el alcance de la E5 es incierto.
2. La cátedra aún no definió el stack de la E5. Funcionalidades complejas (mapa interactivo, tracking de camiones) quedan **TBD**.
3. Sesión simulada con selector de rol (como el login del v0) hasta que exista auth.
4. **No se trabaja sobre el repo oficial.** Se usará un repo aparte; el repo `DonaTrack/` y `study/` son de solo lectura.
