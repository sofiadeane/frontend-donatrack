import json, datetime
IDS = ["orig", "d1", "d2", "d3", "d4", "d5", "d6"]
NOMBRES = ["Propuesta original", "1 · La Gaceta", "2 · Encastre", "3 · Tratado de la máquina", "4 · Huerto", "5 · Lima translúcida", "6 · Atlas de Rumbos"]
CRIT = [("coherencia", "Coherencia con la identidad del proyecto", 25), ("publico", "Adecuación al público objetivo", 20),
        ("credibilidad", "Credibilidad institucional", 20), ("claridad", "Claridad y usabilidad", 20),
        ("escalabilidad", "Escalabilidad a largo plazo", 10), ("diferenciacion", "Diferenciación visual", 5)]

P = {
 "orig": {
  "coherencia": (4, "Teal y verde con tono institucional: refuerza confianza y orden. Dice poco de personas e impacto."),
  "publico": (4, "Sirve a donantes, entidades y administración sin exigir un código visual especial."),
  "credibilidad": (4, "Se parece a una plataforma seria (salud, finanzas). Se lee como organización consolidada."),
  "claridad": (4, "Jerarquía convencional y componentes conocidos. Es observable, no se probó con usuarios."),
  "escalabilidad": (5, "Tokens semánticos con una sola fuente de color; ya está integrada al código real."),
  "diferenciacion": (2, "Se confunde con muchas plataformas de gestión. No hay un rasgo propio memorable.")},
 "d1": {
  "coherencia": (3, "Idea fuerte: lo que se dona se anota y se publica. Pero el tono de diario de barrio le quita seriedad operativa."),
  "publico": (3, "Cómoda para quien lee y le gusta lo editorial. Puede ser ruidosa para quien administra todo el día."),
  "credibilidad": (3, "El libro de registro transmite transparencia. Bordes gruesos y colores fuertes juegan a favor de lo informal."),
  "claridad": (4, "Titulares numerados con una acción cada uno; el texto es grande y legible."),
  "escalabilidad": (3, "Estilo con mucho carácter: cuesta sostenerlo en pantallas densas (tablas, formularios largos)."),
  "diferenciacion": (5, "Es la más reconocible del grupo: nada la confunde con otra plataforma.")},
 "d2": {
  "coherencia": (4, "Un bloque por dato y una tarea por vez: refuerza organización y cercanía. Los pasteles aportan poca solemnidad."),
  "publico": (4, "Voseo, píldoras y bloques: invita a donantes y a entidades chicas. En el dashboard, el foco de a una tarea pide probarse con volumen alto."),
  "credibilidad": (3, "Amable y clara, pero se parece a una app de consumo; puede leerse como menos institucional."),
  "claridad": (4, "Estado con color, punto y texto. Cola de tareas simple de seguir."),
  "escalabilidad": (4, "Un solo CSS compartido y piezas repetibles; la grilla de escritorio ya prevé crecer."),
  "diferenciacion": (3, "Reconocible, pero el estilo de bloques pastel está muy visto.")},
 "d3": {
  "coherencia": (3, "Muestra cómo funciona el sistema (estaciones, válvulas): refuerza trazabilidad. Casi no habla de personas ni de impacto social."),
  "publico": (2, "Atrae a perfiles técnicos. Una persona donante o una entidad pequeña puede sentirlo ajeno."),
  "credibilidad": (3, "Rigor y precisión transmiten seriedad, pero con un aire de curiosidad antigua más que de ONG."),
  "claridad": (3, "El plano de planta es claro para quien lo entiende. Texto técnico chico en mono y metáfora a decodificar."),
  "escalabilidad": (2, "Láminas con doble marco y esquinas de registro: costoso de mantener y de extender a pantallas nuevas."),
  "diferenciacion": (5, "Identidad visual muy propia; difícil de confundir.")},
 "d4": {
  "coherencia": (4, "Calma, aire y nombres reales de estados: refuerza confianza y cuidado. Es la que mejor equilibra personas y proceso."),
  "publico": (4, "Cómoda para donantes y entidades. Para operar con mucho volumen, el aire resta densidad."),
  "credibilidad": (4, "Sobria y legible, sin ruido. Tipografía serif fina sugiere cuidado y seriedad."),
  "claridad": (4, "Una acción principal por vez y lo demás visible. Líneas finas y gris verdoso: revisar contraste."),
  "escalabilidad": (3, "El estilo depende del aire y de pocas cajas; con tablas muy densas puede perder jerarquía."),
  "diferenciacion": (4, "Distinta de lo habitual en gestión, sin caer en lo estridente.")},
 "d5": {
  "coherencia": (3, "Lo estructural (carriles de estado) es coherente. El vidrio y las auroras hablan de tecnología más que de donaciones."),
  "publico": (3, "Resulta cómoda para público acostumbrado a apps recientes. Puede sentirse impersonal para otros."),
  "credibilidad": (3, "Pulida y moderna, pero ligada a una tendencia; transmite innovación más que consolidación."),
  "claridad": (3, "Cifras enormes ayudan. Texto sobre vidrio depende del contraste; existe modo sin transparencia."),
  "escalabilidad": (2, "Transparencias y fondos degradados: caros de renderizar y de mantener accesibles. Envejece con la moda."),
  "diferenciacion": (4, "Llamativa y reconocible, aunque el estilo de vidrio lo usan muchos productos.")},
 "d6": {
  "coherencia": (3, "El recorrido como derrota de puerto a puerto calza con mapas y entregas. Pero Naufragio o Rumbo perdido para donaciones vencidas chocan con un contexto solidario."),
  "publico": (2, "Estilo de nicho (pergamino, lacre, náutica). Muchas personas lo verían teatral para una gestión cotidiana."),
  "credibilidad": (3, "Tradición y sello generan peso. También pueden leerse como disfraz y distraer de lo real."),
  "claridad": (3, "Siempre muestra el nombre real del estado. La metáfora suma una capa de lectura."),
  "escalabilidad": (2, "Collage, bordes rasgados y marrón oscuro: mucha decoración para sostener en cada pantalla nueva."),
  "diferenciacion": (5, "Muy distintiva; cuenta una historia propia.")},
}

