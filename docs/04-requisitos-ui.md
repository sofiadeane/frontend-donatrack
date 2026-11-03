# 04 · Requisitos de UI y roles (lista atómica)

> Fecha: 2026-10-11 · Base: enunciado TPA 2026 completo (req. mínimos de UI/UX 1–20, dominio E1–E3, E6 "Consideraciones para el Maquetado", E7) + [`02-especificacion-funcional.md`](02-especificacion-funcional.md) + [`BACKLOG.md`](BACKLOG.md).
>
> **Origen** de cada requisito:
> `[Mín n]` requerimiento mínimo de UI/UX n (pág. 9–10) · `[E1]…[E7]` texto de dominio o requerimiento de esa entrega · `[Maq]` "Consideraciones para el Maquetado" (E6) · `[Ley]` normativa del anexo · `[Imp]` implicancia: no está escrito literal, pero sin esto el requisito escrito no se cumple.
>
> **Estado** (según backlog/código al 2026-10-11): ✅ hecho o en revisión en el cliente liviano · 🟡 solo en maqueta (v0 / Figma / exploración) · ⬜ no existe.

---

## 0. Roles del sistema

| Rol | Cómo llega | Alcance | Notas |
|---|---|---|---|
| **Visitante** (sin sesión) | Directo, campaña, link de notificación | Landing, galería de entregadas, mapa público, legal, registro, ingreso | `[Mín 1–3]` |
| **Persona donante humana** | Autoregistro, alta por admin, importación CSV, SSO social | Sus donaciones, entidades, **incentivos**, notificaciones, seguimiento, perfil | Incentivos es exclusivo de la humana `[Mín 7]` (ver §0.1) |
| **Persona donante jurídica (representante)** | Autoregistro, alta por admin, CSV | Igual a humana salvo incentivos `[Mín 7]`; opera en nombre de la organización `[E1]` | Una organización tiene N representantes |
| **Entidad beneficiaria (representante)** | Autoregistro (`[Mín 3]`) | Necesidades, donaciones asignadas, confirmar recepción, seguimiento, notificaciones | N representantes con correo `[E1]` |
| **Persona administradora** | Alta interna (no autoregistro) | Depósito, donantes, donaciones, asignaciones, camiones, rankings, CSV, casos de entrega | Recibe notificaciones de entregas no satisfactorias `[E3]` |
| **Chofer** *(a decidir)* | — | Ver su ruta, iniciar ruta | `[E3]` habla de "su aplicación"; no está en los mínimos de UI → ver §11 |

### 0.1 Requisitos de roles y permisos
- [ ] **ROL-01** ⬜ Matriz de permisos documentada (rol × pantalla × acción) — base para guardas y para el diagrama de casos de uso. `[Imp]`
- [ ] **ROL-02** ✅ Guarda por rol en cada ruta privada: acceso a una ruta de otro rol → 403 con página propia, no la del framework. `[Imp]`
- [ ] **ROL-03** ⬜ Distinguir donante humana vs. jurídica en la sesión: la jurídica **no ve** la sección Incentivos (el mínimo 7 dice "persona humana donante"). ⚠️ El dominio E2 dice que el representante de una jurídica también ve métricas y ranking → decidir: ¿la jurídica ve *métricas* pero no *misiones/insignias*? `[Mín 7] [E2]`
- [ ] **ROL-04** ⬜ Representantes: una persona puede operar en nombre de una organización (jurídica o entidad). La UI muestra siempre "Operando como: <razón social>". `[E1] [Imp]`
- [ ] **ROL-05** ⬜ Si una persona tiene más de un rol/organización (ej.: representante de 2 entidades, o donante y representante), selector de contexto en el menú de cuenta. `[Imp]` *(decidir si se soporta o se restringe a un rol por cuenta)*
- [ ] **ROL-06** ⬜ El rol admin no tiene autoregistro ni login social; se crea internamente. `[Imp]`
- [ ] **ROL-07** ⬜ Cada acción que cambia estado registra el actor (para la auditoría que pide E2) y la UI lo muestra en los historiales. `[E2] [Imp]`

---

## 1. Requisitos transversales (aplican a todas las pantallas)

### 1.1 Arquitectura de navegación (máx. 3 niveles) `[Maq]`
- [ ] **NAV-01** ⬜ Mapa del sitio (sitemap) por rol con la profundidad de cada pantalla anotada. Entregable para demostrar el cumplimiento.
- [ ] **NAV-02** ⬜ Ninguna funcionalidad clave a más de 3 niveles desde el inicio del rol (nivel 1 = menú principal, 2 = lista, 3 = detalle/acción). Ej.: Admin → Donaciones → Detalle → *Marcar vencida* es una acción dentro del nivel 3, no un 4.º nivel.
- [ ] **NAV-03** ✅ Menú principal por rol (barra lateral en escritorio, navegación inferior en móvil, máx. 5 ítems en la inferior).
- [ ] **NAV-04** ⬜ Breadcrumbs (migas de pan) en niveles 2 y 3 — orientan y dan salida (Nielsen 3 y 6).
- [ ] **NAV-05** ✅ Indicador de sección activa en el menú (`aria-current="page"`).
- [ ] **NAV-06** ⬜ Rutas con URL estable y compartible (filtros por GET, detalle por id) — necesario para que los links de las notificaciones aterricen en el lugar exacto. `[E3] [Imp]`
- [ ] **NAV-07** ⬜ Deep link con sesión vencida: login y **volver a la URL pedida** (ej.: link "seguí tu entrega" del mail). `[Imp]`
- [ ] **NAV-08** ✅ Logo = volver al inicio del rol.
- [ ] **NAV-09** ⬜ Accesos rápidos en el inicio de cada rol a las 2–3 tareas más frecuentes (admin: registrar donación; entidad: nueva necesidad / confirmar recepción).

### 1.2 Usabilidad heurística `[Maq]` — qué implica "cumplir"
"Diseñada bajo principios de usabilidad heurística" se traduce en (a) requisitos concretos por heurística de Nielsen y (b) una **evaluación heurística documentada** que lo demuestre.

**(a) Requisitos por heurística**
- [ ] **HEU-01 Visibilidad del estado del sistema:** pills de estado en toda donación/entrega; historial de estados; loaders (ASY-*); "Actualizando…" cuando el estado depende de un evento asíncrono; hora de "última actualización" en los mapas.
- [ ] **HEU-02 Relación con el mundo real:** vocabulario del dominio en castellano llano ("En depósito", "Lista para entregar"), nunca enums (`EN_DEPOSITO`) ni códigos HTTP; unidades con nombre (kg, unidades, m³); fechas en formato local (dd/mm/aaaa).
- [ ] **HEU-03 Control y libertad:** "Cancelar"/"Volver" en todo formulario; deshacer en acciones reversibles (toast con "Deshacer" al descartar una propuesta); confirmación en las irreversibles; limpiar filtros.
- [ ] **HEU-04 Consistencia y estándares:** mismo componente para la misma función en todos los roles (lista filtrable, detalle, pill); botón primario siempre a la derecha/abajo; mismos íconos por categoría y por estado.
- [ ] **HEU-05 Prevención de errores:** validación en el cliente antes de enviar; campos condicionales (vencimiento solo si es perecedero, nuevo/usado solo si la categoría lo exige); selects en lugar de texto libre (subcategoría, tipo jurídico); deshabilitar acciones inválidas según estado (no se puede "Marcar vencida" una donación en traslado); doble envío bloqueado.
- [ ] **HEU-06 Reconocer antes que recordar:** autocompletar donante por nombre/DNI/CUIT/correo; mostrar resumen del donante elegido en el formulario de donación; filtros activos visibles como chips; categoría→subcategoría en cascada.
- [ ] **HEU-07 Flexibilidad y eficiencia:** "Guardar y cargar otra" en registro de donación; acciones masivas en inventario (marcar varias vencidas); atajos de teclado opcionales para admin; filtros recordados en la sesión.
- [ ] **HEU-08 Diseño estético y minimalista:** una acción primaria por pantalla; información secundaria plegada (desplegables); tablas con columnas mínimas en móvil.
- [ ] **HEU-09 Reconocer, diagnosticar y recuperarse de errores:** mensajes que dicen qué pasó + qué hacer ("La patente ya existe. Revisá o editá el camión AB123CD"); error junto al campo; foco al primer error; reintentar en fallas de servicio.
- [ ] **HEU-10 Ayuda y documentación:** tooltips e hints contextuales (ver 1.4); página/sección "Cómo funciona" (pública) y "Cómo donar" (lugar y horario del depósito); ayuda en el importador CSV (formato y plantilla).

