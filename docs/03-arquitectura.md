# 03 · Arquitectura, integración con API y design system (Etapa 3)

> Fecha: 2026-10-01 · Estado: **aprobada** (iteración visual de destacados y vista móvil en curso)
> Base: [`01-auditoria.md`](01-auditoria.md), [`02-especificacion-funcional.md`](02-especificacion-funcional.md).
> Etiquetas: **[OBS]** verificado · **[INF]** inferencia · **[PROP]** propuesta · **[TBD]** pendiente.

## 1. Restricciones que guían la decisión

| Restricción | Fuente |
|---|---|
| La E5 pedirá "Arquitectura Web MVC" con un componente de front con SSR y un "Servidor Frontend — FrontEnd y Orquestación" | Enunciado-1, Fig. 1 [DOC] · stack exacto desconocido [TBD] |
| Backend Spring Boot **4.0.7**, Java 21, Maven | `DonaTrack/pom.xml` [OBS] (el README dice 3) |
| Sin CORS ni gateway; 4 servicios en puertos distintos | auditoría §3 [OBS] |
| Sin auth hasta E6 | auditoría §3 [OBS] |
| Entorno local: Java 21, Maven 3.9, Node 24; **sin Docker** | verificado 2026-10-01 [OBS] |
| Preferencia: HTML, CSS y JS; pocas dependencias; código portable | `PROMPT00` |

## 2. Alternativas evaluadas

| | A. Spring MVC + Thymeleaf (SSR + BFF) | B. Node/Express + Handlebars (SSR + BFF) | C. HTML/CSS/JS estático + fixtures | D. Next.js (como el v0) |
|---|---|---|---|---|
| Encaje con "Web MVC + SSR + orquestación" | Alto | Alto | Bajo (no hay servidor) | Medio (SSR sí, MVC no) |
| Encaje con el stack del grupo | Alto (Java/Spring, igual que los servicios) | Medio | — | Bajo |
| Resuelve CORS y agregación entre servicios | Sí (llamadas server-side) | Sí | No | Parcial |
| Plantillas = HTML válido | Sí (*natural templates*) | Casi | Sí | No (JSX) |
| Costo de migrar si la cátedra pide otro motor | Bajo: vistas sin lógica + CSS/JS sin cambios | Bajo-medio | Alto (falta todo el servidor) | Alto |
| Velocidad de iteración visual | Media (DevTools + LiveReload) | Alta | Muy alta | Alta |
| Dependencias | Spring Boot starters | Express + motor | Ninguna | Muchas |

**Recomendación [PROP]: A — Spring Boot 4 + Spring MVC + Thymeleaf, con CSS y JS nativos sin build.**
Es la opción con mayor probabilidad de coincidir con la E5, usa el mismo stack que los servicios y resuelve CORS/agregación sin tocar el backend. El riesgo (que la cátedra pida otro motor) se mitiga manteniendo vistas sin lógica: controladores entregan *view models* ya resueltos, así que pasar a Handlebars u otro motor es traducir sintaxis, no rediseñar. CSS, JS, assets y design system son 100 % independientes del motor.

Descartada D: reintroduce React y una arquitectura SPA que no coincide con "MVC" y no reutiliza el conocimiento del grupo. Descartada C: obliga a reescribir toda la capa de servidor en la E5.

## 3. Arquitectura propuesta

```
Navegador ──HTTP──▶ cliente-liviano (Spring Boot, :8090)
                    ├─ web/            Controllers MVC por área (público, donante, entidad, admin)
                    │                  Form objects + Bean Validation · PRG · flash messages
                    ├─ application/    Casos de uso de UI: orquestan puertos y arman ViewModels
                    ├─ integration/    Puertos por servicio + adapters
                    │   ├─ http/       RestClient → donaciones :8080, notificaciones :8081,
                    │   │              incentivos :8082, logística :8083
                    │   └─ fixtures/   JSON etiquetados "demo" (mismo puerto, otra implementación)
                    ├─ session/        SessionPort → DemoSession (HttpSession) · guardas por rol
                    └─ templates/ + static/   Thymeleaf (layouts, fragments) · CSS · JS · assets
```

