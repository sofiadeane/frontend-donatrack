// Mejoras progresivas del layout. Todo funciona sin JS; esto solo agrega comodidad.
(() => {
  "use strict";

  // Barra lateral colapsable (escritorio). El estado se recuerda por navegador.
  const shell = document.querySelector("[data-shell]");
  const colapsar = document.querySelector("[data-colapsar-lateral]");
  if (shell && colapsar) {
    colapsar.hidden = false;
    const aplicar = (colapsado) => {
      shell.classList.toggle("is-colapsado", colapsado);
      colapsar.setAttribute("aria-expanded", String(!colapsado));
      colapsar.setAttribute("aria-label", colapsado ? "Expandir menú" : "Colapsar menú");
      try {
        localStorage.setItem("donatrack.lateral", colapsado ? "1" : "0");
      } catch (e) {
        /* almacenamiento no disponible: se ignora */
      }
    };
    let guardado = null;
    try {
      guardado = localStorage.getItem("donatrack.lateral");
    } catch (e) {
      guardado = null;
    }
    if (guardado === "1") aplicar(true);
    colapsar.addEventListener("click", () => aplicar(!shell.classList.contains("is-colapsado")));
  }

  // Búsqueda en celular: abre el panel en lugar de navegar a /buscar.
  const hoja = document.querySelector("[data-hoja-busqueda]");
  document.querySelectorAll("[data-abrir-busqueda]").forEach((boton) => {
    if (!hoja || typeof hoja.showModal !== "function") return;
    boton.addEventListener("click", (evento) => {
      evento.preventDefault();
      hoja.showModal();
    });
  });
})();

// Carrusel de la landing: botones anterior/siguiente sobre una pista con scroll-snap.
// Sin JS la pista se desplaza igual (táctil, rueda o teclado con foco).
(() => {
  "use strict";
  document.querySelectorAll("[data-carrusel]").forEach((carrusel) => {
    const pista = carrusel.querySelector("[data-carrusel-pista]");
    const anterior = carrusel.querySelector("[data-carrusel-anterior]");
    const siguiente = carrusel.querySelector("[data-carrusel-siguiente]");
    if (!pista || !anterior || !siguiente) return;
    const paso = () => {
      const tarjeta = pista.querySelector("li");
      return tarjeta ? tarjeta.getBoundingClientRect().width + 41 : pista.clientWidth * 0.8;
    };
    const actualizar = () => {
      const max = pista.scrollWidth - pista.clientWidth - 2;
      anterior.hidden = pista.scrollLeft <= 2;
      siguiente.hidden = pista.scrollLeft >= max;
    };
    anterior.addEventListener("click", () => pista.scrollBy({ left: -paso(), behavior: "smooth" }));
    siguiente.addEventListener("click", () => pista.scrollBy({ left: paso(), behavior: "smooth" }));
    pista.addEventListener("scroll", actualizar, { passive: true });
    window.addEventListener("resize", actualizar);
    actualizar();
  });
})();

// Menú de la barra pública en celular.
(() => {
  "use strict";
  const barra = document.querySelector("[data-barra-publica]");
  const boton = document.querySelector("[data-menu-publico]");
  if (!barra || !boton) return;
  barra.classList.add("is-plegable");
  boton.hidden = false;
  boton.addEventListener("click", () => {
    const abierta = barra.classList.toggle("is-abierta");
    boton.setAttribute("aria-expanded", String(abierta));
  });
})();

// Desplegables (semicírculo que se expande): toque en celular y teclado.
// En escritorio también se abren con hover y al recibir foco (CSS).
(() => {
  "use strict";
  document.querySelectorAll("[data-desplegable]").forEach((desplegable) => {
    const boton = desplegable.querySelector(".desplegable__boton");
    if (!boton) return;
    boton.addEventListener("click", () => {
      const abierto = desplegable.classList.toggle("is-abierto");
      boton.setAttribute("aria-expanded", String(abierto));
    });
  });
})();

