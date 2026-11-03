"""Exporta la variante H en sus dos temas como páginas planas para el artifact: h-*.html (hormigas) e i-*.html (abejas).
Uso: python3 empaquetar_temas.py <repo> <salida> [base]"""
import http.cookiejar, pathlib, re, shutil, sys, urllib.parse, urllib.request

RAIZ, OUT = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2])
B = sys.argv[3] if len(sys.argv) > 3 else "http://localhost:8090"
P = urllib.parse.urlsplit(B).path  # context path ("" en dev)
ESTATICO = RAIZ / "src/main/resources/static"
TEMAS = {"h": ("hormigas", "/h"), "i": ("abejas", "/i")}

def nombre(pref, url):
    partes = urllib.parse.urlsplit(url)
    ruta = (partes.path[len(P):] if partes.path.startswith(P) else partes.path).rstrip("/") or "/"
    q = urllib.parse.parse_qs(partes.query)
    if ruta in ("/", "/h", "/i", "/ingresar", "/registro") and ruta != "/ingresar" and ruta != "/registro": return f"{pref}-inicio.html"
    if ruta in ("/ingresar", "/registro", "/donante"): return f"{pref}-donante.html"
    m = re.fullmatch(r"/donante/donaciones/[0-9a-f-]*?(\d{1,3})", ruta)
    if m: return f"{pref}-donante-donacion-{int(m.group(1))}.html"
    if ruta == "/donante/entidades" and "entidad" in q:
        return f"{pref}-donante-entidades-{q['entidad'][0][-4:]}.html"
    if ruta.startswith("/donante/") and "buscar" not in ruta:
        return f"{pref}-donante-" + ruta[len("/donante/"):].replace("/", "-") + ".html"
    return None

assets = set()
def reescribir(pref, html, es_landing):
    def href(m):
        attr, url = m.group(1), m.group(2)
        if url.startswith(("http", "#", "data:", "mailto:")): return m.group(0)
        url = url.replace("&amp;", "&")
        path, _, frag = url.partition("#")
        ruta = urllib.parse.urlsplit(path).path
        ruta = ruta[len(P):] if P and ruta.startswith(P) else ruta
        if re.match(r"/(css|js|img|fonts)/", ruta):
            assets.add(ruta.lstrip("/")); return f'{attr}="{ruta.lstrip("/")}"'
        if es_landing and ruta in ("/", "/h", "/i") and frag: return f'{attr}="#{frag}"'
        n = nombre(pref, path)
        if n is None: return f'{attr}="#contenido"'
        return f'{attr}="{n}{"#" + frag if frag else ""}"'
    html = re.sub(r'(href|src)="(/[^"]*)"', href, html)
    html = re.sub(r'action="/[^"]*"', 'action="#" onsubmit="return false"', html)
    return html

OUT.mkdir(parents=True, exist_ok=True)
total = 0
for pref, (tema, landing) in TEMAS.items():
    jar = http.cookiejar.CookieJar()
    op = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar))
    op.open(urllib.request.Request(B + "/ingresar", data=b"identidad=donante-nicolas", method="POST"))
    op.open(B + "/donante?tema=" + tema).read()
    paginas, pendientes = {}, [landing, "/donante"]
    while pendientes:
        r = pendientes.pop(0)
        n = nombre(pref, r)
        if n is None or n in paginas: continue
        html = op.open(B + r).read().decode()
        for l in re.findall(r'href="(' + re.escape(P) + r'/(?:donante|h|i)\b[^"#]*)', html):
            l = l.replace("&amp;", "&")
            if nombre(pref, l) and nombre(pref, l) not in paginas: pendientes.append(l[len(P):] if P else l)
        paginas[n] = reescribir(pref, html, r == landing)
    for n, html in paginas.items():
        (OUT / n).write_text(html, encoding="utf-8")
    total += len(paginas)
    print(pref, tema, len(paginas), "páginas")

cola = list(assets)
while cola:
    a = cola.pop()
    (OUT / a).parent.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(ESTATICO / a, OUT / a)
    if a.endswith(".css"):
        for u in re.findall(r'url\(["\']?(\.\./[^)"\'#]+)', (ESTATICO / a).read_text()):
            partes = []
            for p in pathlib.PurePosixPath("css/" + u).parts:
                partes.pop() if p == ".." else partes.append(p)
            dep = "/".join(partes)
            if dep not in assets: assets.add(dep); cola.append(dep)
print(total, "páginas;", len(assets), "estáticos")