### 3.1 Capas y reglas
- **web** solo recibe, valida forma, llama a `application` y elige vista. Sin llamadas HTTP ni lógica de negocio.
- **application** compone datos de varios servicios (p. ej. "donaciones asignadas a la entidad" = necesidades → propuestas aprobadas → donaciones, spec §5 J5) y produce ViewModels inmutables (records). Es el único lugar con reglas de presentación (etiquetas de estado, tonos, textos derivados).
- **integration** expone un puerto por servicio (`DonacionesPort`, `LogisticaPort`, `IncentivosPort`, `NotificacionesPort`). Los DTOs se copian como `record` desde el código del backend (no del OpenAPI, por el drift) y se nombran igual.
- **Selección de adapter por servicio** con propiedad `donatrack.backend.<servicio>.mode = http | fixture`. Permite trabajar sin Docker, mezclar servicios reales y demo, y nunca mezclar silenciosamente: toda vista con datos de fixture muestra la etiqueta "Dato de demostración".
- **Errores:** el adapter HTTP traduce `ErrorResponse` (`code`, `errors[]`, `traceId`) a una excepción tipada; un `@ControllerAdvice` del front la convierte en mensaje de `messages_es.properties` por `code`, conserva el `traceId` para soporte y muestra error parcial si falla un solo servicio.
- **Tiempos de espera y reintentos:** timeout corto por servicio; sin reintentos en operaciones que escriben.

### 3.2 Sesión y autorización
- `SessionPort` con `DemoSession`: guarda `rol`, `personaId`, `donanteId` o `entidadId`, y nombre visible. Se elige en "Ingresar" desde personas reales de la API (o fixtures).
- Interceptor de rutas: `/donante/**`, `/entidad/**`, `/admin/**` exigen el rol correspondiente; si no, redirige a Ingresar con mensaje.
- `X-Actor` / `actor` se completan desde la sesión.
- Reemplazo en E6: `SessionPort` pasa a implementarse con Spring Security + `auth-service`; controladores y vistas no cambian. El backend sigue siendo la autoridad.

### 3.3 Rutas

| Área | Rutas |
|---|---|
| Público | `/` · `/donaciones-entregadas` · `/mapa-de-impacto` (TBD) · `/privacidad` · `/ingresar` · `/registro?tipo=humana\|juridica\|entidad` |
| Donante | `/donante` · `/donante/donaciones` · `/donante/donaciones/{id}` · `/donante/entidades` · `/donante/entidades/{id}` · `/donante/incentivos` · `/donante/notificaciones` · `/donante/seguimiento` |
| Entidad | `/entidad` · `/entidad/necesidades` (+ `/nueva`, `/{id}/editar`) · `/entidad/donaciones` · `/entidad/donaciones/{id}` · `/entidad/seguimiento` · `/entidad/notificaciones` |
| Admin | `/admin` · `/admin/donantes` (+ `/nuevo`, `/importar`) · `/admin/entidades` (+ `/nueva`) · `/admin/donaciones` (inventario) · `/admin/donaciones/nueva` · `/admin/donaciones/revision` · `/admin/asignaciones` · `/admin/camiones` (+ `/nuevo`) · `/admin/rankings` |

Filtros y paginación en query string (`?estado=EN_TRASLADO&categoria=…`) para que sean compartibles y funcionen sin JS.

### 3.4 Interacción y JS
- Todo flujo funciona **sin JS** (formularios POST + PRG, filtros GET).
- JS como mejora progresiva en módulos ES pequeños, sin dependencias: menú móvil, carrusel, diálogo de confirmación (`<dialog>`), filtros dependientes (categoría → subcategoría), envío de filtros sin recarga completa (fetch de un fragmento Thymeleaf), anuncio de resultados (`aria-live`).
- Consistencia eventual (spec §2.3): tras confirmar recepción, la vista muestra "Actualizando…" y JS consulta el estado algunas veces con intervalo creciente; sin JS, un enlace "Actualizar".

