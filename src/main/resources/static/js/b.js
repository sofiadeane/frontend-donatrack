// Variante B · landing y panel. Todo es mejora progresiva: sin JS la página se ve completa.
(() => {
  "use strict";
  const reducir = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  const todos = (sel, raiz = document) => Array.from(raiz.querySelectorAll(sel));

  // ----- Aparición al hacer scroll -----
  const apariciones = todos(".rev");
  if (apariciones.length) {
    if ("IntersectionObserver" in window && !reducir) {
      const obs = new IntersectionObserver(
        (entradas) =>
          entradas.forEach((e) => {
            if (e.isIntersecting) {
              e.target.classList.add("is-in");
              obs.unobserve(e.target);
            }
          }),
        { rootMargin: "0px 0px -8% 0px", threshold: 0.08 }
      );
      apariciones.forEach((el) => obs.observe(el));
    } else {
      apariciones.forEach((el) => el.classList.add("is-in"));
    }
  }

  // ----- Contador de cifras al entrar en pantalla -----
  const formato = new Intl.NumberFormat("es-AR");
  todos("[data-contar]").forEach((el) => {
    const meta = Number(el.dataset.valor);
    if (!Number.isFinite(meta) || reducir || !("IntersectionObserver" in window)) return;
    const obs = new IntersectionObserver((entradas) => {
      if (!entradas[0].isIntersecting) return;
      obs.disconnect();
      const inicio = performance.now();
      const dur = 1100;
      const paso = (t) => {
        const k = Math.min(1, (t - inicio) / dur);
        const suave = 1 - Math.pow(1 - k, 3);
        el.textContent = formato.format(Math.round(meta * suave));
        if (k < 1) requestAnimationFrame(paso);
      };
      requestAnimationFrame(paso);
    });
    obs.observe(el);
  });

  // ----- Hero: la ficha de seguimiento sigue a la estación activa -----
  const hero = document.querySelector("[data-ruta-viva]");
  if (hero) {
    const estaciones = todos("[data-estacion]", hero);
    const etiqueta = {
      EN_DEPOSITO: "En depósito",
      ASIGNACION_REALIZADA: "Asignada",
      EN_TRASLADO: "En traslado",
      ENTREGADA: "Entregada",
    };
    const estado = hero.querySelector("[data-ficha-estado]");
    const destino = hero.querySelector("[data-ficha-destino]");
    const segmentos = todos(".ficha__barra i", hero);
    let actual = -1;
    let timer = null;

    const activar = (i) => {
      actual = i;
      estaciones.forEach((e, n) => e.classList.toggle("is-act", n === i));
      const e = estaciones[i];
      estado.dataset.estado = e.dataset.estado;
      estado.textContent = etiqueta[e.dataset.estado];
      destino.textContent = e.dataset.destino;
      segmentos.forEach((s, n) => s.classList.toggle("is-on", n <= i));
    };
    const avanzar = () => activar((actual + 1) % estaciones.length);
    const arrancar = () => {
      if (reducir || timer) return;
      timer = setInterval(avanzar, 2600);
    };
    const pausar = () => {
      clearInterval(timer);
      timer = null;
    };

    estaciones.forEach((e, i) => {
      e.addEventListener("mouseenter", () => {
        pausar();
        activar(i);
      });
      e.addEventListener("click", () => {
        pausar();
        activar(i);
      });
    });
    hero.addEventListener("mouseleave", arrancar);
    document.addEventListener("visibilitychange", () => (document.hidden ? pausar() : arrancar()));
    activar(0);
    arrancar();
  }

  // ----- Recorrido: nodos del diagrama y filas del libro se iluminan juntos -----
  const recorrido = document.querySelector("[data-recorrido]");
  if (recorrido) {
    const nodos = todos(".mapa__n", recorrido);
    const filas = todos("tbody tr", recorrido);
    const play = document.querySelector("[data-reproducir]");
    let reproduccion = null;

    const elegir = (n) => {
      recorrido.classList.toggle("has-sel", n != null);
      nodos.forEach((el) => el.classList.toggle("is-sel", Number(el.dataset.paso) === n));
      filas.forEach((el) => el.classList.toggle("is-sel", Number(el.dataset.paso) === n));
    };
    const detener = () => {
      clearInterval(reproduccion);
      reproduccion = null;
      if (play) play.textContent = "▶ Reproducir recorrido";
    };
    nodos.forEach((el) => {
      const n = Number(el.dataset.paso);
      el.addEventListener("click", () => {
        detener();
        elegir(n);
      });
      el.addEventListener("mouseenter", () => !reproduccion && elegir(n));
    });
    filas.forEach((el) => {
      const n = Number(el.dataset.paso);
      el.addEventListener("mouseenter", () => !reproduccion && elegir(n));
      el.addEventListener("click", () => {
        detener();
        elegir(n);
      });
    });
    recorrido.addEventListener("mouseleave", () => !reproduccion && elegir(null));

    if (play) {
      play.hidden = false;
      play.addEventListener("click", () => {
        if (reproduccion) return detener();
        let n = 1;
        elegir(n);
        play.textContent = "■ Detener";
        reproduccion = setInterval(() => {
          n += 1;
          if (n > nodos.length) return detener();
          elegir(n);
        }, 1100);
      });
    }
  }

  // ----- Panel: gráfico con dos medidas y lectura al pasar -----
  const grafico = document.querySelector("[data-chart]");
  if (grafico) {
    const botones = todos("[data-metrica]", grafico);
    const salida = grafico.querySelector("[data-lectura-out]");
    const base = salida ? salida.textContent : "";
    let metrica = "n";
    const leer = (col) => {
      if (salida) salida.textContent = col ? `${col.dataset.lectura} · ${col.dataset[metrica]}` : base;
    };
    botones.forEach((b) =>
      b.addEventListener("click", () => {
        metrica = b.dataset.metrica;
        grafico.dataset.m = metrica;
        botones.forEach((o) => o.setAttribute("aria-pressed", String(o === b)));
        leer(null);
      })
    );
    todos(".col", grafico).forEach((col) => {
      col.addEventListener("mouseenter", () => leer(col));
      col.addEventListener("focus", () => leer(col));
      col.addEventListener("mouseleave", () => leer(null));
      col.addEventListener("blur", () => leer(null));
    });
  }

  // ----- Panel: filtro de la tabla desde la barra de flujo y desde los chips -----
  const tabla = document.getElementById("tabla-atencion");
  if (tabla) {
    const filas = todos("tbody tr", tabla);
    const vacio = document.querySelector(".aten__vacio");
    const controles = todos("[data-filtro]");
    let filtro = "TODAS";
    const aplicar = () => {
      let visibles = 0;
      filas.forEach((f) => {
        const ok =
          filtro === "TODAS" ||
          (filtro === "URGENTE" ? f.dataset.urgente === "true" : f.dataset.estado === filtro);
        f.hidden = !ok;
        if (ok) visibles += 1;
      });
      if (vacio) vacio.hidden = visibles > 0;
      controles.forEach((c) =>
        c.setAttribute("aria-pressed", String(c.dataset.filtro === filtro))
      );
    };
    controles.forEach((c) =>
      c.addEventListener("click", () => {
        filtro = filtro === c.dataset.filtro ? "TODAS" : c.dataset.filtro;
        aplicar();
        if (filtro !== "TODAS" && window.matchMedia("(max-width: 860px)").matches) {
          tabla.scrollIntoView({ behavior: reducir ? "auto" : "smooth", block: "nearest" });
        }
      })
    );
    aplicar();
  }

  // ----- Carrusel de funcionalidades: flechas solo si hay JS -----
  const pista = document.querySelector("[data-carrusel]");
  if (pista) {
    const ctl = document.querySelector("[data-carrusel-ctl]");
    const paso = () => (pista.querySelector("li")?.getBoundingClientRect().width || 300) + 14;
    const mover = (dir) => pista.scrollBy({ left: dir * paso(), behavior: reducir ? "auto" : "smooth" });
    if (ctl) {
      ctl.hidden = false;
      ctl.querySelector("[data-carrusel-prev]").addEventListener("click", () => mover(-1));
      ctl.querySelector("[data-carrusel-next]").addEventListener("click", () => mover(1));
    }
    pista.addEventListener("keydown", (e) => {
      if (e.key === "ArrowRight") mover(1);
      if (e.key === "ArrowLeft") mover(-1);
    });
  }
})();
