# Progreso y decisiones

## Estado

| Etapa | Estado |
|---|---|
| 1. Auditoría | ✅ Aprobada — [`01-auditoria.md`](01-auditoria.md) |
| 2. Especificación funcional y MVP | ✅ Aprobada — [`02-especificacion-funcional.md`](02-especificacion-funcional.md) · [`BACKLOG.md`](BACKLOG.md) |
| 3. Arquitectura, API y design system | ✅ Aprobada — [`03-arquitectura.md`](03-arquitectura.md) · iteración visual en `design/exploraciones/` |
| 4. Implementación incremental | 🚧 En curso — E0 y E1 Público en revisión · sigue E2 Donante |
| 5. QA | ⏳ |
| 6. Animaciones post-MVP | ⏳ |

Proceso: [`METODOLOGIA.md`](METODOLOGIA.md).

## Registro de decisiones

| Fecha | Decisión | Motivo |
|---|---|---|
| 2026-10-01 | Figma (Prototipo v2, `334:925`) reemplaza a `disenioFigma.html` como referencia visual | El export HTML perdió estilos y tipografías |
| 2026-10-01 | Repo oficial `DonaTrack/` y `study/` en solo lectura; trabajo en `frontend-donatrack` | Pedido de la Sofiadeane |
| 2026-10-01 | Mapa interactivo y tracking de camiones: TBD | Sin coordenadas ni posiciones en el backend; enunciado E5 pendiente |
| 2026-10-01 | Sesión simulada con selector de rol hasta E6 | No existe auth-service |
| 2026-10-01 | Auditoría aprobada | Sofiadeane |
| 2026-10-01 | Stack aprobado: Spring Boot 4 + Spring MVC + Thymeleaf, CSS/JS nativos (D1) | Sofiadeane |
| 2026-10-01 | Solo DM Sans; paleta del Figma sin cambios en fuente única `tokens.css`; ajustes de contraste y colores de estado pendientes (D4) | Sofiadeane |
| 2026-10-01 | Identidad del hero en todo el sitio, con gradientes y efectos (D5); sesión demo (D3); puerto 8090 (D7) | Sofiadeane |
| 2026-10-01 | Adapters demo/real conmutables (D2); Playwright + axe (D6); proyecto `cliente-liviano`, paquete `grupo5.clienteliviano` (D7) | Sofiadeane |
| 2026-10-01 | Destacados: "Realizadas" = estilo A con mini gráfico real por mes (sin texto) que previsualiza la futura sección Estadísticas; "Entregadas" = estilo B con barra segmentada | Sofiadeane |
| 2026-10-01 | Barra superior: logo como botón de inicio a la izquierda; campana y avatar (solo iniciales) a la derecha. Móvil: navegación inferior fija con 5 secciones (notificaciones por la campana). Mini gráfico a ancho completo, sin línea punteada, barras finas | Sofiadeane |
| 2026-10-01 | Mini gráfico: barras finas, separación chica, grupo centrado en el tile. Móvil: botón Buscar (lupa) en la barra superior, junto a la campana, que abre un panel de búsqueda; navegación inferior con 5 secciones. Barras del mini gráfico alineadas a la derecha | Sofiadeane |
| 2026-10-01 | Categoría: semicírculo oscuro con flecha que se expande (hover en escritorio, toque en celular) y revela la próxima categoría. Móvil: gráfico más grande, "Entregadas" antes que "Realizadas", recorrido vertical con línea punteada | Sofiadeane |
| 2026-10-01 | Barra lateral de escritorio colapsable a riel de íconos (estado recordado por navegador). Hora de notificaciones centrada verticalmente con más margen derecho | Sofiadeane |
| 2026-10-01 | google-java-format 1.28.0 (estilo GOOGLE): la 1.22.0 del backend falla con el JDK 21.0.12 instalado | Técnica |
| 2026-10-01 | El ingreso renueva la sesión (evita fijación de sesión) y la sesión viaja solo por cookie | Técnica |
| 2026-10-01 | Etiquetas con punto (eyebrow): se ajustan a su contenido; 15 px a la izquierda del punto y 12 px entre punto y texto. Barra lateral sin etiqueta de rol: solo menú y botón para plegar (el rol queda en el menú de la cuenta) | Sofiadeane |
| 2026-10-01 | DM Sans autoalojada (woff2 variable, subconjuntos latin y latin-ext, OFL 1.1); sin dependencias de CDN | Sofiadeane autorizó la descarga |
| 2026-10-01 | Los commits los hace la Sofiadeane; Claude no commitea | Sofiadeane |
| 2026-10-01 | Todos los pills de estado con el mismo ancho (11rem = 176 px, el más largo mide 160 px) | Sofiadeane |
| 2026-10-01 | Demo en GitHub Pages: export estático (opción 1) generado por la app en perfil `export` y publicado por Actions | Sofiadeane |
| 2026-10-01 | Landing v3: lado derecho del hero libre (animación Etapa 6); destacadas estilo foto con título ajustado a su espacio, ícono de categoría, sin pill "Entregada" y con donante; degradé a ambos lados del carrusel; números alternativa A (tiles que se despliegan desde arriba); roles con botones en negativo y despliegue lateral | Sofiadeane |
| 2026-10-01 | Barra pública de vidrio adaptativa: vidrio oscuro con texto claro sobre el hero, vidrio claro en el resto y apenas una superficie clara del hero (tarjeta "Cómo funciona", marcada con `data-fondo-claro`) toca la barra (sin JS queda el claro). A confirmar; alternativa: barra sólida | Propuesta |
| 2026-10-01 | Donante en destacadas: persona humana como "Nombre I." (minimización de datos personales); persona jurídica con razón social. Se obtiene de `GET /api/donaciones/{id}` (máx. 8 llamadas; si falla, la tarjeta sale sin donante). Pendiente: consentimiento explícito de aparecer públicamente | Propuesta |
| 2026-10-01 | Registro (H1.3): una página por secciones, todos los campos del DTO visibles, domicilio opcional para todos los tipos; exploración visual antes de implementar | Usuaria |
| 2026-10-01 | Color de error: rojo terroso `--red-600 #b9443b` (único primitivo fuera del Figma; AA 5,3:1 sobre blanco) con fondo `--red-50`. Las secciones con errores se marcan enteras en rojo (borde, título, pill con la cantidad y nodo de la ruta lateral) | Usuaria |
| 2026-10-01 | Registro: organizaciones y entidades con mínimo un representante (se pueden agregar más). Privacidad obligatoria (solo en la interfaz). Consentimiento para aparecer en destacadas y apodo opcional: se piden pero no se envían hasta acordarlo con backend | Usuaria |
| 2026-10-01 | Inicio del donante = tablero de la exploración aprobada (hero con red, tiles realizadas/entregadas/categoría, recorrido de la última donación, misión y notificaciones) con datos reales de donaciones, incentivos y notificaciones | Usuaria |
| 2026-10-02 | Tile "Donaciones entregadas": moneda + título en dos líneas, barra y leyenda 2×2. Chip del tile realizadas: diferencia con el mes pasado o "¡Igual que el mes pasado!" | Usuaria |
| 2026-10-02 | Listas: columnas fijas universales (estado y fecha); el pill abraza su texto a la izquierda, la fecha va a la derecha; ícono por estado | Usuaria |
| 2026-10-02 | Nodos encendidos: el espacio con los vecinos se mide desde el borde del aura (`--aura-nodo`) | Usuaria |
| 2026-10-02 | Hero de la landing: texto al 50 % en escritorio, ancho completo hasta 1024 px. Barra de vidrio confirmada | Usuaria |
| 2026-10-02 | Contraste AA aprobado: texto teal sobre la banda verde (también su botón), números teal en círculos, enlaces de la barra al 78 % de tinta, fechas en gris de texto secundario | Usuaria |
| 2026-10-02 | Tiles con error: alternativa C (esqueleto + aviso "Sin conexión" + Reintentar con flecha curva); el mensaje completo queda para lectores de pantalla | Usuaria |
| 2026-10-02 | Pills de estado: base A (tinte); falta elegir tinte o sólidos en `design/exploraciones/pills-y-cifras.html` | Usuaria |
| 2026-10-02 | Incentivos: categorías alcanzadas plegadas; próxima categoría separada (borde punteado, candado, nota). Título de la misión a la derecha. Cifra de la comparación en verde y negrita | Usuaria |
| 2026-10-01 | Especificación aprobada: MVP F01–F19, fusiones de navegación, TBD visibles, vocabulario del backend | Usuaria |
| 2026-10-01 | Registro (H1.3): una página por secciones, todos los campos del DTO visibles, domicilio opcional para todos los tipos; exploración visual antes de implementar | Sofiadeane |
| 2026-10-01 | Color de error: rojo terroso `--red-600 #b9443b` (único primitivo fuera del Figma; AA 5,3:1 sobre blanco) con fondo `--red-50`. Las secciones con errores se marcan enteras en rojo (borde, título, pill con la cantidad y nodo de la ruta lateral) | Sofiadeane |
| 2026-10-01 | Registro: organizaciones y entidades con mínimo un representante (se pueden agregar más). Privacidad obligatoria (solo en la interfaz). Consentimiento para aparecer en destacadas y apodo opcional: se piden pero no se envían hasta acordarlo con backend | Sofiadeane |
| 2026-10-01 | Especificación aprobada: MVP F01–F19, fusiones de navegación, TBD visibles, vocabulario del backend | Sofiadeane |