### 3.5 Capacidades TBD
| Capacidad | MVP | Evolución prevista |
|---|---|---|
| Mapas (R2, R9, R14) | Componente `tbd-panel` + listado accesible | Leaflet + OSM cuando existan coordenadas (G4) |
| Fotos de recepción | Panel TBD explicativo | Subida multipart en el BFF cuando exista G5 |
| Importar CSV | Formulario + seguimiento de estado con `GET /api/donantes/archivos/{id}`; el envío queda detrás del puerto (hoy el backend espera una ruta en su servidor) | Multipart (G6) |
| Notificaciones | Lista al cargar | Contador en topbar; tiempo real fuera de alcance |

### 3.6 Configuración y entornos
- Perfiles: `fixtures` (por defecto, sin backend), `local` (servicios en `localhost:8080–8083`), `preprod`.
- URLs por variable de entorno: `DONACIONES_URL`, `NOTIFICACIONES_URL`, `INCENTIVOS_URL`, `LOGISTICA_URL`.
- Puerto del front: `8090` [PROP] (evita choques con 8080–8085).
- `LOGISTICA_TRACKING_BASE_URL` del backend debe apuntar a `/{área}/seguimiento` del front cuando se integre.

### 3.7 Calidad y pruebas
- JUnit 5 + MockMvc: controladores (rutas, guardas, validación, PRG).
- `MockRestServiceServer`: adapters HTTP contra respuestas de ejemplo tomadas del backend.
- Prueba de contrato de fixtures: cada JSON debe deserializar al `record` del DTO (detecta drift).
- Herramientas de desarrollo [PROP, sin dependencia de runtime]: Playwright + axe-core para recorridos, accesibilidad y capturas en 360/768/1280. Requiere Node (instalado).
- Formato: Spotless (igual que el backend) para Java; Prettier opcional para CSS/JS/HTML.

### 3.8 Estructura del repo
```
frontend-donatrack/
├─ pom.xml
├─ src/main/java/grupo5/clienteliviano/{web,application,integration/{http,fixtures},session,config}
├─ src/main/resources/
│  ├─ templates/{layouts,fragments,publico,donante,entidad,admin}
│  ├─ static/{css,js,img,fonts}
│  ├─ fixtures/*.json
│  └─ messages_es.properties
├─ src/test/java/…
├─ e2e/ (Playwright, solo desarrollo)
├─ design/figma-assets/ (originales exportados del Figma)
└─ docs/
```

## 4. Design system

Fuente: Figma Prototipo v2, nodo `480:1914` (`get_design_context` y `get_variable_defs`, leídos completos). Los ajustes respecto del Figma se justifican por contraste (WCAG 2.2 AA) o por consolidación.

### 4.1 Color

**Primitivos**

| Token | Valor | Origen Figma |
|---|---|---|
| `--teal-900` | `#163a3a` | `color/cyan/16` |
| `--teal-800` | `#1d4d4d` | `color/cyan/21` |
| `--teal-750` | `#1d4c4c` | título "Transparencia en números" |
| `--teal-100` | `#cfefe7` | `color/cyan/87` |
| `--green-500` | `#50d794` | `Green[P]` |
| `--green-400` | `#54da8e` | `color/spring green/59` (botones, nodos) |
| `--green-50` | `#eafbf3` | `color/grey/95` |
| `--turquoise-500` | `#36c7b4` | círculos de métricas, gradiente CTA |
| `--slate-green` | `#364d4a` | título del CTA |
| `--ink` | `#292928` | `Ink[P]` |
| `--ink-muted` | `#6b6b6b` | footer |
| `--gray-100` | `#f3f4f6` | cuerpo de tarjetas |
| `--gray-75` | `#f8f8f8` | texto de badges |
| `--gray-50` | `#f4f6fc` | `color/grey/97` |
| `--white` | `#ffffff` | |

Todos los valores son los del Figma, sin fusiones ni agregados. Los verdes `#50d794`/`#54da8e` y los teal `#1d4d4d`/`#1d4c4c` son casi idénticos: se conservan separados por fidelidad y se podrán unificar editando `tokens.css`.