M = {
 "Personalidad": ["Sobria, institucional, tecnológica", "Editorial, directa, de diario", "Amable, lúdica, ordenada", "Técnica, de ingenio, de grabado", "Serena, cuidada, orgánica", "Fresca, liviana, tecnológica", "Teatral, exploradora, de archivo"],
 "Keywords": [["institucional", "limpia", "teal", "convencional", "confiable"], ["diario", "registro", "contraste", "papel", "tinta"], ["bloques", "pasteles", "amable", "tarea", "bolsillo"], ["grabado", "máquina", "plano", "tinta", "precisión"], ["aire", "planta", "calma", "hairline", "serif"], ["vidrio", "lima", "aurora", "liviano", "tipografía"], ["mapa", "brújula", "pergamino", "lacre", "derrota"]],
 "Percepción emocional": ["Orden y tranquilidad", "Franqueza y energía", "Cercanía y alivio", "Curiosidad y rigor", "Calma y cuidado", "Novedad y ligereza", "Aventura y solemnidad"],
 "Edad percibida": ["Adulta, transversal", "Adulta joven", "Joven a adulta", "Adulta madura", "Adulta", "Joven a adulta", "Adulta madura"],
 "Público objetivo": ["Donantes, entidades y personal", "Lectores comprometidos, sensibles a lo editorial", "Donantes y entidades chicas", "Perfiles técnicos y curiosos", "Donantes y entidades", "Usuarios de apps actuales", "Público de nicho: narrativa e historia"],
 "Orientación": ["Híbrido (ONG + institucional)", "ONG / comunitaria", "B2C + ONG", "B2B / técnica", "ONG / B2C", "B2C / tecnológica", "Cultural / ONG de nicho"],
 "Formalidad": ["Media-alta", "Media-baja", "Media-baja", "Alta", "Media", "Media", "Alta, ornamental"],
 "Mensaje institucional": ["Organización seria y ordenada", "Transparencia como acto público", "Gestión simple y cercana", "Sistema preciso y verificable", "Cuidado paciente de cada donación", "Plataforma actual y ágil", "Cada donación tiene su recorrido"],
 "Comunicación cromática": ["Teal y verde: confianza y salud; error en rojo terroso", "Mostaza, celeste, rosa y tomate: energía; tomate choca con alertas", "Pasteles: calidez; estado por color + texto", "Tinta y un solo naranja: atención reservada", "Oliva y salvia: calma; coral solo para urgencias", "Gris-lima y pasteles: ligereza; poco contraste de marca", "Pergamino y marrón: solemnidad; poco semáforo"],
 "Profesionalismo": ["Alto", "Medio", "Medio", "Medio-alto", "Alto", "Medio", "Medio"],
 "Confianza": ["Alta", "Media", "Media", "Media", "Alta", "Media", "Media"],
 "Innovación": ["Baja", "Alta", "Media", "Media", "Media", "Alta", "Media"],
 "Accesibilidad": ["Buena (contraste verificado)", "Buena; revisar rosa y celeste", "Buena; estado con texto", "Media; texto mono chico", "Media; revisar contraste fino", "Media; depende del vidrio", "Media; serif chica y fondo oscuro"],
 "Diferenciación": ["Baja", "Muy alta", "Media", "Muy alta", "Alta", "Alta", "Muy alta"],
 "Escalabilidad": ["Alta (ya en código)", "Media", "Alta", "Baja", "Media", "Baja", "Baja"],
 "Coherencia con Donatrack": ["Alta", "Media", "Alta", "Media-baja", "Alta", "Media", "Media-baja"],
 "Ventajas": ["Probada en código; sobria y legible", "Memorable; refuerza transparencia", "Cercana; una tarea a la vez", "Se ve cómo funciona el sistema", "Calma; estados con su nombre real", "Cifras grandes; estados como carriles", "Recorrido claro; mapa natural"],
 "Desventajas": ["Poco memorable", "Choca con alertas; ruido en pantallas densas", "Poco solemne; foco único limita volumen", "Poco humana; mantenimiento caro", "Menos densa; contraste fino", "Ligada a una moda; exige cuidar contraste", "Metáforas incómodas; decoración pesada"],
 "Riesgos": ["Que parezca genérica", "Que se lea como informal ante instituciones", "Que se perciba como app de consumo", "Que aleje a donantes y entidades", "Que no rinda con mucho volumen", "Que envejezca rápido", "Que la narrativa tape la función"],
}