**(b) Verificación**
- [ ] **HEU-11** ⬜ Evaluación heurística de las pantallas clave por 3–5 evaluadores del equipo, con planilla: pantalla · heurística violada · descripción · severidad (0–4 de Nielsen) · corrección.
- [ ] **HEU-12** ⬜ Correcciones de severidad 3–4 aplicadas antes de la entrega; registro de las de severidad 1–2 como deuda.
- [ ] **HEU-13** ⬜ "Curva de aprendizaje mínima para usuarios sin experiencia": prueba con 3–5 usuarios externos (tareas: registrarse, filtrar donaciones, registrar necesidad, confirmar recepción) midiendo éxito, tiempo y errores. `[Maq]`
- [ ] **HEU-14** ⬜ Onboarding del primer ingreso por rol (ver HLP-05).

### 1.3 Familiaridad: patrones establecidos (Material / HIG) y Laws of UX `[Maq]`
- [ ] **PAT-01** ⬜ Documentar qué patrón de Material Design / HIG usa cada componente (app bar, navigation rail/bottom nav, cards, chips de filtro, snackbar/toast, dialog, stepper, FAB opcional) — sirve para justificar "familiaridad".
- [ ] **PAT-02** ⬜ Formularios largos (registro, donación) con **stepper** o secciones con índice lateral (patrón establecido).
- [ ] **PAT-03** ⬜ Listas con filtros como chips/selects arriba, resultados con contador, paginación o "cargar más".
- [ ] **PAT-04** ⬜ Ley de Hick: menús con ≤ 7 opciones; filtros avanzados plegados.
- [ ] **PAT-05** ⬜ Ley de Fitts: acción primaria grande y cerca del pulgar en móvil (barra de acción inferior fija en formularios largos).
- [ ] **PAT-06** ⬜ Ley de Jakob: login, registro, perfil y notificaciones con la forma que todos conocen (campana arriba a la derecha, avatar con menú).
- [ ] **PAT-07** ⬜ Umbral de Doherty (<400 ms) coherente con ASY-01 (feedback antes de 300 ms).
- [ ] **PAT-08** ⬜ Gradiente de objetivo / efecto de progreso: barras de progreso en misiones y en cobertura de necesidades.

### 1.4 Microcopy y ayudas visuales `[Maq]`
"Los flujos críticos" = registro, ingreso, registrar donación, registrar necesidad, asignar, confirmar recepción, importar CSV.
- [ ] **HLP-01** ⬜ Todo campo tiene `<label>` visible (no solo placeholder).
- [ ] **HLP-02** ⬜ Placeholders con **ejemplo de formato**, no con la etiqueta: "Ej.: 30-12345678-9", "Ej.: AB 123 CD", "Ej.: 100".
- [ ] **HLP-03** ⬜ Texto de ayuda bajo campos no obvios: "Medio preferido: por acá te avisamos de tus donaciones"; "Recurrente: se repite cada período"; "Subcategoría: es la unidad mínima de asignación".
- [ ] **HLP-04** ⬜ Tooltips (ícono ⓘ) accesibles por teclado y descartables con Esc (WCAG 1.4.13) en: tipos de necesidad, algoritmos de asignación (qué prioriza cada uno), categorías de donante, misiones, estados de donación, "racha".
- [ ] **HLP-05** ⬜ Onboarding hints en el primer ingreso de cada rol (3–4 pasos tipo coach marks, salteable, con "no volver a mostrar"):
  - Donante: mis donaciones → seguimiento → incentivos.
  - Entidad: registrar necesidad → ver asignadas → confirmar recepción.
  - Admin: registrar donación → asignaciones → camiones.
- [ ] **HLP-06** ⬜ Estados vacíos con explicación + acción siguiente ("Todavía no registraste necesidades · Registrar necesidad").
- [ ] **HLP-07** ⬜ Microcopy de botones con verbo + objeto ("Confirmar recepción", no "Aceptar").
- [ ] **HLP-08** ⬜ Indicador de campos obligatorios consistente (asterisco + leyenda).
- [ ] **HLP-09** ⬜ Guía de tono y glosario (términos fijos: "entidad beneficiaria", "donación", "bien", "necesidad"), para que todas las pantallas digan lo mismo.

### 1.5 Gestión de estados e interacciones (feedback no intrusivo) `[Maq]`
"Toda acción del usuario" → cada submit, cambio de estado, subida o borrado tiene feedback.
- [ ] **FBK-01** ⬜ Componente **toast** (éxito/info/error), con `role="status"` o `aria-live`, autocierre ≥ 5 s, pausa al hover/foco, botón cerrar, opcional "Deshacer".
- [ ] **FBK-02** ⬜ Componente **modal de confirmación** para acciones irreversibles o con impacto: marcar vencida, eliminar necesidad, aprobar asignación, dar de baja camión, informar no recepción, importar CSV. Foco atrapado, Esc cierra, foco vuelve al disparador.
- [ ] **FBK-03** ⬜ Componente **banner** persistente para estados de página: modo demo, servicio caído, sesión por vencer, importación en curso, cuenta sin verificar.
- [ ] **FBK-04** ⬜ Patrón PRG (post-redirect-get) con mensaje flash → toast en la página de destino (natural en SSR).
- [ ] **FBK-05** ✅ Errores de formulario por campo + resumen arriba con links a cada campo.
- [ ] **FBK-06** ⬜ Catálogo de mensajes: cada código de error del backend (`ErrorResponse.code`) mapeado a un texto humano (parcial: `messages.properties`).
- [ ] **FBK-07** ⬜ Feedback de consistencia eventual: tras confirmar recepción, "Registrado. Actualizando el estado de la donación…" (no mostrar "Entregada" hasta que lo esté).

### 1.6 Indicadores asincrónicos (> 300 ms) `[Maq]`
Con SSR la latencia está en (a) la navegación/submit y (b) lo que se cargue después por JS.
- [ ] **ASY-01** ⬜ Botón de submit pasa a estado "cargando" (spinner + texto "Guardando…", deshabilitado) si la respuesta tarda > 300 ms.
- [ ] **ASY-02** ⬜ Barra de progreso de navegación superior (tipo NProgress) para cambios de página lentos.
- [ ] **ASY-03** ⬜ **Skeleton screens** para bloques cargados de forma diferida: tiles del inicio, métricas/gráficos, mapa, listas paginadas.
- [ ] **ASY-04** ⬜ Spinner/progreso específico en: ejecución de algoritmos de asignación, importación CSV (progreso por filas), subida de fotos (progreso por archivo), carga del mapa.
- [ ] **ASY-05** ⬜ Umbral: no mostrar loader si responde en < 300 ms (evita parpadeo); mínimo visible ~500 ms una vez que aparece.
- [ ] **ASY-06** ⬜ `aria-busy="true"` en la región que carga y anuncio para lectores de pantalla.
- [ ] **ASY-07** ⬜ Medición: documentar qué pantallas superan 300 ms (log del cliente liviano o devtools) para justificar dónde hay loader.