**Fuente única de color [decisión 2026-10-01].** Toda la paleta vive en un solo archivo, `static/css/tokens.css`, en dos niveles:
1. **Primitivos** (tabla anterior): los únicos valores hexadecimales de todo el proyecto.
2. **Semánticos**, que solo referencian primitivos: `--text: var(--ink)`, `--text-brand: var(--green-500)`, `--surface-dark: var(--teal-900)`, etc. Lista: `--text`, `--text-muted`, `--text-on-dark`, `--text-on-dark-muted`, `--text-brand`, `--surface`, `--surface-muted`, `--surface-dark`, `--surface-brand`, `--border`, `--focus-ring`, `--action-primary-bg`, `--action-primary-text`, y los tonos de estado `--status-<tono>-bg/-text`.

Los componentes, plantillas y JS **solo usan tokens semánticos**; nunca un hex ni un primitivo. Ajustar un color = editar una línea de `tokens.css`. Un chequeo automático (grep en CI/local) falla si aparece un hex fuera de ese archivo.

**Estado de la paleta:** se usan **los valores del Figma sin cambios**. Los ajustes de contraste de la tabla siguiente quedan **documentados como riesgo, no aplicados** (no aprobados el 2026-10-01). Gracias a la fuente única, aplicarlos más adelante es cambiar el valor de los tokens semánticos afectados.

**Hallazgos de contraste (medidos, pendientes de decisión)**

| Uso en Figma | Contraste | Opción posible | Resultado |
|---|---|---|---|
| Texto blanco sobre `#50d794` (títulos de banda, botones outline) | 1,83 | Texto `--teal-900` | 6,76 |
| Texto blanco en círculos `#36c7b4` | 2,10 | Texto `--teal-900` | 5,87 |
| Títulos verdes `#50d794` sobre blanco ("Cómo funciona", pasos) | 1,83 | `--green-700` para texto; `#50d794` solo en relleno/decoración | 6,00 |
| Links del nav: ink al 62 % sobre blanco | 4,29 | `--ink-muted` `#6b6b6b` | 5,33 |
| Texto de chips: blanco 60 % sobre teal, 11 px | 4,57 | Blanco 72 %, 13 px | ≥ 5,5 |
| Badge de categoría a 4,54 px (instancia escalada) | ilegible | 12 px mínimo, texto `--teal-900` sobre `--green-500` | 6,76 |

Se mantienen sin cambios: teal-900 sobre green-500 en botones (6,76), blanco sobre teal (12,35), `#cfefe7` sobre teal (10,09).

**Estados de dominio** — *propuesta pendiente de aprobación* (el Figma no define colores de estado). Se declaran en `tokens.css` como `--status-<tono>-bg/-text`; pill = fondo + texto, todos ≥ 6:1; siempre con texto, nunca solo color.

| Tono | Fondo / texto | Estados |
|---|---|---|
| neutral | `#eef1f4` / `#3d4852` | `EN_DEPOSITO`, `PENDIENTE`, `CARGADA` |
| info | `#e5f4f7` / `#14596a` | `ASIGNACION_REALIZADA`, `LISTA_PARA_ENTREGAR`, `NORMALIZADA` |
| progreso | `#e8efff` / `#1f4fa3` | `EN_TRASLADO`, `EN_RUTA` |
| éxito | `#eafbf3` / `#146c46` | `ENTREGADA`, `APROBADA`, `DISPONIBLE`, `SEGMENTADA`, `COMPLETADA` |
| advertencia | `#fff3dd` / `#8a4b00` | `VENCIDA`, `REVISION`, `PENDIENTE_REVISION` |
| peligro | `#fdecec` / `#a12626` | `ENTREGA_FALLIDA`, `NO_RECIBIDA`, `DESCARTADA`, `DESHABILITADO` |

Etiquetas en español: catálogo único en `messages_es.properties` (p. ej. `estado.donacion.ASIGNACION_REALIZADA=Asignada`).