def A(personalidad, publico, coherencia, institucional, paleta, v, d, r, o, c):
    return dict(personalidad=personalidad, publico=publico, coherencia=coherencia, institucional=institucional, paleta=paleta, ventajas=v, desventajas=d, riesgos=r, oportunidades=o, conservar=c)

AN = {
 "orig": A(
  "Sobria y tecnológica. Se parece a plataformas de gestión: ordenada, previsible, calmada. Observable: hero con CTA doble (donar o ser entidad) y pasos numerados.",
  "Adultos y transversal. Cómoda para quien dona, para entidades y para quien administra. Interpretación: no intimida, pero tampoco emociona.",
  "Alta. Refuerza confianza, orden y trazabilidad. Deja en segundo plano a las personas y el impacto social. No contradice lo que ofrece el producto.",
  "Se lee como organización consolidada y confiable. Poca señal de cercanía o de historia propia.",
  "Teal oscuro y verde. Interpretación (no universal): en muchos contextos se asocia a salud y cuidado. Contraste verificado en AA. Error en rojo terroso que no choca con la paleta. Flexible para estados.",
  ["Ya está en el código, con tokens semánticos y una sola fuente de color.", "Contraste revisado, con estados claros.", "Componentes convencionales: bajo costo de aprender."],
  ["Se parece a muchas plataformas: poca identidad propia.", "Verde también es éxito: puede confundirse con estados positivos."],
  ["Parecer genérica ante una organización que quiere destacar su cercanía."],
  ["Sumar rasgos propios (tipografía, ilustración) sin romper la base."],
  ["Tokens y fuente única de color.", "Contraste AA.", "Estructura de la landing."]),
 "d1": A(
  "Editorial y directa: DonaTrack como diario de barrio. Observable: titulares enormes, libro de registro, bordes gruesos y sombra dura.",
  "Adultos jóvenes y lectores comprometidos. Cómoda para quien ama lo editorial; puede cansar a quien usa el panel todo el día.",
  "Media. Refuerza transparencia (todo se anota, se verifica y se publica) y conciencia de lo público. Debilita la formalidad y la sensación de sistema serio.",
  "Franqueza y transparencia. A la vez, una iniciativa con voz propia, algo informal para una institución.",
  "Mostaza, celeste, rosa y tomate sobre papel cálido. Interpretación: energía y calidez. El tomate se usa para En traslado y para titulares: choca con la idea de alerta.",
  ["Identidad muy reconocible.", "Titulares con una acción por vez: orden claro.", "Libro de registro: encaja con trazabilidad y auditoría."],
  ["Colores fuertes compiten con las alertas reales.", "Estilo costoso de sostener en formularios densos.", "Tono informal frente a instituciones."],
  ["Que se lea como una propuesta experimental y no como un sistema consolidado."],
  ["Usar el lenguaje de libro y titulares como voz de la landing pública."],
  ["Libro de registro y cinta de circulación.", "Paleta de comandos."]),
 "d2": A(
  "Amable y ordenada. Observable: bloques pastel por dato, cápsula de cinco paradas, cola de tareas con foco único y dock inferior.",
  "Jóvenes y adultos. Cercana para donantes y entidades chicas. Para el personal con mucho volumen, el foco único hay que probarlo.",
  "Alta. Refuerza cercanía, claridad y organización. Debilita la solemnidad institucional.",
  "Una organización cercana y práctica, que simplifica. Puede leerse como iniciativa joven más que consolidada.",
  "Crema con pasteles. Interpretación: calidez y cercanía. Estado = color + punto + texto, nunca solo color. Los pasteles necesitan tonos oscuros de texto (ya definidos).",
  ["Una tarea a la vez baja la carga mental.", "Estado con color, punto y texto: accesible.", "CSS compartido por landing y dashboard: coherencia."],
  ["Se parece a una app de consumo.", "El foco único puede frenar cuando hay muchas tareas."],
  ["Perder credibilidad frente a instituciones y empresas donantes."],
  ["Sirve para un portal de donantes y entidades, más que para la administración pesada."],
  ["Cola de tareas con foco.", "Estado con texto y punto.", "Voseo."]),
 "d3": A(
  "Técnica y de ingenio: máquina grabada en tinta. Observable: láminas con doble marco, plano de planta con estaciones, válvulas naranjas, tipografía condensada y mono.",
  "Adultos maduros y perfiles técnicos. Quien dona o la entidad pequeña puede sentirlo ajeno o intimidante.",
  "Media-baja. Refuerza trazabilidad y rigor (se ve cómo está hecho). Deja de lado a las personas, la cercanía y el impacto social.",
  "Precisión y sistema verificable. A la vez, distancia y un aire de curiosidad antigua.",
  "Papel crema, tinta y un solo naranja. Interpretación: sobriedad, con el naranja como atención. Un único acento es una regla clara, pero deja poco margen para estados diversos.",
  ["Un solo color de atención: regla simple.", "Los estados son estaciones: modelo mental claro.", "Muy reconocible."],
  ["Poco humana: casi no hay personas ni impacto.", "Texto mono de 11–13 px: lectura difícil.", "Marcos y láminas: costo alto de mantener."],
  ["Que quienes donan o reciben se sientan lejos de la plataforma."],
  ["El plano de planta puede servir como vista técnica del depósito."],
  ["Plano de estaciones.", "Un solo acento de atención."]),
 "d4": A(
  "Serena y orgánica: la donación es una planta que crece. Observable: serif fina, líneas finas en lugar de cajas, mucho aire, metáfora de poda y helada solo en cursiva.",
  "Adultos. Cómoda para donantes y entidades. En administración muy cargada, el aire resta densidad.",
  "Alta. Refuerza confianza, cuidado y claridad. Equilibra proceso y personas. El nombre real de cada estado siempre se muestra.",
  "Una organización cuidadosa y sobria. Cercana sin ser informal.",
  "Oliva, salvia y miel; coral solo para urgencias. Interpretación: calma y cuidado (asociación cultural, no universal). El coral queda reservado para alerta y se destaca. Revisar contraste de grises verdosos y líneas finas.",
  ["Coral reservado para lo urgente: semántica clara.", "Nombres reales de estado siempre visibles.", "Una cosa por vez, con lo demás visible."],
  ["Líneas finas y grises verdosos: riesgo de contraste.", "Menos densa para tablas grandes.", "Metáfora de planta puede quedar corta en entregas fallidas."],
  ["Que en operación con mucho volumen cueste escanear."],
  ["Muy buena base para la landing y el portal de donantes."],
  ["Paleta oliva/salvia con coral de alerta.", "Estado con nombre real.", "Para después visible."]),
 "d5": A(
  "Fresca y liviana: vidrio esmerilado sobre auroras. Observable: tipografía enorme y delgada, lima como acento, carriles de estado y modo sin transparencia.",
  "Jóvenes y adultos habituados a apps actuales. Puede sentirse impersonal o inestable para otros públicos.",
  "Media. Los carriles de estado son coherentes con trazabilidad. El vidrio comunica tecnología más que donaciones.",
  "Plataforma actual e innovadora. Menos señal de institución consolidada.",
  "Gris-lima con pasteles. Interpretación: ligereza y novedad. La lima marca lo activo, no la jerarquía de significado. Contraste del texto depende de la superficie.",
  ["Cifras grandes legibles.", "Modo sin transparencia disponible.", "Carriles de estado como estructura."],
  ["Transparencias y degradados: costo de mantener accesible.", "Ligada a una tendencia."],
  ["Envejecer rápido y requerir rediseño pronto."],
  ["Los carriles de estado sirven en cualquier estética."],
  ["Carriles de estado.", "Modo sin transparencia."]),
 "d6": A(
  "Teatral y exploradora: cartografía antigua. Observable: pergamino y marrón, collage con bordes rasgados, rosa de los vientos como filtro, lacres y nombres náuticos.",
  "Adultos maduros y público de nicho. Para el uso cotidiano de entidades y donantes resulta ornamental.",
  "Media-baja. El recorrido de puerto a puerto refuerza trazabilidad y mapa. Naufragio y Rumbo perdido para donaciones vencidas o fallidas chocan con un contexto solidario.",
  "Solemnidad y tradición. A la vez, algo disfrazado y lejano de una iniciativa universitaria solidaria.",
  "Pergamino y marrón tabaco. Interpretación: antigüedad y peso. Hay pocos colores semánticos; la alerta depende del texto y los sellos.",
  ["El recorrido de puerto a puerto es una metáfora que calza con el mapa de entregas.", "Siempre muestra el nombre real del estado."],
  ["Naufragio y Rumbo perdido: metáforas inapropiadas para donaciones.", "Collage y bordes rasgados: costoso de mantener.", "Texto serif chico sobre fondo oscuro."],
  ["Que la decoración tape la función y aleje a quienes necesitan simplicidad."],
  ["El mapa de rutas puede inspirar la vista de camiones."],
  ["Recorrido como ruta.", "Constancia de recepción."]),
}

