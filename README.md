# frontend-donatrack

Prototipo de frontend para DonaTrack (UTN-FRBA · Diseño de Sistemas 2026), anticipado a la Entrega 5.

## Cómo levantarlo

Requisitos: Java 21 y Maven 3.9.

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Abre en http://localhost:8090. El perfil `dev` lee plantillas y CSS desde `src/` (los cambios se ven al recargar).

### Datos: demostración o backend real

Cada servicio del backend se conecta por un adapter que puede ser de **demostración** (`fixture`, datos ficticios de `src/main/resources/fixtures`) o **real** (`http`). Por defecto todo es demostración y la interfaz lo indica con la etiqueta "Datos de demostración".

| Variable | Valores | Por defecto |
|---|---|---|
| `DONACIONES_MODO`, `NOTIFICACIONES_MODO`, `INCENTIVOS_MODO`, `LOGISTICA_MODO` | `fixture` · `http` | `fixture` |
| `DONACIONES_URL`, `NOTIFICACIONES_URL`, `INCENTIVOS_URL`, `LOGISTICA_URL` | URL base del servicio | `localhost:8080` … `8083` |

Con el backend levantado (Docker), el perfil `local` pasa todos los servicios a `http`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
```
Ingreso: `/ingresar` con identidades de demostración (no hay usuarios reales hasta la Entrega 6).

Pruebas y formato:

```bash
mvn test
mvn spotless:apply
```

## Demo estática en GitHub Pages

El workflow [`.github/workflows/pages.yml`](.github/workflows/pages.yml) publica una versión estática del prototipo en cada push a `main` (o a mano desde la pestaña Actions):

1. Compila y corre los tests (`mvn verify`).
2. Levanta la app con el perfil `export`: datos de demostración, fecha fija (1/10/2026) y la app bajo `/<nombre-del-repo>/`.
3. [`scripts/exportar_estatico.py`](scripts/exportar_estatico.py) recorre los enlaces internos (inicio público y el panel de cada rol, con una identidad demo por rol) y guarda cada página como HTML. También publica las exploraciones de `design/` en `/exploraciones/`.
4. Publica `dist/` en Pages.

**Configuración única:** en el repo, *Settings → Pages → Source: GitHub Actions*. Si el repo es privado, Pages requiere un plan pago.

**Limitaciones de la versión estática:** no hay servidor, así que no funcionan el ingreso con formulario, la búsqueda ni ningún envío de datos. "Ingresar" muestra enlaces a los tres paneles prerenderizados.

Probarlo localmente:

```bash
mvn -B verify
java -jar target/cliente-liviano-0.1.0-SNAPSHOT.jar --spring.profiles.active=export
python3 scripts/exportar_estatico.py
```

El resultado queda en `dist/` (servilo bajo `/frontend-donatrack/`).

## Variantes visuales (exploración)

Seis direcciones nuevas, solo para la landing y el panel de administración. El resto de las pantallas sigue con la versión anterior.

| Ruta | Variante |
|---|---|
| `/` y `/admin` | **v6 · "Cartografía"** (G): pergamino, sepia y latón, brújula, numeración romana, bitácora |
| `/f` y `/admin/f` | **v5 · "Cristal"** (F): vidrio esmerilado, degradés pastel, acento lima |
| `/e` y `/admin/e` | **v4 · "Calma"** (E): minimalista y aireada, salvia y crema, títulos serif, líneas finas |
| `/d` y `/admin/d` | **v3 · "Trazo"** (D): dibujo lineal en tinta, relleno naranja y amarillo, mascota propia |
| `/c` y `/admin/c` | **v2 · "Suave"** (C): plana, redondeada, pastel sobre crema |
| `/b` y `/admin/b` | **v1 · "Ruta"** (B): papel y tinta, bordes gruesos, neobrutalista |
| `/v1` | Landing original |

Para el panel, ingresar como "Administración Depósito"; los datos salen de `fixtures/admin/panel.json`.

Archivos: C es la base estructural (`c-base.css`, `c-landing.css`, `c-panel.css`, plantillas `*-c.html`). D, E, F y G reutilizan esa estructura y la remapean con `d-theme.css`, `e-theme.css`, `f-theme.css` y `g-theme.css` (tokens `--d-*` a `--g-*`); sus plantillas son `*-d.html` a `*-g.html`. B tiene `b-*.css` y `*-b.html`. Todas comparten `static/js/b.js`; los tokens están al final de `tokens.css`.

## Documentación

- [Progreso y decisiones](docs/PROGRESO.md)
- [01 · Auditoría](docs/01-auditoria.md)
- [02 · Especificación funcional y MVP](docs/02-especificacion-funcional.md)
- [03 · Arquitectura y design system](docs/03-arquitectura.md)
- [Backlog](docs/BACKLOG.md)
- [Metodología](docs/METODOLOGIA.md)