### 4.2 Tipografía
- **Solo DM Sans** (300–900) en todo el proyecto [decisión 2026-10-01]: interfaz, cuerpo, hero, títulos y números. Es la fuente declarada en las variables del Figma.
- Montserrat (títulos de sección y tarjetas en el Figma) e Inter (2 botones) se reemplazan por DM Sans con el mismo tamaño y peso (Bold 700 para títulos).
- Familia definida una sola vez en `tokens.css` (`--font-sans`).
- Autoalojada en `woff2` variable, subconjunto latino, `font-display: swap`; sin CDN.

| Token | Tamaño (móvil → escritorio, `clamp`) | Uso |
|---|---|---|
| `--fs-display` | 48 → 84 px, `letter-spacing -0.02em` | h1 del hero |
| `--fs-h2` | 32 → 48 px | títulos de sección (DM Sans Bold) |
| `--fs-h3` | 28 → 36 px | "Cómo funciona", títulos de página privada |
| `--fs-h4` | 20 → 24 px | pasos, subtítulos |
| `--fs-body-lg` | 18 → 20 px | bajadas |
| `--fs-body` | 16 → 18 px | cuerpo |
| `--fs-sm` | 15 px | botones, tarjetas, tablas |
| `--fs-xs` | 13 px | metadatos (**mínimo**) |

### 4.3 Espaciado, radios, sombras, layout
- Espaciado base 4 px: `4, 8, 12, 16, 24, 32, 40, 48, 60, 80, 100`.
- Radios: `--r-xs 5`, `--r-sm 10` (tarjetas), `--r-md 20` (bandas), `--r-lg 24` (chips), `--r-xl 40` (panel Cómo funciona), `--r-pill 999`.
- Sombras: `--shadow-card 0 1px 15px rgb(0 0 0 / .18)`; `--shadow-band 0 10px 15px rgb(54 77 74 / .39)`; `--shadow-btn 0 10px 15px rgb(22 58 58 / .06)`; `--glow-node 0 0 0 8px rgb(84 218 142 / .28), 0 0 24px 6px rgb(84 218 142 / .4)`.
- Contenedor: `max-width 1280px`, padding lateral `clamp(16px, 5vw, 100px)`.
- Breakpoints (mobile first): `600px`, `900px`, `1200px`. Probados en 360 / 768 / 1280.
- Movimiento: `--dur-fast 120ms`, `--dur-base 200ms`; todo efecto respeta `prefers-reduced-motion`.

### 4.4 Componentes y estados

Cada componente define: default, hover, focus-visible (anillo 3 px `--teal-900` o `--green-500` sobre oscuro, ≥ 3:1), active, disabled, cargando y error cuando aplica.

| Componente | Notas |
|---|---|
| Botón | `primary` (pill green-500, texto teal-900) · `ghost-dark` (glass sobre teal) · `outline` (como en el Figma: borde y texto claros sobre bandas verdes) · `danger` · tamaños sm/md · estado cargando con texto |
| Navbar pública | Pill blanca; en < 900 px, botón "Menú" con `aria-expanded` y panel desplegable |
| Footer | 3 columnas → 1 columna en móvil |
| Eyebrow | Pill glass con ícono |
| Chip de métrica | Glass sobre teal; valor + etiqueta; marca "demo" si es fixture |
| Pasos ("Cómo funciona") | Lista ordenada `<ol>`; horizontal ≥ 900, zigzag 600–900 (variante del Figma `493:2915`), vertical < 600 |
| Tarjeta de donación | Imagen con `alt` real, categoría, donante/entidad, título, fecha `<time>` |
| Carrusel | `scroll-snap`, botones anterior/siguiente con label, sin autoplay, usable con teclado |
| Métrica circular | Turquesa con texto blanco (Figma) |
| Banda CTA | Gradiente turquesa → verde |
| Campos de formulario | label visible, ayuda, error vinculado (`aria-describedby`), requerido marcado con texto |
| Tarjetas de selección | Radios estilizados (tipo de registro, rol demo) — no botones con `aria-pressed` |
| Pill de estado | §4.1, texto siempre presente |
| Tabla | `<table>` con `<caption>`; < 600 px se reorganiza en filas apiladas |
| Barra de filtros | `<form method="get">`, botón aplicar, limpiar, contador `aria-live` |
| Estado vacío / error / cargando | Mensaje + acción; error con `role="alert"`; esqueletos con `aria-busy` |
| Diálogo de confirmación | `<dialog>` nativo; foco atrapado y retorno |
| Línea de tiempo | Historial de estados (`<ol>`, fecha, actor, justificación) |
| Shell privado | Sidebar teal-900 (lleva la identidad del hero al área privada), topbar blanca; sidebar → drawer < 900 px; `aria-current="page"`; skip link |
| Etiqueta demo | "Dato de demostración" visible + texto para lectores |
| Panel TBD | Explica qué falta y por qué; sin simular |