### 1.7 Layout responsive y cross-device `[Maq]`
- [ ] **RSP-01** ✅ Breakpoints definidos como tokens: móvil (≤ 599), tablet (600–1023), desktop (≥ 1024); implementados con media queries + flexbox/grid.
- [ ] **RSP-02** ⬜ Tablas → tarjetas apiladas en móvil (inventario, donantes, camiones, propuestas).
- [ ] **RSP-03** ⬜ Formularios de una columna en móvil, dos en desktop donde tenga sentido.
- [ ] **RSP-04** ⬜ Mapa: alto adaptable, controles accesibles con el pulgar, lista de marcadores debajo en móvil.
- [ ] **RSP-05** ⬜ Sin scroll horizontal de página en 320 px (WCAG 1.4.10 Reflow).
- [ ] **RSP-06** ⬜ Modales a pantalla completa (o bottom sheet) en móvil.
- [ ] **RSP-07** ⬜ Ningún elemento interactivo superpuesto ni cortado en 320, 375, 768, 1024, 1366, 1920 px; zoom al 200 % operable (WCAG 1.4.4).
- [ ] **RSP-08** ⬜ Imágenes responsive (`srcset`/`sizes`), fotos de donaciones con relación de aspecto fija (sin saltos de layout).
- [ ] **RSP-09** ⬜ Matriz de prueba cross-device (navegadores × anchos × orientación) con capturas como evidencia.
- [ ] **RSP-10** ⬜ Subida de fotos desde el celular con cámara (`accept="image/*" capture`) — la entidad confirma la recepción en la puerta.

### 1.8 Accesibilidad WCAG 2.x nivel AA `[Maq] [Ley 26.653]`
- [ ] **A11Y-01** ✅ HTML semántico: `header`, `nav`, `main`, `footer`, `section` con títulos, jerarquía de `h1…h3` sin saltos, un solo `h1` por página.
- [ ] **A11Y-02** ⬜ `lang="es-AR"` y `<title>` único por página: "Mis donaciones · DonaTrack" (2.4.2).
- [ ] **A11Y-03** ⬜ Link "Saltar al contenido" (2.4.1).
- [ ] **A11Y-04** ✅ Foco visible en todo interactivo, con contraste ≥ 3:1 (2.4.7 / 2.4.11).
- [ ] **A11Y-05** ⬜ Todo operable con teclado, orden de tabulación lógico, sin trampas (2.1.1, 2.1.2, 2.4.3) — incluidos mapa, carrusel, desplegables, modales.
- [ ] **A11Y-06** ✅ Contraste de texto ≥ 4.5:1 (≥ 3:1 en texto grande) y de componentes/íconos ≥ 3:1 (1.4.3, 1.4.11).
- [ ] **A11Y-07** ⬜ El estado nunca se comunica solo con color (pill = color + ícono + texto) (1.4.1).
- [ ] **A11Y-08** ⬜ `alt` en todas las imágenes: fotos de donaciones con descripción ("6 sillas de madera entregadas a Escuela N.º 10"), decorativas con `alt=""` (1.1.1).
- [ ] **A11Y-09** ⬜ ARIA donde el HTML no alcanza: `aria-expanded` (desplegables, menú), `aria-current`, `aria-live`/`role="status"` (toasts, contador de resultados), `aria-describedby` (errores y ayudas), `aria-invalid`, `role="dialog"` + `aria-modal` (modales), `aria-busy` (cargas).
- [ ] **A11Y-10** ⬜ Formularios: etiqueta asociada, errores identificados en texto (3.3.1), instrucciones (3.3.2), sugerencia de corrección (3.3.3), confirmación/revisión en acciones con consecuencias (3.3.4), `autocomplete` en datos personales (1.3.5).
- [ ] **A11Y-11** ⬜ **Alternativa accesible a los mapas**: lista/tabla equivalente con los mismos datos (los mapas no son operables por lector de pantalla).
- [ ] **A11Y-12** ⬜ Gráficos de incentivos con tabla de datos o descripción textual alternativa.
- [ ] **A11Y-13** ⬜ Sesión que vence: aviso previo con opción de extenderla (2.2.1 Tiempo ajustable).
- [ ] **A11Y-14** ⬜ Animaciones respetan `prefers-reduced-motion`; nada parpadea > 3 veces/seg (2.3.1). Carrusel con pausa (2.2.2).
- [ ] **A11Y-15** ⬜ Verificación: axe/Lighthouse sin violaciones AA en todas las páginas + prueba manual con lector de pantalla (VoiceOver/NVDA) de 2 flujos críticos + checklist WCAG documentada.

### 1.9 Interacción móvil: hit area 48×48 px `[Maq]`
- [ ] **TAP-01** ⬜ Token `--hit-min: 48px`; todo botón, link de acción, ícono clickeable, checkbox/radio (con su label), pill clickeable, chip de filtro y marcador de mapa tiene área táctil ≥ 48×48 (padding o pseudo-elemento si el ícono es chico).
- [ ] **TAP-02** ⬜ Separación ≥ 8 px entre objetivos táctiles adyacentes.
- [ ] **TAP-03** ⬜ Links dentro de texto: interlineado suficiente o alternativa en botón.
- [ ] **TAP-04** ⬜ Test automatizado (Playwright) que mida el bounding box de los interactivos en 375 px.

### 1.10 Estilo visual unificado: design tokens `[Maq]`
- [ ] **TOK-01** ✅ Tokens de color (primitivos + semánticos: primario, superficie, texto, borde, éxito, alerta, error, info) en una sola fuente (`tokens.css`).
- [ ] **TOK-02** ✅ Tokens de tipografía (familia, escala de tamaños, pesos, interlineados).
- [ ] **TOK-03** ⬜ Tokens de espaciado (escala 4/8 px), de bordes (radios), sombras/elevación, z-index, duraciones/easings de animación, breakpoints.
- [ ] **TOK-04** ⬜ Tokens semánticos de estado: un color + ícono por cada estado de donación (7), de entrega (4), de camión, de necesidad, de propuesta.
- [ ] **TOK-05** ⬜ Página de documentación del design system (tokens + componentes con variantes y estados) — evidencia de "sistema".
- [ ] **TOK-06** ✅ Test que impide colores fuera de tokens (`FuenteUnicaDeColorTest`) — extender a espaciados/radios.
- [ ] **TOK-07** ⬜ Paridad Figma ↔ código: los mismos tokens en variables de Figma.

### 1.11 Componentes reutilizables (inventario mínimo) `[Imp]`
- [ ] **CMP-01** Botón (primario, secundario, terciario, peligro; tamaños; estado cargando/deshabilitado).
- [ ] **CMP-02** Campos: texto, número con unidad, fecha, select, select dependiente (categoría→subcategoría), autocompletar, radio-cards, checkbox, textarea con contador, subida de archivo con preview y progreso.
- [ ] **CMP-03** Pill/badge de estado (con ícono), chip de filtro removible.
- [ ] **CMP-04** Lista filtrable (barra de filtros + contador + resultados + paginación + vacío).
- [ ] **CMP-05** Tabla responsive (→ tarjetas en móvil) con orden por columna.
- [ ] **CMP-06** Tarjeta de donación, de entidad, de necesidad (con barra de cobertura), de camión, de misión (con progreso), de insignia.
- [ ] **CMP-07** Línea de tiempo de estados (historial/auditoría).
- [ ] **CMP-08** Toast, modal, banner, tooltip, desplegable, tabs, breadcrumb, stepper.
- [ ] **CMP-09** Skeleton, spinner, barra de progreso.
- [ ] **CMP-10** Mapa (marcadores, popup, leyenda, "última actualización") + lista alternativa.
- [ ] **CMP-11** Gráfico simple (barras/línea) con tabla alternativa.
- [ ] **CMP-12** Galería de fotos con lightbox accesible.
- [ ] **CMP-13** Avatar + menú de cuenta; campana de notificaciones con contador.

