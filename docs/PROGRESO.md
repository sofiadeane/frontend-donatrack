# Progreso y decisiones

## Estado

| Etapa | Estado |
|---|---|
| 1. Auditoría | ✅ Aprobada — [`01-auditoria.md`](01-auditoria.md) |
| 2. Especificación funcional y MVP | ✅ Aprobada — [`02-especificacion-funcional.md`](02-especificacion-funcional.md) · [`BACKLOG.md`](BACKLOG.md) |
| 3. Arquitectura, API y design system | ✅ Aprobada — [`03-arquitectura.md`](03-arquitectura.md) · iteración visual en `design/exploraciones/` |
| 4. Implementación incremental | 🚧 En curso — E0 Fundaciones, incremento 1 hecho |
| 5. QA | ⏳ |
| 6. Animaciones post-MVP | ⏳ |

Proceso: [`METODOLOGIA.md`](METODOLOGIA.md).

## Registro de decisiones

| Fecha | Decisión | Motivo |
|---|---|---|
| 2026-10-01 | Figma (Prototipo v2, `334:925`) reemplaza a `disenioFigma.html` como referencia visual | El export HTML perdió estilos y tipografías |
| 2026-10-01 | Repo oficial `DonaTrack/` y `study/` en solo lectura; trabajo en `frontend-donatrack` | Pedido de la usuaria |
| 2026-10-01 | Mapa interactivo y tracking de camiones: TBD | Sin coordenadas ni posiciones en el backend; enunciado E5 pendiente |
| 2026-10-01 | Sesión simulada con selector de rol hasta E6 | No existe auth-service |
| 2026-10-01 | Auditoría aprobada | Usuaria |
| 2026-10-01 | Stack aprobado: Spring Boot 4 + Spring MVC + Thymeleaf, CSS/JS nativos (D1) | Usuaria |
| 2026-10-01 | Solo DM Sans; paleta del Figma sin cambios en fuente única `tokens.css`; ajustes de contraste y colores de estado pendientes (D4) | Usuaria |
| 2026-10-01 | Identidad del hero en todo el sitio, con gradientes y efectos (D5); sesión demo (D3); puerto 8090 (D7) | Usuaria |
| 2026-10-01 | Adapters demo/real conmutables (D2); Playwright + axe (D6); proyecto `cliente-liviano`, paquete `grupo5.clienteliviano` (D7) | Usuaria |
| 2026-10-01 | Destacados: "Realizadas" = estilo A con mini gráfico real por mes (sin texto) que previsualiza la futura sección Estadísticas; "Entregadas" = estilo B con barra segmentada | Usuaria |
| 2026-10-01 | Barra superior: logo como botón de inicio a la izquierda; campana y avatar (solo iniciales) a la derecha. Móvil: navegación inferior fija con 5 secciones (notificaciones por la campana). Mini gráfico a ancho completo, sin línea punteada, barras finas | Usuaria |
| 2026-10-01 | Mini gráfico: barras finas, separación chica, grupo centrado en el tile. Móvil: botón Buscar (lupa) en la barra superior, junto a la campana, que abre un panel de búsqueda; navegación inferior con 5 secciones. Barras del mini gráfico alineadas a la derecha | Usuaria |
| 2026-10-01 | Categoría: semicírculo oscuro con flecha que se expande (hover en escritorio, toque en celular) y revela la próxima categoría. Móvil: gráfico más grande, "Entregadas" antes que "Realizadas", recorrido vertical con línea punteada | Usuaria |
| 2026-10-01 | Barra lateral de escritorio colapsable a riel de íconos (estado recordado por navegador). Hora de notificaciones centrada verticalmente con más margen derecho | Usuaria |
| 2026-10-01 | google-java-format 1.28.0 (estilo GOOGLE): la 1.22.0 del backend falla con el JDK 21.0.12 instalado | Técnica |
| 2026-10-01 | El ingreso renueva la sesión (evita fijación de sesión) y la sesión viaja solo por cookie | Técnica |
| 2026-10-01 | Etiquetas con punto (eyebrow): se ajustan a su contenido; 15 px a la izquierda del punto y 12 px entre punto y texto. Barra lateral sin etiqueta de rol: solo menú y botón para plegar (el rol queda en el menú de la cuenta) | Usuaria |
| 2026-10-01 | DM Sans autoalojada (woff2 variable, subconjuntos latin y latin-ext, OFL 1.1); sin dependencias de CDN | Usuaria autorizó la descarga |
| 2026-10-01 | Los commits los hace la usuaria; Claude no commitea | Usuaria |
| 2026-10-01 | Especificación aprobada: MVP F01–F19, fusiones de navegación, TBD visibles, vocabulario del backend | Usuaria |

## Pendientes abiertos

- Enunciado de la Entrega 5 (define stack/SSR).
- Validar la nueva barra superior y la navegación móvil en la exploración.
- Decisión sobre hallazgos de contraste y colores de estado (riesgo AA documentado).
- Sin Docker local: la integración con el backend real se valida con fixtures hasta tener entorno.

- Ejecutar la app desde el panel de vista previa requiere `.claude/launch.json` en `DSI/` (fuera de `FrontEnd/`); por ahora se levanta con `mvn spring-boot:run`.

## Log

- **2026-10-01** · Auditoría de solo lectura de `DonaTrack/`, `FrontEnd/`, `study/`, Figma y v0 desplegado.
- **2026-10-01** · Docs movidos al repo `frontend-donatrack/docs/`. Borrador de especificación funcional y metodología.
- **2026-10-01** · Backlog generado. Design context del Figma leído completo; assets exportados a `design/figma-assets/`. Propuesta de arquitectura y design system.
- **2026-10-01** · Exploración visual del área privada (`design/exploraciones/area-privada-donante.html`), verificada en 1366 px y 390 px.
- **2026-10-01** · **E0 · incremento 1**: proyecto `cliente-liviano` (Spring Boot 4.0.7, Thymeleaf), `tokens.css` como fuente única de color, CSS base/layout/componentes, layout público y privado (barra superior, barra lateral colapsable, navegación inferior, búsqueda móvil), ingreso de demostración con identidades en fixtures, guarda por rol, secciones "en construcción". Tests: 9/9 en verde (sesión, guarda, navegación, 404, salida, fuente única de color). Verificado en navegador a 1280 px y 375 px.