### 4.5 Hero estático (MVP) preparado para animación
Estructura en capas, todas decorativas (`aria-hidden`), salvo el texto y los chips:
1. Fondo: `linear-gradient(144.6deg, #163a3a 0%, #1d4d4d 60%, #163a3a 100%)` (valores del Figma).
2. Curvas topográficas: `design/figma-assets/hero-fondo-topografico.svg` como `background-image`.
3. Red de ruta: SVG **inline** con el `path` del Figma (`hero-red-ruta.svg`, `stroke-dasharray 7.65 9.57`) y los nodos como `<circle>` con resplandor; clases-gancho `hero-route__path`, `hero-route__node`.
4. Chips: HTML real posicionado en porcentajes dentro de un contenedor con `aspect-ratio 652/440` (posiciones del Figma).

Responsive: ≥ 900 px dos columnas (0.92fr / 1.08fr, como el Figma); < 900 px el texto va primero, la red se reduce y los chips pasan a grilla 2×2 sin la ruta.
Animación futura (Etapa 6): `stroke-dashoffset` del path, pulso de nodos y entrada de chips, con `prefers-reduced-motion: reduce` → estático. El Figma no define animaciones reales (`get_motion_context` solo devolvió una animación nula en el nav).

### 4.7 Lenguaje visual de identidad (decisión 2026-10-01)
Objetivo: que cualquier pantalla sea reconocible como DonaTrack a simple vista. La identidad del hero se aplica a **todo** el sitio, con gradientes y efectos, no solo a la landing.

| Motivo | De dónde sale | Dónde se usa |
|---|---|---|
| Gradiente teal (`--grad-hero`) | Fondo del hero | Hero público, barra lateral, encabezado de cada página privada, tarjetas destacadas |
| Curvas topográficas | `hero-fondo-topografico.svg` | Fondos teal (hero, sidebar, encabezados, tarjetas oscuras) |
| Ruta punteada + nodos con resplandor | Red del hero | Navegación lateral (cada ítem es un nodo, el activo brilla), **recorrido de estados de una donación**, indicadores de progreso |
| Vidrio (glass) | Chips del hero | Chips de métricas, etiquetas sobre fondos oscuros, botón secundario |
| Gradiente turquesa → verde (`--grad-impulso`) | Banda CTA del Figma | Métrica destacada, barra de progreso, avatar, acentos superiores de tarjetas |
| Fondo de página con halo verde suave | Derivado de `--green-50` | Superficie general del área privada |

Reglas: los efectos son CSS (gradientes, `backdrop-filter`, sombras) y SVG; sin imágenes pesadas. Todo valor sale de `tokens.css`. Los efectos decorativos van con `aria-hidden` y se reducen con `prefers-reduced-motion` / `prefers-reduced-transparency` cuando aplique.
Exploración de referencia: `design/exploraciones/area-privada-donante.html` (autocontenida; datos de demostración).

### 4.6 Wireframes de flujos clave