### 1.12 Estados de cada vista con datos `[Imp]`
- [ ] **EST-01** ✅ Cargando · vacío · error (con reintentar) · éxito · demo, en toda vista que consuma API.
- [ ] **EST-02** ✅ Falla parcial: si un servicio cae, el resto de la página sigue funcionando (inicio del donante = donaciones + incentivos + notificaciones de servicios distintos).
- [ ] **EST-03** ⬜ Páginas de error propias: 403, 404, 500, 503 "servicio en mantenimiento".
- [ ] **EST-04** ⬜ **429 Too Many Requests** (rate limiting de E7): mensaje "Demasiados intentos, probá de nuevo en N segundos" y no reintentar automáticamente en bucle. `[E7]`
- [ ] **EST-05** ⬜ Timeout de API con mensaje claro y reintento.

### 1.13 Arquitectura del cliente (implicancias de UI) `[E6]`
- [ ] **MVC-01** ✅ Proyecto independiente con motor de plantillas (Thymeleaf); vistas renderizadas con datos de las APIs REST.
- [ ] **MVC-02** ✅ Sin lógica de negocio en el cliente: estados, asignaciones, segmentación los decide el backend; la UI solo los muestra/solicita.
- [ ] **MVC-03** ⬜ El servidor front obtiene y adjunta el token del servicio de autenticación en cada llamada a los servicios; la UI nunca expone tokens al navegador (cookie de sesión `HttpOnly`, `Secure`, `SameSite`).
- [ ] **MVC-04** ⬜ Protección CSRF en todos los formularios.
- [ ] **MVC-05** ⬜ Progressive enhancement: los flujos críticos funcionan sin JS; el JS mejora (filtros dependientes, mapa, polling).
- [ ] **MVC-06** ⬜ Actualización en tiempo real de mapas por polling/SSE desde el front (si se elige gRPC en E7, el front lo consume del lado servidor). `[E7]`

### 1.14 Legal y privacidad `[Ley 25.326] [Ley 26.653] [Mín 1.iii]`
- [ ] **LEG-01** ✅ Página de información legal y privacidad, accesible desde el pie de todas las páginas.
- [ ] **LEG-02** ✅ Consentimiento informado obligatorio en el registro (checkbox no pre-marcado, link a la política).
- [ ] **LEG-03** ⬜ Consentimiento **separado y opcional** para: aparecer en destacadas/mapa público, mostrar categoría junto al nombre, ser **mencionado en redes sociales** al ganar una insignia (flujo n8n de E2), aparecer en el ranking público. `[E2] [Imp]`
- [ ] **LEG-04** ⬜ Minimización en lo público: donante humano como "Nombre I." o apodo; nunca DNI, dirección, teléfono.
- [ ] **LEG-05** ⬜ Derechos de acceso, rectificación y supresión: desde el perfil, ver/editar datos y solicitar baja de cuenta.
- [ ] **LEG-06** ⬜ Fotos de entregas: aviso a la entidad de no subir fotos con rostros identificables de menores (hogar de niños, escuelas) o de no publicarlas sin autorización; checkbox de confirmación al subir. `[Imp]`
- [ ] **LEG-07** ⬜ Declaración de accesibilidad (Ley 26.653) en el pie.
- [ ] **LEG-08** ⬜ Banner de cookies solo si se usan cookies no esenciales (si solo hay sesión, informarlo en privacidad).

---

## 2. Acceso público (visitante)

### 2.1 Landing page `[Mín 1]` ✅ (en revisión)
- [ ] **PUB-01** ✅ Hero con propósito de DonaTrack y propuesta de valor en una frase.
- [ ] **PUB-02** ✅ Sección "Cómo funciona" (donás en el depósito → se asigna → se entrega → ves la foto).
- [ ] **PUB-03** ✅ Objetivos/impacto ("Transparencia en números"). ⚠️ Sin endpoint de métricas públicas (brecha G7).
- [ ] **PUB-04** ✅ **Destacadas del último mes**: carrusel de donaciones entregadas en los últimos 30 días, con foto, categoría, entidad receptora y donante (si consintió). Definir criterio de "destacada" (B3).
- [ ] **PUB-05** ✅ Link a galería de fotos de donaciones entregadas **sin login** `[Mín 1.i]`.
- [ ] **PUB-06** ✅ CTAs de registro diferenciados: "Quiero donar" / "Soy entidad beneficiaria", y "Ingresar" `[Mín 1.ii]`.
- [ ] **PUB-07** ✅ Link a información legal y de privacidad `[Mín 1.iii]`.
- [ ] **PUB-08** ⬜ Link al **mapa de donaciones entregadas** `[Mín 2]` (hoy "Próximamente").
- [ ] **PUB-09** ⬜ Top 3 del ranking mensual de donantes en una sección visible `[E2: "destacará a los tres primeros en una sección visible de la aplicación"]` — decidir si en landing (público) o solo en área privada.
- [ ] **PUB-10** ⬜ Información del depósito: dirección, horarios, qué se recibe (sin esto, el visitante no sabe cómo donar, porque la donación se entrega físicamente). `[E1] [Imp]`
- [ ] **PUB-11** ⬜ SEO/compartibilidad básica: `meta description`, Open Graph (es la página a la que "aterriza" una campaña).

### 2.2 Galería de donaciones entregadas `[Mín 1.i]` ✅
- [ ] **PUB-12** ✅ Grilla de fotos de donaciones con estado Entregada, sin login.
- [ ] **PUB-13** ✅ Filtro por categoría.
- [ ] **PUB-14** ⬜ Filtros por período y por entidad/zona.
- [ ] **PUB-15** ⬜ Detalle/lightbox: fotos, subcategoría, cantidad, entidad, fecha de entrega, donante (si consintió).
- [ ] **PUB-16** ⬜ Paginación o "cargar más".
- [ ] **PUB-17** ⬜ Fotos reales (depende de la subida de fotos de la entidad — brecha G5).

### 2.3 Mapa interactivo de donaciones entregadas `[Mín 2]` ⬜
- [ ] **MAP-01** ⬜ Mapa con un marcador por entrega realizada (ubicación = dirección de la entidad beneficiaria). Requiere geocodificar direcciones (brecha G4).
- [ ] **MAP-02** ⬜ Click/tap en marcador → popup/panel con detalle: entidad, subcategoría y cantidad, fecha, foto miniatura, link al detalle.
- [ ] **MAP-03** ⬜ Agrupación de marcadores (clustering) cuando hay muchos en una zona.
- [ ] **MAP-04** ⬜ Filtros: categoría, período.
- [ ] **MAP-05** ⬜ Leyenda e indicador de cantidad de entregas visibles.
- [ ] **MAP-06** ⬜ Lista sincronizada con el mapa como alternativa accesible (A11Y-11).
- [ ] **MAP-07** ⬜ Marcadores operables con teclado y con hit area de 48 px.
- [ ] **MAP-08** ⬜ Estado de carga (skeleton del mapa) y de error (mapa no disponible → solo lista).

### 2.4 Registro `[Mín 3] [E1]` ✅ (en revisión)
- [ ] **REG-01** ✅ Selección de tipo de cuenta: donante humana · donante jurídica · entidad beneficiaria (preseleccionado si viene de un CTA).
- [ ] **REG-02** ✅ Humana: nombre*, apellido*, fecha de nacimiento (⚠️ el enunciado dice *edad*; se calcula — B2), tipo y número de documento*, género, dirección.
- [ ] **REG-03** ✅ Medios de contacto: correo **obligatorio**; teléfono y WhatsApp opcionales; **elegir cuál es el predeterminado** para notificaciones (solo entre los cargados).
- [ ] **REG-04** ✅ Jurídica: razón social*, tipo* (Gubernamental / ONG / Empresa / Institución), rubro*, ≥ 1 medio de contacto, ≥ 1 representante (agregar más).
- [ ] **REG-05** ✅ Entidad beneficiaria: razón social*, **dirección completa*** (calle, número, piso/depto, localidad, provincia, CP — se usa para rutas y mapa), teléfono*, correos de representantes*.
- [ ] **REG-06** ⬜ Validaciones de formato en cliente: DNI (7–8 dígitos), CUIT (11 dígitos + dígito verificador), correo, teléfono (E.164 / +54).
- [ ] **REG-07** ⬜ Contraseña (si no es social): requisitos visibles mientras se escribe, mostrar/ocultar, confirmación.
- [ ] **REG-08** ⬜ Opción "Registrarse con Google" (SSO social) que precompleta nombre y correo y pide el resto. `[E6]`
- [ ] **REG-09** ✅ Consentimiento de privacidad obligatorio + consentimientos opcionales (LEG-03).
- [ ] **REG-10** ⬜ Verificación de correo (link) antes de habilitar la cuenta — o justificar por qué no.
- [ ] **REG-11** ⬜ Detección de duplicado ("Ese correo ya está registrado · Ingresar / Recuperar contraseña"), relevante porque admin y CSV también crean cuentas.
- [ ] **REG-12** ✅ Pantalla de confirmación con siguiente paso; estado parcial reintentable.
- [ ] **REG-13** ⬜ Entidad: aviso de que su cuenta puede requerir validación por un admin antes de recibir donaciones *(decidir si existe aprobación)*.