CONC = [
 ("¿Qué variante comunica mejor el propósito y los valores?", "La original y Huerto. Ambas dicen orden, confianza y cuidado. Gaceta transmite transparencia pero con tono informal."),
 ("¿Cuál proyecta mayor confianza y credibilidad institucional?", "La original y Huerto. Sobriedad, legibilidad y pocas decoraciones. Tratado y Atlas inspiran respeto, pero por tradición, no por claridad."),
 ("¿Cuál es más apropiada para los usuarios reales?", "Encastre y Huerto: simples y cercanas para donantes y entidades. En el panel, conviene una versión más densa y consistente (original o Huerto)."),
 ("¿Cuál tiene más potencial de identidad reconocible?", "La Gaceta, Tratado y Atlas: son muy distintivas. Pero la identidad también debe resistir el tiempo y el uso cotidiano; ahí pesa menos."),
 ("¿Cuál tiene el mejor equilibrio entre personalidad, profesionalismo y usabilidad?", "Huerto: personalidad propia, tono sobrio y estados claros. La original es más segura pero menos propia."),
 ("¿Qué elementos podrían combinarse?", "De Huerto, la paleta con coral solo para alertas y los estados con nombre real. De Encastre, la cola de tareas. De Gaceta, el libro de registro. De Atlas, el recorrido como ruta. De la original, la base de tokens."),
 ("¿Qué dirección recomendás y por qué?", "Híbrida: Huerto como identidad pública y base de color. Dashboard sobre la estructura de la original, con la cola de tareas de Encastre. Prioriza confianza, claridad y contexto institucional sobre novedad."),
]

