# Notas · d3 "Tratado de la máquina"

**Bug del dashboard previo (causa raíz):** la regla `.nd button{width:100%;height:100%;padding;background;position:relative}` y `.nd button:before{inset:3px;border}` se aplicaba también al botón de la válvula (hijo de `.nd`), porque su especificidad (0,1,1) gana a `.valve` (0,1,0). La válvula heredaba 100 % de ancho/alto, el marco interno y el fondo, y se veía como una elipse enorme tapando alertas. Arreglo: la estación es `.sel` y la válvula `.valve`, sin selectores compartidos; además `min-width:0` en grillas para que nada se desborde.

**Verificación:** capturas a 1440 y 390 px en `shots/`. Las de 390 están hechas con un iframe de 390 px reales, porque Chrome headless no baja de unos 500 px de ventana.

**Pendiente:** los textos del esquema de la hero (landing) son chicos en móvil (es ilustrativo). Las fuentes de Google no se verificaron sin red (fallbacks definidos). Datos de la galería y de las solicitudes de entidades son ejemplos rotulados.