### 2.5 Ingreso y autenticación `[Mín 4] [E6]`
- [ ] **AUT-01** 🟡 Login único para los 3 roles (correo + contraseña); el rol se resuelve después del ingreso y redirige al inicio correspondiente. Hoy: sesión demo con selector de rol.
- [ ] **AUT-02** ⬜ **SSO con social login** (ej. Google) — entregable explícito de E6. Botón con patrón estándar del proveedor.
- [ ] **AUT-03** ⬜ Vincular cuenta social a una cuenta existente con el mismo correo (caso: donante creado por admin o CSV que luego entra con Google).
- [ ] **AUT-04** ⬜ **Primer acceso**: link del correo de bienvenida (alta por admin `[E1]`) o credenciales del CSV `[E1]` → pantalla para definir contraseña; si recibió contraseña temporal, forzar cambio.
- [ ] **AUT-05** ⬜ "Olvidé mi contraseña": pedir correo → mensaje neutro (no revelar si existe) → link con vencimiento → nueva contraseña.
- [ ] **AUT-06** ⬜ Errores de login genéricos ("Correo o contraseña incorrectos"); bloqueo/espera tras N intentos con mensaje claro (relacionado con rate limiting E7).
- [ ] **AUT-07** ✅ Cerrar sesión desde el menú de cuenta.
- [ ] **AUT-08** ⬜ Aviso de sesión por vencer y redirección con "volver a donde estabas" (NAV-07, A11Y-13).
- [ ] **AUT-09** ⬜ Elegir organización si el usuario representa a más de una (ROL-05).

---

## 3. Área común a roles con sesión

### 3.1 Perfil y cuenta `[E1] [E2] [Imp]`
- [ ] **PRF-01** ⬜ Ver y editar datos personales / de la organización.
- [ ] **PRF-02** ⬜ Gestionar medios de contacto (agregar/quitar teléfono, WhatsApp) y **cambiar el medio predeterminado** de notificaciones.
- [ ] **PRF-03** ⬜ Organizaciones (jurídica/entidad): ver, invitar y quitar representantes.
- [ ] **PRF-04** ⬜ Cambiar contraseña / ver cuenta social vinculada.
- [ ] **PRF-05** ⬜ Gestionar consentimientos (LEG-03) y solicitar baja de cuenta (LEG-05).
- [ ] **PRF-06** ⬜ Donante humana: categoría visible junto al nombre (y en todo lugar público donde aparezca el nombre) `[E2]`.
- [ ] **PRF-07** ⬜ Donante: **visibilidad de insignias** (mostrar/ocultar cada una en el perfil) `[E2]`.

### 3.2 Centro de notificaciones `[Mín 8] [Mín 13]`
Las notificaciones salen por correo/SMS/WhatsApp; la bandeja en la app es implicancia (para que sean consultables).
- [ ] **NOT-01** 🟡 Campana en la barra superior con contador de no leídas (⚠️ backend no soporta "leída", G8).
- [ ] **NOT-02** 🟡 Página de notificaciones: lista cronológica, ícono por tipo, fecha relativa + absoluta, link al objeto (donación, entrega, misión).
- [ ] **NOT-03** ⬜ Filtro por tipo (asignaciones, entregas, incentivos).
- [ ] **NOT-04** ⬜ Marcar como leída / todas como leídas.
- [ ] **NOT-05** ✅ Últimas 3 en el inicio del rol.
- [ ] **NOT-06** ⬜ Indicar por qué medio se envió cada notificación.

---

## 4. Persona donante

### 4.1 Inicio (tablero) ✅
- [ ] **DON-01** ✅ Resumen: donaciones realizadas/entregadas, categoría actual, misión activa con progreso, última donación con su recorrido, notificaciones recientes.
- [ ] **DON-02** ⬜ Bloque de "entregas activas" con acceso directo al mapa de seguimiento (si hay alguna En traslado).
- [ ] **DON-03** ⬜ Si no tiene donaciones: estado vacío con "Cómo donar" (depósito, horarios) y link a entidades con necesidades.

### 4.2 Mis donaciones `[Mín 5]` ✅
- [ ] **DON-04** ✅ Menú/página "Mis donaciones" con lista de **donaciones segmentadas** (una por subcategoría).
- [ ] **DON-05** ✅ Filtro por **estado** con los 7 estados: En depósito · Asignación realizada · Lista para entregar · En traslado · Entregada · Entrega fallida · Vencida.
- [ ] **DON-06** ✅ Filtro por **categoría** y por **subcategoría/s** (cascada; ⚠️ el enunciado dice "subcategoría/s" → **selección múltiple** de subcategorías, hoy es simple).
- [ ] **DON-07** ⬜ Filtros combinables, chips de filtros activos, "Limpiar filtros", contador de resultados anunciado.
- [ ] **DON-08** ⬜ Orden (fecha desc por defecto) y paginación.
- [ ] **DON-09** ⬜ Filtro por período/fecha de donación. `[Imp]`
- [ ] **DON-10** ⬜ Vista agrupada por **donación original** (lo que llevó al depósito un día) → sus segmentadas. El donante piensa en "lo que doné el martes", no en segmentos. `[E1] [Imp]`

### 4.3 Detalle de donación ✅
- [ ] **DON-11** ✅ Historial de estados en orden cronológico con fecha/hora (trazabilidad `[E2]`).
- [ ] **DON-12** ✅ Justificación visible si hubo Entrega fallida.
- [ ] **DON-13** ⬜ Entidad beneficiaria asignada (nombre, link a su perfil) cuando la asignación está realizada.
- [ ] **DON-14** ⬜ Bienes: descripción, subcategoría, cantidad + unidad, nuevo/usado, vencimiento, foto.
- [ ] **DON-15** ⬜ Si está En traslado: link "Seguir en el mapa".
- [ ] **DON-16** ⬜ Si está Entregada: **comprobante de entrega** (fecha, hora, camión) y **fotos subidas por la entidad**. `[E3]`

### 4.4 Entidades beneficiarias `[Mín 6]` 🟡
- [ ] **DON-17** ⬜ Listado navegable de entidades registradas (tarjetas: nombre, tipo, localidad, necesidades activas).
- [ ] **DON-18** ⬜ Búsqueda por nombre.
- [ ] **DON-19** ⬜ Filtros: subcategoría/categoría de necesidad, tipo de necesidad (recurrente/extraordinaria), zona/localidad.
- [ ] **DON-20** ⬜ Destacar necesidades extraordinarias/urgentes.
- [ ] **DON-21** ⬜ Detalle de entidad: datos públicos (sin datos de contacto personales de los representantes), necesidades activas con **barra de cobertura** (recibido / requerido) y período en las recurrentes, donaciones recibidas (fotos).
- [ ] **DON-22** ⬜ CTA "Quiero donar esto" → instrucciones de cómo llevarlo al depósito (no se dona online). `[Imp]`
- [ ] **DON-23** ⬜ Vista alternativa en mapa de entidades (opcional, Could).

