# frontend-donatrack

Prototipo de frontend para DonaTrack (UTN-FRBA · Diseño de Sistemas 2026), anticipado a la Entrega 5.

## Cómo levantarlo

Requisitos: Java 21 y Maven 3.9.

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Abre en http://localhost:8090. El perfil `dev` lee plantillas y CSS desde `src/` (los cambios se ven al recargar).
Ingreso: `/ingresar` con identidades de demostración (no hay usuarios reales hasta la Entrega 6).

Pruebas y formato:

```bash
mvn test
mvn spotless:apply
```

## Documentación

- [Progreso y decisiones](docs/PROGRESO.md)
- [01 · Auditoría](docs/01-auditoria.md)
- [02 · Especificación funcional y MVP](docs/02-especificacion-funcional.md)
- [03 · Arquitectura y design system](docs/03-arquitectura.md)
- [Backlog](docs/BACKLOG.md)
- [Metodología](docs/METODOLOGIA.md)
