#!/usr/bin/env python3
"""Regenera finales.html (visor de direcciones finales + panel de Evaluación) a partir de esta carpeta.
Uso: python3 build.py  ->  escribe ./finales.html. Si existe evaluacion.json lo embebe en el panel."""
import json, os
here = os.path.dirname(os.path.abspath(__file__))
meta = {1: ("La Gaceta", "Ruta · diario de barrio: portada, titulares, libro de registro y clasificados."),
        2: ("Encastre", "Suave · landing de bloques y dashboard de bolsillo en un mismo sistema."),
        3: ("Tratado de la máquina", "Trazo · máquina grabada en tinta, láminas numeradas y plano de planta con válvulas."),
        4: ("Huerto", "Calma · la donación es una planta que crece con sus estados reales."),
        5: ("Lima translúcida", "Cristal · vidrio esmerilado gris-lima con tipografía enorme y liviana."),
        6: ("Atlas de Rumbos", "Cartografía · cartas náuticas, rosa de los vientos, sellos de lacre y constancias.")}
data = []
for n in range(1, 7):
    d = os.path.join(here, "final", "d%d" % n)
    rd = lambda f: open(os.path.join(d, f), encoding="utf-8").read()
    data.append({"n": n, "nombre": meta[n][0], "resumen": meta[n][1], "landing": rd("landing.html"), "dashboard": rd("dashboard.html"),
                 "sistema": json.loads(rd("sistema.json")), "notas": rd("notas.md")})
ev_path = os.path.join(here, "evaluacion.json")
ev = json.load(open(ev_path, encoding="utf-8")) if os.path.exists(ev_path) else None
def dump(o): return json.dumps(o, ensure_ascii=False).replace("</", "<\\/")
html = open(os.path.join(here, "viewer_shell.html"), encoding="utf-8").read().replace("__DATA__", dump(data)).replace("__EVAL__", dump(ev))
open(os.path.join(here, "finales.html"), "w", encoding="utf-8").write(html)
print("finales.html", len(html), "bytes; evaluacion:", "sí" if ev else "pendiente")