### 4.5 Incentivos (solo persona humana) `[Mín 7] [E2]` ✅
- [ ] **INC-01** ✅ Categoría actual (Colaborador → Sostenedor → Transformador) y recorrido hacia la siguiente.
- [ ] **INC-02** ✅ **Misiones a realizar**: lista por categoría, en orden secuencial; bloqueadas con candado y "se desbloquea al completar X".
- [ ] **INC-03** ✅ Misión actual con **progreso y distancia restante** ("Te faltan 2 meses de racha") `[E2: "en todo momento"]`.
- [ ] **INC-04** ⬜ Aviso de **riesgo de pérdida de progreso** en misiones tipo Racha ("Doná antes del 31/10 para no perder tu racha de 3 meses") y registro de cuando se perdió. `[E2]`
- [ ] **INC-05** ✅ **Insignias obtenidas** (con fecha y misión de origen) vs. pendientes, diferenciadas por algo más que color.
- [ ] **INC-06** ⬜ Configurar visibilidad de cada insignia (PRF-07).
- [ ] **INC-07** ⬜ Compartir/ver la publicación en redes de una insignia (link al post generado por n8n). `[E2]`

### 4.6 Métricas / estadísticas del donante `[E2]` ⬜
Lo pide el dominio de E2 para el perfil (no figura en los mínimos de UI, pero "podrá observar en su perfil").
- [ ] **MET-01** ⬜ Totales históricos de donaciones (cantidad de donaciones y de bienes).
- [ ] **MET-02** ⬜ Gráfico de evolución por período (mensual) con selector de rango.
- [ ] **MET-03** ⬜ Comparación mes actual vs. mes anterior (✅ parcial en el tile del inicio).
- [ ] **MET-04** ⬜ Número total de organizaciones ayudadas.
- [ ] **MET-05** ⬜ Posición en el ranking de donantes activos.
- [ ] **MET-06** ⬜ Indicadores de impacto acumulado (ej. por categoría donada).
- [ ] **MET-07** ⬜ Alternativa textual/tabla para cada gráfico (A11Y-12).

### 4.7 Seguimiento de entregas activas `[Mín 9]` 🟡
- [ ] **TRK-01** ⬜ Mapa con las entregas activas (En traslado) de **sus** donaciones.
- [ ] **TRK-02** ⬜ Marcador de cada camión asociado con **ubicación** y **hora de última actualización** ("hace 2 min").
- [ ] **TRK-03** ⬜ Marcador del destino (entidad) y, opcional, recorrido/paradas en orden.
- [ ] **TRK-04** ⬜ Actualización automática (polling/SSE) sin recargar la página; indicador "en vivo" y aviso si la posición está desactualizada (> N min).
- [ ] **TRK-05** ⬜ Si hay varios camiones/entregas: lista lateral para elegir cuál ver.
- [ ] **TRK-06** ⬜ Estado vacío: "No tenés entregas en curso" + próximas (Lista para entregar).
- [ ] **TRK-07** ⬜ Aterrizaje desde el link de la notificación "inicio de ruta" `[E3]` directo a esa entrega.
- [ ] **TRK-08** ⬜ Lista accesible equivalente (A11Y-11).

### 4.8 Notificaciones que recibe `[Mín 8]` — ver matriz §8.

---

## 5. Entidad beneficiaria

### 5.1 Inicio 🟡
- [ ] **ENT-01** ⬜ Resumen: necesidades activas con cobertura, donaciones asignadas próximas, entregas en curso hoy, notificaciones.
- [ ] **ENT-02** ⬜ Alerta destacada si hay una entrega En traslado hacia la entidad (para que esté atenta a recibir) con CTA "Confirmar recepción".

### 5.2 Necesidades materiales `[Mín 10] [E1]` 🟡
- [ ] **NEC-01** ⬜ Lista de necesidades con: subcategoría, tipo (recurrente/extraordinaria), cantidad requerida, **cobertura** (cantidad recibida, barra), período (recurrentes), estado (activa / satisfecha / cerrada).
- [ ] **NEC-02** ⬜ Filtros: tipo, estado, categoría.
- [ ] **NEC-03** ⬜ **Alta de necesidad**: categoría → subcategoría* (cascada), descripción breve*, tipo*, cantidad* + unidad.
- [ ] **NEC-04** ⬜ Si **recurrente**: período (semanal/mensual/…) y cantidad objetivo por período; fecha de inicio/fin opcional. Ayuda contextual que cambia según el tipo.
- [ ] **NEC-05** ⬜ Si **extraordinaria**: motivo (inundación, mudanza, incendio…), fecha límite opcional, marca de urgencia.
- [ ] **NEC-06** ⬜ Explicar las donaciones parciales: "Podés recibir varias donaciones hasta completar 30 sillas".
- [ ] **NEC-07** ⬜ Editar necesidad (con restricciones si ya tiene asignaciones) y eliminar/cerrar con confirmación.
- [ ] **NEC-08** ⬜ Detalle de necesidad: donaciones que la fueron cubriendo; en recurrentes, historial por período.
- [ ] **NEC-09** ⬜ Evitar duplicados: aviso si ya hay una necesidad activa de la misma subcategoría.

### 5.3 Donaciones asignadas `[Mín 11]` 🟡
- [ ] **ENT-03** ⬜ Lista de donaciones asignadas a la entidad con estado (Asignación realizada · Lista para entregar · En traslado · Entregada · Entrega fallida/No recibida).
- [ ] **ENT-04** ⬜ Filtros por estado, categoría y fecha; contador.
- [ ] **ENT-05** ⬜ Detalle: bienes, cantidad, vencimiento, necesidad que cubre, donante (si consintió), fecha estimada de entrega, camión, historial de estados.
- [ ] **ENT-06** ⬜ Acción "Seguir en el mapa" si está En traslado.

### 5.4 Confirmar recepción `[Mín 12] [E3]` 🟡
- [ ] **REC-01** ⬜ Acción "Confirmar recepción" disponible solo si la entrega está En traslado (y destacada en el inicio).
- [ ] **REC-02** ⬜ Paso 1 · Confirmación: qué se recibió (cantidad recibida si difiere → ¿se admite recepción parcial? *decidir*), modal de confirmación.
- [ ] **REC-03** ⬜ Paso 2 · **Subir fotos**: múltiples archivos, desde cámara o galería, preview con miniaturas, quitar foto, validación de tipo (JPG/PNG/HEIC→convertir) y tamaño, compresión, barra de progreso por archivo, texto alternativo/descripción. (El enunciado E3 dice que las fotos se cargan *luego* de confirmar → permitir subirlas más tarde con recordatorio.)
- [ ] **REC-04** ⬜ Aviso de privacidad sobre las fotos (LEG-06).
- [ ] **REC-05** ⬜ Resultado: toast + "Actualizando estado…" (consistencia eventual) → **comprobante** (fecha, hora, camión).
- [ ] **REC-06** ⬜ Alternativa "**No la recibí**" con justificación obligatoria (texto + motivos predefinidos) → estado No recibida; informar que un admin lo revisará.
- [ ] **REC-07** ⬜ Pendientes de foto: indicador "Tenés 2 entregas sin fotos" hasta que se suban.

### 5.5 Seguimiento `[Mín 14]` 🟡 — mismos requisitos que TRK-01…08, filtrado a las entregas que **la entidad debe recibir**.

### 5.6 Notificaciones `[Mín 13]` — ver §8.

---

## 6. Persona administradora

### 6.1 Inicio (tablero operativo) 🟡
- [ ] **ADM-01** ⬜ Indicadores agregados de evolución del sistema `[E2]`: donaciones por estado (en depósito, por vencer, en traslado hoy), entregas del día, necesidades sin cubrir, entregas fallidas/no recibidas pendientes de revisión, donantes nuevos del mes.
- [ ] **ADM-02** ⬜ Alertas: donaciones por vencer (próximos N días), casos "No recibida" sin revisar, importaciones CSV en curso/fallidas, propuestas de asignación pendientes de confirmar.
- [ ] **ADM-03** ⬜ Accesos rápidos: Registrar donación · Registrar donante · Asignaciones pendientes.
- [ ] **ADM-04** ⬜ Top 3 del ranking del mes.

