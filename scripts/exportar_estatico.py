#!/usr/bin/env python3
"""
Exporta el cliente liviano a HTML estático para GitHub Pages.

Requiere la app levantada con el perfil `export` (fixtures, reloj fijo, context-path del repo):
    java -jar target/cliente-liviano-*.jar --spring.profiles.active=export

Recorre los enlaces internos desde el inicio público y desde el panel de cada rol (con una
identidad de demostración por rol) y guarda cada página como <ruta>/index.html. Copia los
estáticos y las exploraciones de diseño. Solo usa la biblioteca estándar de Python.

Uso: python3 scripts/exportar_estatico.py [--base http://localhost:8095/frontend-donatrack] [--salida dist]
"""

import argparse
import html.parser
import http.cookiejar
import pathlib
import re
import shutil
import sys
import urllib.error
import urllib.parse
import urllib.request

RAIZ = pathlib.Path(__file__).resolve().parent.parent
IDENTIDADES = {"donante": "donante-nicolas", "entidad": "entidad-girasoles", "admin": "admin-deposito"}
INICIOS_PUBLICOS = ["/", "/ingresar", "/registro", "/privacidad", "/mapa-de-impacto", "/donaciones-entregadas"]


class Enlaces(html.parser.HTMLParser):
    def __init__(self):
        super().__init__()
        self.hrefs = []

    def handle_starttag(self, tag, attrs):
        if tag == "a":
            for nombre, valor in attrs:
                if nombre == "href" and valor:
                    self.hrefs.append(valor)


def abridor(rol=None, base=""):
    jar = http.cookiejar.CookieJar()
    op = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar))
    if rol:
        datos = urllib.parse.urlencode({"identidad": IDENTIDADES[rol]}).encode()
        # El perfil export muestra enlaces en /ingresar, pero el POST sigue disponible para el exportador.
        op.open(urllib.request.Request(base + "/ingresar", data=datos, method="POST"), timeout=20)
    return op


def ruta_interna(href, prefijo):
    """Devuelve la ruta relativa al context-path si el enlace es una página interna; si no, None."""
    partes = urllib.parse.urlsplit(href)
    if partes.scheme or partes.netloc or not partes.path.startswith(prefijo):
        return None
    ruta = partes.path[len(prefijo):] or "/"
    if re.search(r"\.[a-z0-9]{2,5}$", ruta):  # archivos estáticos
        return None
    return ruta.rstrip("/") or "/"


def rol_de(ruta):
    for rol in IDENTIDADES:
        if ruta == "/" + rol or ruta.startswith("/" + rol + "/"):
            return rol
    return None


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--base", default="http://localhost:8095/frontend-donatrack")
    ap.add_argument("--salida", default=str(RAIZ / "dist"))
    args = ap.parse_args()
    base = args.base.rstrip("/")
    prefijo = urllib.parse.urlsplit(base).path
    salida = pathlib.Path(args.salida)
    if salida.exists():
        shutil.rmtree(salida)
    salida.mkdir(parents=True)

    abridores = {None: abridor()}
    for rol in IDENTIDADES:
        abridores[rol] = abridor(rol, base)

    pendientes = list(INICIOS_PUBLICOS) + ["/" + r for r in IDENTIDADES]
    vistas, errores = set(), []
    while pendientes:
        ruta = pendientes.pop(0)
        if ruta in vistas:
            continue
        vistas.add(ruta)
        try:
            with abridores[rol_de(ruta)].open(base + ruta, timeout=20) as resp:
                cuerpo = resp.read().decode("utf-8")
        except urllib.error.HTTPError as e:
            errores.append(f"{ruta} → HTTP {e.code}")
            continue
        destino = salida / ruta.lstrip("/") / "index.html" if ruta != "/" else salida / "index.html"
        destino.parent.mkdir(parents=True, exist_ok=True)
        destino.write_text(cuerpo, encoding="utf-8")
        parser = Enlaces()
        parser.feed(cuerpo)
        for href in parser.hrefs:
            interna = ruta_interna(href, prefijo)
            if interna and interna not in vistas:
                pendientes.append(interna)

    # Estáticos de la app (css, js, img, fonts) en la raíz del sitio, igual que con el servidor.
    shutil.copytree(RAIZ / "src/main/resources/static", salida, dirs_exist_ok=True)

    # Exploraciones de diseño: sus rutas absolutas pasan a relativas (están un nivel más abajo).
    (salida / "figma-assets").mkdir(exist_ok=True)
    shutil.copytree(RAIZ / "design/figma-assets", salida / "figma-assets", dirs_exist_ok=True)
    (salida / "exploraciones").mkdir(exist_ok=True)
    for pagina in (RAIZ / "design/exploraciones").glob("*.html"):
        texto = pagina.read_text(encoding="utf-8")
        texto = re.sub(r'(href|src)="/(?!/)', r'\1="../', texto)
        texto = texto.replace('url("/', 'url("../')
        (salida / "exploraciones" / pagina.name).write_text(texto, encoding="utf-8")

    (salida / ".nojekyll").write_text("")
    print(f"Exportadas {len(vistas) - len(errores)} páginas en {salida}")
    for e in errores:
        print("  aviso:", e, file=sys.stderr)
    return 1 if errores else 0


if __name__ == "__main__":
    sys.exit(main())