// Títulos que llenan su espacio: el tamaño más grande (17–34 px) que entra en el alto disponible.
(() => {
  "use strict";
  const titulos = document.querySelectorAll("[data-ajustar-titulo]");
  if (!titulos.length) return;
  const ajustar = () => {
    titulos.forEach((h) => {
      let min = 17;
      let max = 34;
      h.style.fontSize = "";
      const alto = h.clientHeight;
      const ancho = h.clientWidth;
      while (max - min > 0.5) {
        const medio = (min + max) / 2;
        h.style.fontSize = medio + "px";
        if (h.scrollHeight <= alto && h.scrollWidth <= ancho) min = medio;
        else max = medio;
      }
      h.style.fontSize = Math.floor(min) + "px";
    });
  };
  (document.fonts ? document.fonts.ready : Promise.resolve()).then(ajustar);
  window.addEventListener("resize", ajustar);
})();

// Barra pública: vidrio oscuro solo mientras el hero cubre toda la barra, para que el logo se lea.
// Si una superficie clara (p. ej. la tarjeta "Cómo funciona", dentro del hero) toca la barra,
// vuelve al vidrio claro para que el menú siga legible.
(() => {
  "use strict";
  const barra = document.querySelector("[data-barra-publica]");
  const hero = document.querySelector("[data-hero]");
  if (!barra || !hero) return;
  const claras = [...document.querySelectorAll("[data-fondo-claro]")];
  const toca = (el, arriba, abajo) => {
    const r = el.getBoundingClientRect();
    return r.top < abajo && r.bottom > arriba;
  };
  const actualizar = () => {
    const { top, bottom } = barra.getBoundingClientRect();
    const fondo = hero.getBoundingClientRect();
    const cubre = fondo.top <= top && fondo.bottom >= bottom;
    const oscuro = cubre && !claras.some((el) => toca(el, top, bottom));
    barra.classList.toggle("sobre-oscuro", oscuro);
  };
  window.addEventListener("scroll", actualizar, { passive: true });
  window.addEventListener("resize", actualizar);
  actualizar();
})();

// Formularios: con errores, el foco va al resumen (sus enlaces llevan a cada campo).
// Ruta lateral del registro: marca la sección que se está viendo.
(() => {
  "use strict";
  const resumen = document.querySelector("[data-resumen-errores]");
  const autofoco = document.querySelector("form [autofocus]");
  if (resumen) resumen.focus();
  // Chrome ignora autofocus si la URL tiene ancla (p. ej. al agregar un representante).
  else if (autofoco && document.activeElement === document.body) autofoco.focus();
  const ruta = document.querySelector("[data-ruta-form]");
  if (!ruta || !("IntersectionObserver" in window)) return;
  const enlaces = [...ruta.querySelectorAll("a")];
  const observador = new IntersectionObserver(
    (entradas) =>
      entradas.forEach((e) => {
        if (!e.isIntersecting) return;
        enlaces.forEach((a) => {
          if (a.hash === "#" + e.target.id) a.setAttribute("aria-current", "step");
          else a.removeAttribute("aria-current");
        });
      }),
    { rootMargin: "-40% 0px -55% 0px" }
  );
  enlaces.forEach((a) => {
    const seccion = document.getElementById(a.hash.slice(1));
    if (seccion) observador.observe(seccion);
  });
})();

// Filtros de donaciones: la subcategoría solo ofrece las de la categoría elegida (CA2).
(() => {
  "use strict";
  document.querySelectorAll("[data-filtros]").forEach((form) => {
    const categoria = form.querySelector("[data-filtro-categoria]");
    const subcategoria = form.querySelector("[data-filtro-subcategoria]");
    if (!categoria || !subcategoria) return;
    const sincronizar = () => {
      [...subcategoria.options].forEach((op) => {
        if (!op.dataset.categoria) return;
        const visible = !categoria.value || op.dataset.categoria === categoria.value;
        op.hidden = !visible;
        op.disabled = !visible;
      });
      if (subcategoria.selectedOptions[0]?.disabled) subcategoria.value = "";
    };
    categoria.addEventListener("change", sincronizar);
    sincronizar();
  });
})();
