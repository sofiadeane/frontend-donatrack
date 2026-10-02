# Metodología de trabajo

Proceso liviano para una desarrolladora con Claude Code. Sin ceremonias que no aporten.

## Marco

| Enfoque | Uso en el proyecto | Artefacto |
|---|---|---|
| Double Diamond — Descubrir | Auditoría de repo, diseño y requisitos | `01-auditoria.md` |
| Double Diamond — Definir | Especificación, journeys, MoSCoW | `02-especificacion-funcional.md` |
| Double Diamond — Desarrollar | Arquitectura, design system, prototipo | `03-arquitectura.md` (Etapa 3) |
| Double Diamond — Entregar | Implementación incremental + QA | código + `PROGRESO.md` |
| Lean UX | Hipótesis explícitas validadas con prototipos | §Hipótesis |
| Kanban | Flujo de trabajo con límite de WIP | `BACKLOG.md` |

## Hipótesis Lean UX (a validar, sin resultados todavía)

| ID | Hipótesis | Señal de validación |
|---|---|---|
| H1 | Ver el historial de estados en el detalle aumenta la confianza del donante | En prueba de pasillo, 4 de 5 personas explican dónde está su donación sin ayuda |
| H2 | Confirmar recepción desde el detalle es más rápido que una página separada | Tarea completada en ≤ 3 interacciones |
| H3 | El estado "Actualizando…" evita interpretar la consistencia eventual como error | Nadie reporta "no funcionó" tras confirmar |
| H4 | Registrar ítems de a uno con resultado de normalización inmediato reduce correcciones posteriores | Menos ítems en revisión por donación (medible con datos de prueba) |

## Kanban

Estados: **Backlog → Listo → En curso → Revisión → Hecho**. WIP máximo en *En curso*: **2** (una historia + una corrección).

### Definition of Ready
- Historia vinculada a un requisito (`R#`) o a una decisión registrada.
- Criterios de aceptación escritos y verificables.
- Endpoints identificados en el código, o adapter/fixture acordado.
- Diseño de referencia disponible (Figma o especificación de componente).

### Definition of Done
- Cumple los criterios de aceptación y los estados de UI comunes (cargando, vacío, error, éxito).
- Funciona con teclado, foco visible, sin errores de axe de nivel serio o crítico.
- Probado en 360, 768 y 1280 px.
- Sin errores en consola; fixtures etiquetados.
- Checks del proyecto en verde; cambio revisado (auto-revisión con checklist).
- `PROGRESO.md` actualizado si hubo decisión o cambio de alcance.

## Iteraciones

- Duración: 1 semana, con un objetivo de iteración escrito en `PROGRESO.md`.
- Checkpoint de revisión al cierre de cada etapa y antes de decisiones de arquitectura (aprobación de la usuaria).
- Retro de 3 líneas por iteración: qué funcionó, qué no, qué cambiamos.

## Git

- `main` siempre desplegable; ramas cortas `feat/…`, `fix/…`, `docs/…`.
- Commits convencionales (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`).
- Un incremento = un journey o componente verificable de forma independiente.

## Plan de validación

| Dimensión | Método | Cuándo |
|---|---|---|
| Funcional | Criterios de aceptación por journey (manual + tests automatizados donde aplique) | Cada historia |
| Integración API | Adapters tipados contra DTOs del código; contraste con backend levantado localmente | Cada adapter |
| Accesibilidad | axe, navegación con teclado, contraste (WCAG 2.2 AA), lector de pantalla en flujos clave | Cada historia + Etapa 5 |
| Responsive | 360 / 768 / 1280 px | Cada historia |
| Usabilidad | Prueba de pasillo con 3–5 personas sobre H1–H4 | Fin de Etapa 4 |
| Performance | Lighthouse en páginas públicas | Etapa 5 |

Los resultados de validación se registran en `PROGRESO.md`; no se reportan resultados que no se hayan medido.