### 6.2 Donantes `[Mín 15] [Mín 20]` 🟡
- [ ] **ADN-01** ⬜ Lista de donantes con búsqueda (nombre, razón social, DNI/CUIT, correo), filtro por tipo (humana/jurídica) y categoría; paginación (más de 20.000 registros tras el CSV).
- [ ] **ADN-02** ⬜ **Registrar donante** en el depósito: mismo formulario que REG (humana/jurídica), sin contraseña; al guardar, aviso "Se envió el correo de bienvenida a …" `[E1]`.
- [ ] **ADN-03** ⬜ Verificación de existencia previa (por correo/documento) antes de crear: "Ya está registrado · Usar este donante".
- [ ] **ADN-04** ⬜ Detalle de donante: datos, contacto, donaciones, categoría; editar; reenviar correo de bienvenida.
- [ ] **ADN-05** ⬜ Acción "Registrar donación para este donante" desde su detalle.

### 6.3 Importación masiva por CSV `[Mín 20] [E1]` 🟡
- [ ] **CSV-01** ⬜ Pantalla de importación con explicación del formato (TipoPersona, TipoDoc, Documento, Nombre/Razón social, Email, Teléfono) y **plantilla descargable**.
- [ ] **CSV-02** ⬜ Subida por arrastrar o elegir archivo; validación de extensión, tamaño máximo y encabezados **antes** de procesar.
- [ ] **CSV-03** ⬜ Vista previa de las primeras filas y resumen detectado (N filas).
- [ ] **CSV-04** ⬜ Confirmación ("Se crearán o actualizarán ~20.000 donantes y se enviarán credenciales a los nuevos").
- [ ] **CSV-05** ⬜ Procesamiento **asíncrono** (> 10.000 filas no puede ser una request bloqueante): la UI muestra progreso (filas procesadas / total), y el admin puede navegar mientras tanto (banner persistente).
- [ ] **CSV-06** ⬜ Resultado: creados · actualizados (correo ya existente) · con error; detalle de errores por fila con motivo; **descarga del CSV de errores** para corregir y reimportar.
- [ ] **CSV-07** ⬜ Historial de importaciones (fecha, archivo, admin, resultado).
- [ ] **CSV-08** ⬜ Notificación al terminar (toast/banner + entrada en notificaciones del admin).

### 6.4 Registrar donación recibida en el depósito `[Mín 15] [E1]` 🟡
- [ ] **ADD-01** ⬜ Paso 1 · **Donante**: buscador con autocompletar; si no existe, "Registrar nuevo donante" sin salir del flujo (modal o paso intermedio); resumen del donante elegido.
- [ ] **ADD-02** ⬜ Paso 2 · **Datos generales**: descripción general*, fecha de recepción (hoy por defecto), depósito.
- [ ] **ADD-03** ⬜ Paso 3 · **Bienes** (N filas, "+ Agregar bien"): descripción*, categoría* → subcategoría*, cantidad* + **unidad*** (kg, unidades, litros…), foto opcional.
- [ ] **ADD-04** ⬜ Campo **Nuevo/Usado** que aparece solo si la categoría lo requiere (mobiliario, vestimenta).
- [ ] **ADD-05** ⬜ Campo **Fecha de vencimiento** que aparece (y es obligatorio) solo si la categoría es perecedera; validar fecha futura.
- [ ] **ADD-06** ⬜ Duplicar fila de bien (cargar rápido mismos productos con distinto vencimiento).
- [ ] **ADD-07** ⬜ Paso 4 · **Revisión + previsualización de la segmentación**: "Se generarán 3 donaciones: Fideos secos (100 u), Tomate (50 u, vence 01/01/2027)…", agrupadas por subcategoría y separadas por vencimiento/estado. `[E1]`
- [ ] **ADD-08** ⬜ Confirmación → resultado con las donaciones segmentadas creadas (estado En depósito) y link a cada una.
- [ ] **ADD-09** ⬜ "Guardar y registrar otra" (conserva depósito/fecha) y "Registrar otra del mismo donante".
- [ ] **ADD-10** ⬜ Borrador recuperable si se pierde la conexión o se sale sin querer (aviso "tenés cambios sin guardar").
- [ ] **ADD-11** ⬜ Si el backend tiene normalización de ítems con revisión pendiente: pantalla de **revisión de normalización** (F15 de la spec) con sugerencia + aceptar/corregir.

### 6.5 Donaciones en depósito / inventario `[Mín 16] [E2]` 🟡
- [ ] **ADV-01** ⬜ Lista de donaciones con filtro por estado (por defecto En depósito), categoría/subcategoría, donante, rango de vencimiento.
- [ ] **ADV-02** ⬜ Columna/indicador de **vencimiento** con resaltado de vencidas y "por vencer".
- [ ] **ADV-03** ⬜ Acción **"Marcar como vencida"** (solo En depósito), con modal de confirmación y motivo opcional; también en lote.
- [ ] **ADV-04** ⬜ Detalle con **auditoría completa**: historial de estados con fecha, actor y justificación. `[E2]`
- [ ] **ADV-05** ⬜ Ver donación original y sus segmentadas.
- [ ] **ADV-06** ⬜ Acciones inválidas según el estado, deshabilitadas con tooltip explicando por qué.

### 6.6 Asignación de donaciones (matchmaking) `[Mín 17] [E2]` 🟡
- [ ] **ASG-01** ⬜ Lista de donaciones **pendientes de asignación** (En depósito con resultados de algoritmos disponibles).
- [ ] **ASG-02** ⬜ Botón "Ejecutar algoritmos ahora" (ejecución a demanda, `[E2 REST]`) con progreso y aviso de que normalmente corre en horario de baja carga; fecha de la última ejecución.
- [ ] **ASG-03** ⬜ Detalle de una donación pendiente: resultado de los algoritmos:
  - Si hay **coincidencias** entre ambos algoritmos: lista filtrada de entidades que aparecen en los dos.
  - Si **no** hay coincidencias: ambas listas (Compatibilidad semántica / Prioridad a sub-atendidos), cada una con hasta **10** entidades en orden de ranking.
- [ ] **ASG-04** ⬜ Por entidad candidata: posición, puntaje/motivo (qué necesidad coincide, cuántas donaciones recibió en el trimestre), cantidad que cubre de su necesidad, ubicación.
- [ ] **ASG-05** ⬜ Tooltip que explica qué prioriza cada algoritmo (HLP-04).
- [ ] **ASG-06** ⬜ **Seleccionar la entidad final** → modal de confirmación → estado Asignación realizada → toast "Se notificó a la entidad y al donante".
- [ ] **ASG-07** ⬜ Descartar/posponer (queda En depósito) con motivo opcional.
- [ ] **ASG-08** ⬜ Estado vacío: "No hay donaciones pendientes" o "No hay entidades con necesidades compatibles".
- [ ] **ASG-09** ⬜ Historial de ejecuciones de los algoritmos.

### 6.7 Camiones `[Mín 18] [E3]` 🟡
- [ ] **CAM-01** ⬜ Lista de camiones: patente, capacidad de volumen (m³), altura (m), carga (kg), estado (Disponible / En ruta / Deshabilitado).
- [ ] **CAM-02** ⬜ Alta: patente* (validación formato AR viejo `ABC123` y Mercosur `AB123CD`, única), volumen*, altura*, carga* (números > 0 con unidad visible).
- [ ] **CAM-03** ⬜ Editar y dar de baja/deshabilitar con motivo y confirmación; impedir baja si está En ruta.
- [ ] **CAM-04** ⬜ Detalle: ruta del día, entregas asignadas, última ubicación conocida.
- [ ] **CAM-05** ⬜ Vista de flota en mapa (camiones en ruta en tiempo real) — Could, reusa TRK.