**Shell privado (escritorio / móvil)**
```
┌────────┬──────────────────────────────────────┐   ┌──────────────────────┐
│ Logo   │ Inicio › Mis donaciones   [🔔] [NP ▾]│   │ [☰] DonaTrack   [🔔] │
│ ────── ├──────────────────────────────────────┤   ├──────────────────────┤
│ Inicio │ h1 Mis donaciones                     │   │ h1 Mis donaciones    │
│ Mis... │ [Estado ▾][Categoría ▾][Subcat ▾][↺]  │   │ [Filtros ▾]          │
│ Entid. │ 12 donaciones encontradas             │   │ 12 encontradas       │
│ Incen. │ ┌ tabla ───────────────────────────┐  │   │ ┌ fila apilada ────┐ │
│ Notif. │ │ Código Subcat Cant Fecha Estado  │  │   │ │ DN… · En traslado│ │
│ Segui. │ └──────────────────────────────────┘  │   │ └──────────────────┘ │
└────────┴──────────────────────────────────────┘   └──────────────────────┘
```

**Detalle de donación asignada (entidad) con confirmación**
```
h1 Arroz · 50 kg                                [pill: En traslado]
┌ Resumen ─────────────┐ ┌ Historial ─────────────────────────┐
│ Necesidad vinculada  │ │ ● En depósito        01/10 09:12   │
│ Donante              │ │ ● Asignada           02/10 03:00   │
│ Entrega · Camión     │ │ ● Lista para entregar 02/10 18:40  │
└──────────────────────┘ │ ● En traslado        03/10 08:05   │
[Confirmar recepción] [No la recibí]   └────────────────────────────────────┘
Fotos: panel TBD
→ tras confirmar: alerta de éxito + estado "Actualizando…"
```

**Registrar donación (admin)**
```
1 Donante  [buscar o crear ▾]   Fecha [  ]   Depósito [  ]   Descripción [   ]
2 Ítems    ┌ descripción · cantidad · vencimiento · estado · [quitar] ┐
           └──────────────────────────────────────────────────────────┘
           [+ Agregar ítem]
           [Registrar donación] [Guardar y cargar otra]
→ Resultado: Segmentada en N donaciones | M ítems en revisión → [Ir a revisión]
```

## 5. Accesibilidad (WCAG 2.2 AA) — requisitos de implementación
- `lang="es"`, un `h1` por página, jerarquía sin saltos, landmarks, skip link.
- Foco visible en todo control (2.4.7, 2.4.11), orden lógico, objetivos táctiles ≥ 24 px (2.5.8).
- Formularios: labels visibles, errores por campo y resumen, sin depender del color; sin límites de tiempo.
- Estados anunciados con `role="status"` / `role="alert"`; contadores de resultados con `aria-live="polite"`.
- Tablas con encabezados; componentes compuestos solo con patrones ARIA probados (disclosure, dialog).
- Imágenes de contenido con `alt`; decorativas `alt=""` / `aria-hidden`.
- Contraste según §4.1; texto mínimo 13 px; zoom al 200 % y reflow en 320 px sin scroll horizontal.
- `prefers-reduced-motion` respetado.

## 6. Decisiones para aprobar

| # | Decisión |
|---|---|
| D1 | ✅ Aprobada — Stack: Spring Boot 4 + Spring MVC + Thymeleaf, CSS/JS nativos sin build |
| D2 | ✅ Aprobada — Capas web / application / integration con puertos y adapters `http`/`fixture` por servicio, conmutables por configuración |
| D3 | ✅ Aprobada — Sesión demo detrás de `SessionPort` + guardas por rol |
| D4 | ✅ Resuelta — Solo DM Sans · paleta del Figma sin cambios en fuente única (`tokens.css`) · ajustes de contraste y colores de estado: **pendientes** |
| D5 | ✅ Aprobada — La identidad del hero se lleva a todo el sitio (ver §4.7 y `design/exploraciones/`) |
| D6 | ✅ Aprobada — Herramientas solo de desarrollo: Playwright + axe-core |
| D7 | ✅ Aprobada — Puerto 8090 · proyecto `cliente-liviano` (artifactId Maven) · paquete Java `grupo5.clienteliviano` |