INC = [
 "Se usó el texto del PDF extraído con PDFKit (la herramienta de lectura de PDF no estaba disponible). No se vieron las figuras ni diagramas del enunciado.",
 "No hay pruebas con usuarias reales (donantes, entidades, administración). Todo juicio sobre público es interpretativo.",
 "Las asociaciones de color y de estilo (calma, confianza, tradición) dependen de cada cultura y contexto; no son verdades universales.",
 "No se midió contraste ni accesibilidad de cada propuesta; solo se tomó lo declarado en las notas y lo visto en capturas.",
 "No se revisaron todas las pantallas de cada propuesta con detalle (se miró una captura de dashboard por propuesta y notas); la landing de cada una se evaluó por sus notas y sistema.json.",
 "El enunciado no define identidad visual ni público en detalle: se infirió del marco institucional (UTN Solidaria) y de los requisitos de UI.",
 "Pesos sugeridos y puntajes no son métricas objetivas.",
]

out = {
 "generado": datetime.datetime.now().strftime("%Y-%m-%d %H:%M"),
 "fuentes": "Enunciado DDS-TP-Anual-2026.pdf (prólogo, decálogo, marco institucional, contexto, 'Nuestro sistema' y requisitos mínimos de UI/UX); CRITERIOS.md y ESQUEMA.md; plantilla inicio.html y tokens.css de la propuesta original; en final/d1…d6: notas.md, sistema.json y una captura de dashboard por propuesta.",
 "enunciado": {
  "resumen": "DonaTrack nace como caso de estudio de UTN Solidaria, una iniciativa estudiantil que asiste a personas en situación de vulnerabilidad. El problema es el desorden y la poca claridad sobre el destino de las donaciones. El sistema busca organizarlas, registrarlas y seguirlas desde el depósito hasta la entidad beneficiaria. Su objetivo declarado es mejorar la distribución y fortalecer la confianza de donantes y entidades. Los valores citados son responsabilidad social, empatía y trabajo colaborativo.",
  "criteriosClave": ["Confianza y transparencia hacia donantes y entidades", "Trazabilidad de cada donación, de punta a punta", "Landing pública con propósito, ejemplos del mes, mapa y fotos de entregas", "Tres actores: donantes, entidades beneficiarias y administración", "Impacto social y tono responsable", "Visión sistémica y profesional"]},
 "columnas": [{"id": i, "nombre": n} for i, n in zip(IDS, NOMBRES)],
 "resumenEjecutivo": [
  "Se evaluó qué comunica cada propuesta y qué tan bien calza con DonaTrack, no cuál es más linda.",
  "El enunciado pide confianza, trazabilidad y cercanía con personas en situación vulnerable. Ese es el filtro.",
  "La original es la más segura y escalable, pero también la menos distintiva.",
  "Huerto es la que mejor equilibra sobriedad, cercanía y claridad de estados.",
  "Gaceta, Tratado y Atlas se destacan por identidad, pero son costosas de sostener o poco cercanas para donantes y entidades.",
  "Recomendación: híbrida. Huerto para la landing, estructura de la original para el panel, y cola de tareas de Encastre.",
  "Los puntajes son juicios interpretativos, sin pruebas con usuarias reales."],
 "recomendacion": {
  "direccion": "Híbrida: Huerto como identidad pública y paleta de alertas, con el panel construido sobre la base y los tokens de la original.",
  "landing": "Huerto: aire, tipografía serif, estados con su nombre real y coral solo para lo urgente.",
  "dashboard": "Estructura y tokens de la original, con la cola de tareas de Encastre y el coral de alerta de Huerto.",
  "combinar": ["Huerto: paleta oliva/salvia y estados con nombre real", "Encastre: cola de tareas con foco", "Gaceta: libro de registro para auditar estados", "Atlas: recorrido como ruta para la vista de camiones", "Original: tokens y fuente única de color"],
  "porQue": "El enunciado habla de confianza, trazabilidad y de personas en situación de vulnerabilidad. Eso pide sobriedad, cercanía y estados claros antes que estética distintiva. Huerto cubre la parte pública y la original ya está integrada y probada para la operación diaria. Las otras ideas aportan piezas sueltas."},
 "puntajes": {
  "criterios": [{"id": i, "nombre": n, "peso": p} for i, n, p in CRIT],
  "valores": {k: {c[0]: {"p": P[k][c[0]][0], "j": P[k][c[0]][1]} for c in CRIT} for k in IDS}},
 "matriz": [{"dimension": dim, "valores": {i: v for i, v in zip(IDS, vals)}} for dim, vals in M.items()],
 "analisis": AN,
 "conclusiones": [{"pregunta": q, "respuesta": a} for q, a in CONC],
 "incertidumbres": INC,
}
assert len(out["matriz"]) == 19
json.dump(out, open("evaluacion.json", "w"), ensure_ascii=False, indent=1)
w = {k: sum(P[k][c][0] * p for c, _, p in CRIT) / 100 for k in IDS}
print(sorted(w.items(), key=lambda x: -x[1]))