## Pendientes abiertos

- Enunciado de la Entrega 5 (define stack/SSR).
- Validar la nueva barra superior y la navegación móvil en la exploración.
- Elegir en `design/exploraciones/pills-y-cifras.html`: pills en tinte (A) o sólidos (A′), y las cifras de incentivos (X1 trayecto, X2 integradas en la misión, X3 tablero de logros).
- Sonar: los paquetes `grupo5.*` no cumplen la regla de nombres (`^[a-z_]+…`) por el dígito de `grupo5`, que es el paquete base del equipo; marcar como "won't fix" o ajustar la regla.
- Decisión sobre hallazgos de contraste y colores de estado (riesgo AA documentado). Ya visibles en la landing: título blanco sobre la banda verde, texto blanco en los círculos turquesa, links del nav (ink 62 %), fecha de tarjetas (ink 40 %) y etiquetas de los chips (blanco 60 %).
- Backend: guardar el consentimiento para aparecer en destacadas y el apodo del donante (hoy el registro los pide pero no los envía).
- Confirmar barra de vidrio adaptativa (o pasar a sólida) y el formato público del nombre del donante; el backend no registra consentimiento para mostrarlo.
- Sin Docker local: el adapter HTTP se valida contra un servidor simulado con respuestas del backend; falta probarlo contra el backend real levantado.