### 6.8 Rutas y entregas `[E3] [Imp]` ⬜ (no está en los mínimos, pero el admin "revisa" casos)
- [ ] **RUT-01** ⬜ Lista de rutas planificadas para el día siguiente / del día (camión → paradas ordenadas → entregas), con estado.
- [ ] **RUT-02** ⬜ **Casos a revisar**: entregas No recibida / Entrega fallida con justificación; acción "Volvió al depósito" (→ Pendiente) para replanificar.
- [ ] **RUT-03** ⬜ Registrar justificación de entrega fallida si la carga el admin (ej. "Tocamos timbre y nadie respondió") `[E2]`.
- [ ] **RUT-04** ⬜ Selector/indicador del proveedor de logística activo (broker E4) — solo si se decide exponerlo en la UI.

### 6.9 Rankings `[Mín 19] [E2]` 🟡
- [ ] **RNK-01** ⬜ Ranking del mes (o del último mes cerrado): posición, donante, categoría, misiones cumplidas en el período; **podio de 3** destacado.
- [ ] **RNK-02** ⬜ **Historial**: selector de período (mes/año) para ver rankings anteriores.
- [ ] **RNK-03** ⬜ Estado vacío por período sin ranking.
- [ ] **RNK-04** ⬜ Indicar fecha de cálculo ("calculado el 01/10 a las 00:05").
- [ ] **RNK-05** ⬜ Exportar a CSV (Could).

### 6.10 Otros de admin `[Imp]`
- [ ] **ADO-01** ⬜ Entidades beneficiarias: lista, detalle, (aprobar si se decide REG-13), ver necesidades.
- [ ] **ADO-02** ⬜ Catálogo de categorías/subcategorías (y si requieren estado nuevo/usado o vencimiento) — Could, pero alimenta ADD-04/05.
- [ ] **ADO-03** ⬜ Notificaciones del admin: entregas no satisfactorias `[E3]`, importaciones terminadas.

---

## 7. Chofer *(decisión pendiente)* `[E3]`
"Las rutas asignadas quedan disponibles para los choferes en su aplicación" y "el chofer deberá indicar en el sistema que dará por inicio su ruta". No figura en los mínimos de UI. Opciones: (a) sin UI, se resuelve por API/Postman; (b) vista móvil mínima.
- [ ] **CHO-01** ⬜ Ver la ruta del día: paradas ordenadas con dirección y entregas por parada.
- [ ] **CHO-02** ⬜ Botón "Iniciar ruta" con confirmación → entregas pasan a En traslado y se disparan las notificaciones de inicio.
- [ ] **CHO-03** ⬜ Compartir ubicación (geolocalización del navegador) para alimentar el seguimiento en tiempo real.
- [ ] **CHO-04** ⬜ Marcar entrega fallida en una parada con justificación.

---

## 8. Matriz de notificaciones (contenido y destino en la UI)
Cada fila implica: plantilla de mensaje (correo/SMS/WhatsApp), entrada en el centro de notificaciones y **link a una pantalla concreta**.

| # | Evento | Destinatario | Contenido mínimo | Link a | Origen |
|---|---|---|---|---|---|
| N1 | Bienvenida (alta por admin) | Donante | Saludo + link de primer acceso | AUT-04 | `[E1]` |
| N2 | Credenciales (alta por CSV) | Donante nuevo | Usuario + forma de definir contraseña | AUT-04 | `[E1]` |
| N3 | Inactividad > 20 días | Donante | Invitación a donar + necesidades destacadas | DON-17 / PUB-10 | `[E2]` |
| N4 | Donación asignada | Donante | Qué donación y a qué entidad | DON-11 | `[Mín 8] [E2]` |
| N5 | Donación asignada | Entidad | Qué recibe, qué necesidad cubre | ENT-05 | `[Mín 13] [E2]` |
| N6 | Misión cumplida | Donante | Misión, insignia, siguiente misión | INC-02 | `[Mín 8] [E2]` |
| N7 | Cambio de categoría | Donante | Nueva categoría | INC-01 | `[Mín 8] [E2]` |
| N8 | Inicio de ruta | Donantes y entidades de la ruta | Aviso + **link al mapa en tiempo real** | TRK-07 | `[E3]` |
| N9 | Entrega exitosa | Donante y entidad | **Comprobante**: fecha, hora, camión | DON-16 | `[Mín 8] [Mín 13] [E3]` |
| N10 | Entrega no satisfactoria | Donante, entidad y **admins** | Motivo + si se replanifica | DON-11 / RUT-02 | `[E3]` |
| N11 | Insignia obtenida | (publicación en red social) | Imagen + mención al usuario | INC-07 | `[E2]` (requiere consentimiento LEG-03 y usuario de red social en el perfil) |
| N12 | Importación CSV terminada | Admin | Resumen | CSV-06 | `[Imp]` |

- [ ] **NOT-07** ⬜ Plantillas de correo con la misma identidad visual (tokens) y accesibles (texto plano alternativo).
- [ ] **NOT-08** ⬜ Los links de notificación funcionan con o sin sesión iniciada (NAV-07).
- [ ] **NOT-09** ⬜ El canal usado es el medio predeterminado del usuario (PRF-02).

---

## 9. Entregables de UI que piden las entregas
- [ ] **ENT-UI-01** 🟡 Bocetos de las interfaces principales (E1) — v0 + Figma.
- [ ] **ENT-UI-02** ⬜ Implementación de esos bocetos en el cliente liviano con navegabilidad entre interfaces y estilos (E6.1, E6.4).
- [ ] **ENT-UI-03** ⬜ SSO con social login (E6.3).
- [ ] **ENT-UI-04** ⬜ Evidencias de cumplimiento de "Consideraciones para el Maquetado": sitemap con profundidad, evaluación heurística, reporte axe/Lighthouse, matriz cross-device, documentación de tokens y patrones.
- [ ] **ENT-UI-05** ⬜ Diagrama de casos de uso actualizado con los roles de §0 (E1.4).

---

## 10. Contradicciones con lo ya decidido en la spec (a revisar)
| Tema | Spec actual | Enunciado | Impacto |
|---|---|---|---|
| Mapas (W01) | Won't MVP / "Próximamente" | Mínimos 2, 9 y 14 | **Obligatorios**; requieren coordenadas y posición de camión (G4) |
| Subida de fotos (W02) | Won't MVP | Mínimo 12 + galería pública 1.i | **Obligatoria**; requiere multipart (G5) |
| Auth real (W03) | "hasta E6" | E6 pide servicio de auth + **SSO con social login** | Es *esta* entrega (la UI es E6, no E5) |
| CSV | Ruta en servidor | Mínimo 20: "desde archivos CSV" (> 10.000 filas) | Subida desde el navegador + procesamiento asíncrono (G6) |
| Incentivos jurídica | Igual para todos | Mín 7: "persona **humana** donante" | Ocultar misiones/insignias a jurídicas (ROL-03) |
| Subcategoría en filtros | Simple | "subcategoría/s" | Selección múltiple (DON-06) |
| Top 3 ranking | Solo admin | E2: "sección visible de la aplicación" | Mostrar también a donantes/público (PUB-09) |

## 11. Decisiones abiertas
1. ¿La jurídica ve métricas aunque no vea misiones/insignias? (ROL-03)
2. ¿Una cuenta puede tener varios roles/organizaciones? (ROL-05)
3. ¿Hay UI de chofer o se resuelve por API? (§7)
4. ¿Las entidades necesitan aprobación de un admin? (REG-13)
5. ¿Se admite recepción parcial (cantidad distinta a la enviada)? (REC-02)
6. ¿Top 3 del ranking visible en la landing pública? (PUB-09)
7. ¿Social login para todos los roles o solo donantes? (AUT-02, ROL-06)
8. ¿Se expone en la UI el proveedor de logística del broker? (RUT-04)
