"""Genera textura-ciudad.svg: un damero de manzanas desparejas, una diagonal y un recorrido.

Mismo lienzo y mismo criterio que topografico.svg (1281x1295, trazos blancos con opacidad baja),
para reemplazar la textura actual sin tocar posiciones ni tamaños.
"""
import math
import random
import sys

W, H = 1281, 1295
GIRO = math.radians(-8)  # el damero no va a escuadra con la pantalla
random.seed(11)


def rot(x, y):
    cx, cy = W / 2, H / 2
    dx, dy = x - cx, y - cy
    return (cx + dx * math.cos(GIRO) - dy * math.sin(GIRO),
            cy + dx * math.sin(GIRO) + dy * math.cos(GIRO))


def ejes(desde, hasta):
    """Calles con cuadras desparejas, como una ciudad real (no una hoja cuadriculada)."""
    v, out = desde, []
    while v < hasta:
        out.append(v)
        v += random.choice([70, 84, 84, 96, 110])
    return out


def seg(p, q, opacidad, ancho):
    (x1, y1), (x2, y2) = rot(*p), rot(*q)
    return (f'<line x1="{x1:.1f}" y1="{y1:.1f}" x2="{x2:.1f}" y2="{y2:.1f}" '
            f'stroke-opacity="{opacidad}" stroke-width="{ancho}"/>')


M = 360
xs, ys = ejes(-M, W + M), ejes(-M, H + M)
out = []
for i, x in enumerate(xs):
    av = i % 5 == 2
    out.append(seg((x, -M), (x, H + M), 0.07 if av else 0.035, 1.6 if av else 1))
for j, y in enumerate(ys):
    av = j % 5 == 3
    out.append(seg((-M, y), (W + M, y), 0.07 if av else 0.035, 1.6 if av else 1))

# Una diagonal que rompe el damero.
out.append(seg((-M, H * 0.9), (W + M, H * 0.05), 0.05, 1.4))

# Plazas: algunas manzanas apenas teñidas.
for _ in range(10):
    i, j = random.randrange(3, len(xs) - 4), random.randrange(3, len(ys) - 4)
    x0, x1, y0, y1 = xs[i] + 5, xs[i + 1] - 5, ys[j] + 5, ys[j + 1] - 5
    pts = " ".join(f"{a:.1f},{b:.1f}" for a, b in (rot(x0, y0), rot(x1, y0), rot(x1, y1), rot(x0, y1)))
    out.append(f'<polygon points="{pts}" fill="white" fill-opacity="0.03" stroke="none"/>')


def cerca(v, lista):
    return min(range(len(lista)), key=lambda k: abs(lista[k] - v))


# Recorrido: del depósito (abajo a la izquierda) a la última parada (arriba a la derecha), siempre
# por las calles; cruza la zona que queda visible en las tarjetas oscuras (centro y derecha).
i, j = cerca(140, xs), cerca(1120, ys)
fin_i, fin_j = cerca(1180, xs), cerca(150, ys)
esquinas = [(xs[i], ys[j])]
while i < fin_i or j > fin_j:
    # Tramos largos, como un camión que va por avenidas y dobla pocas veces.
    if i < fin_i:
        i = min(fin_i, i + random.choice([3, 4, 5]))
        esquinas.append((xs[i], ys[j]))
    if j > fin_j:
        j = max(fin_j, j - random.choice([2, 3, 4]))
        esquinas.append((xs[i], ys[j]))
pts = [rot(x, y) for x, y in esquinas]
d = "M" + " L".join(f"{x:.1f} {y:.1f}" for x, y in pts)
out.append(f'<path d="{d}" stroke-opacity="0.2" stroke-width="2.4" stroke-dasharray="9 7" '
           'stroke-linecap="round" stroke-linejoin="round"/>')
paradas = [0, len(pts) // 3, 2 * len(pts) // 3, len(pts) - 1]
for k in paradas:
    x, y = pts[k]
    r = 8 if k == 0 else 5.5
    out.append(f'<circle cx="{x:.1f}" cy="{y:.1f}" r="{r}" fill="white" fill-opacity="0.16" '
               'stroke-opacity="0.32" stroke-width="1.5"/>')

svg = (f'<svg width="{W}" height="{H}" viewBox="0 0 {W} {H}" fill="none" stroke="white" '
       'preserveAspectRatio="none" xmlns="http://www.w3.org/2000/svg">\n'
       '<!-- Textura de ciudad: damero, una diagonal y un recorrido de depósito a tres paradas. '
       'Ilustrativa, sin ubicaciones reales. Generada con scripts/textura_ciudad.py -->\n'
       + "\n".join(out) + "\n</svg>\n")
open(sys.argv[1], "w").write(svg)
print(len(svg), "bytes,", len(pts), "esquinas")