- Ejecutar la app desde el panel de vista previa requiere `.claude/launch.json` en `DSI/` (fuera de `FrontEnd/`); por ahora se levanta con `mvn spring-boot:run`.

## Log

- **2026-10-01** · Auditoría de solo lectura de `DonaTrack/`, `FrontEnd/`, `study/`, Figma y v0 desplegado.
- **2026-10-01** · Docs movidos al repo `frontend-donatrack/docs/`. Borrador de especificación funcional y metodología.
- **2026-10-01** · Backlog generado. Design context del Figma leído completo; assets exportados a `design/figma-assets/`. Propuesta de arquitectura y design system.
- **2026-10-01** · Exploración visual del área privada (`design/exploraciones/area-privada-donante.html`), verificada en 1366 px y 390 px.
- **2026-10-01** · **E0 · incremento 1**: proyecto `cliente-liviano` (Spring Boot 4.0.7, Thymeleaf), `tokens.css` como fuente única de color, CSS base/layout/componentes, layout público y privado (barra superior, barra lateral colapsable, navegación inferior, búsqueda móvil), ingreso de demostración con identidades en fixtures, guarda por rol, secciones "en construcción". Tests: 9/9 en verde (sesión, guarda, navegación, 404, salida, fuente única de color). Verificado en navegador a 1280 px y 375 px.
- **2026-10-01** · **E0 · incremento 2**: capa de integración (`integracion/`): configuración por servicio con modo `fixture`/`http`, `ClienteHttp` que clasifica fallas (`NO_DISPONIBLE`, `NO_ENCONTRADO`, `VALIDACION`, `CONFLICTO`, `ERROR`) conservando el `ErrorResponse` del backend, encabezado `X-Trace-Id`, `LectorFixtures` estricto. Primer par de adapters: donaciones (`GET /donaciones-independientes`), DTOs copiados del código. Capa `aplicacion/`: `Seccion` (OK/vacío/error/demo), traducción de errores a textos (`messages.properties`), estados de donación con etiquetas en español. Inicio del donante con "Tus últimas donaciones". DM Sans autoalojada. Tests: 19/19 (adapter HTTP contra servidor simulado, contrato de fixtures, estados OK/vacío/error en la página). Verificado en navegador: datos demo (1280 y 375 px) y servicio real caído (estado de error, resto de la página operativo).
- **2026-10-01** · **E1 · incremento 1 (landing)**: barra pública del Figma (menú plegable en celular, abierto sin JS), hero con red de ruta estática en SVG inline (clases-gancho para la Etapa 6) y chips de vidrio, "Cómo funciona" horizontal / zigzag (variante Figma) / vertical, "Donaciones destacadas del último mes" desde `DonacionesPort` (entregadas en los últimos 30 días, reloj inyectable), carrusel con scroll-snap y botones, "Transparencia en números", llamado a la acción y pie. Métricas: puerto con adapter solo de demostración (sin endpoint, brecha G7). Mapa de impacto: página TBD explícita. Tests: 23/23. Verificado en 1340, 820 y 375 px sin scroll horizontal.
- **2026-10-01** · Exploraciones de la landing v2 y v3 (`design/exploraciones/`), formato "Recorrido en vivo" guardado para los mapas. Export estático para GitHub Pages: perfil `export` (context-path del repo, fixtures, reloj fijo, enlaces en lugar de formularios), `scripts/exportar_estatico.py` (crawler por rol) y workflow `pages.yml`. Rutas de CSS pasadas a relativas. El export detectó un enlace roto real (campana de admin → `/admin/notificaciones`): se agregó la sección. Probado localmente: 28 páginas, sin errores, servidas bajo `/frontend-donatrack/`. Tests: 23/23.
- **2026-10-01** · **E1 · landing v3** en la app: hero con escena vacía, destacadas con donante e ícono de categoría (`IconoCategoria`), números como tiles desplegables (componente `desplegable` reutilizable: hover solo con puntero fino, botón con `aria-expanded`, foco, `prefers-reduced-motion`), roles con despliegue lateral y más aire entre viñetas y CTA, barra de vidrio adaptativa. DTO parcial `DonacionOutputDTO` y fixture `donaciones.json`; métricas como lista. Corregido scroll horizontal en tablet (los `.sr-only` de las tarjetas escapaban del carrusel). Tests: 26/26 (donante, ícono, nombre público, contrato de fixtures de originales y métricas). Verificado en 1340, 768 y 375 px sin scroll horizontal.
- **2026-10-01** · Barra pública: el vidrio oscuro exige que el hero cubra toda la barra (antes alcanzaba con tocarla). Exploración `design/exploraciones/registro.html`: tipo de cuenta como tarjetas-radio sobre la banda, ruta lateral de secciones, datos según tipo con `:has()` (sin JS), contacto con medio preferido, domicilio opcional, consentimiento, estados con errores / éxito / parcial (CA3).
- **2026-10-01** · **E1 · registro (H1.3)**: `RegistroPort` (`POST /api/personas`, `/api/donantes`, `/api/entidades`) con adapters real y demo, DTOs de entrada copiados del backend (medios de contacto polimórficos). Formulario de una página sin JS (tipo con `:has()`, representantes con botones de envío, PRG a la confirmación), validación en el cliente con las reglas del backend, errores del backend mapeados a cada campo (`direccion.calle`, `mediosDeContacto[i]`, `representantes[i]`, códigos ERR-VAL-1xx), paso parcial reintentable guardado en sesión (CA3). En el export estático el botón queda deshabilitado. Tests: 36/36.
- **2026-10-01** · **E1 · privacidad (H1.2) y galería (H1.5)**: `/privacidad` con resumen en tarjetas y ruta lateral (contenido del v0 + datos que se piden, qué se ve públicamente, derechos según Ley 25.326); `/donaciones-entregadas` con las 24 entregas más recientes, filtro por categoría (enlaces GET) y estados vacío/error. Tarjeta de donación extraída a `fragments/donacion.html` (landing y galería). Banda del hero reutilizable (`.banda-hero`). Tests: 38/38. Export estático: 28 páginas sin errores.
- **2026-10-01** · **E2 · mis donaciones (H2.1) y detalle (H2.2)**: `/donante/donaciones` con filtros por estado (los 7 de `TipoEstadoDonacion`), categoría y subcategoría (`GET /api/categorias`; el backend filtra por estado y subcategoría, la categoría sola se filtra en el cliente), contador anunciado y vacío con "Limpiar filtros". `/donante/donaciones/{id}`: historial cronológico como ruta vertical, ítems con categoría, cantidad, estado y vencimiento; solo se muestran donaciones propias (404 si es ajena). Filas de la lista enlazan al detalle (también en el inicio). `VistaDonaciones` concentra estados/unidades. Fixture `categorias.json`. Tests: 45/45. Export estático: 34 páginas (incluye los detalles).
- **2026-10-01** · **E2 · inicio del donante (tablero)**: puertos `IncentivosPort` (`GET /api/incentivos/donantes/{id}` y `/metricas`; 404 = sin incentivos) y `NotificacionesPort` (`GET /api/notificaciones/persona/{personaId}`) con adapters real y demo, fixtures `incentivos/donantes.json` y `notificaciones/notificaciones.json`. `PanelDonanteService` arma cada bloque por separado (`Bloque<T>`): si incentivos o notificaciones fallan, el recorrido y las entregas siguen. Mini gráfico SVG generado en el servidor (6 meses, mes actual destacado), barra segmentada, categoría con el componente desplegable, recorrido horizontal/vertical con estado interrumpido en rojo, misión con barra de progreso, notificaciones con tiempo relativo. Estilos en `privado.css`. Fix: padding de las tarjetas-radio dentro de un campo. Tests: 48/48. Export: 34 páginas.
- **2026-10-02** · Correcciones de la revisión (tile entregadas, pills con ícono y columnas fijas, aura de nodos, hero 50 %), donación de octubre en los datos demo, comparación con el mes pasado. Exploración `estados-y-contraste.html` (ratios WCAG calculados). **E2 · incentivos (H2.4)**: `IncentivosPort` suma `/misiones` y `/ascensos`; `/donante/incentivos` con recorrido de categorías en el hero, misión activa, cifras (misiones, insignias, puesto en el ranking), insignias derivadas de las misiones (ganadas con brillo y fecha; pendientes punteadas con candado y la misión que las desbloquea, CA2), misiones agrupadas por categoría, vacío explicativo si el donante no está en incentivos (CA3). Tests: 55/55.
- **2026-10-02** · Correcciones de Sonar: correo validado sin regex (lineal), constantes para literales repetidos, record patterns en el controlador de registro, `<output>` en lugar de `role="status"`, teléfono como `fieldset` con etiquetas, ids literales en los campos de representantes, fragmentos `<li>` dentro de una lista, Javadoc colgante. Contraste AA y tiles con error C aplicados. Exploración `pills-y-cifras.html`. Tests: 66/66.
