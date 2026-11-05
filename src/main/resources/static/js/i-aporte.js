/* Tema abejas · panal de "Tus gotas aportadas": al tocar una celda se abre la tarjeta con las donaciones de ese estado.
 * Tocar la misma celda, la × o Esc la cierra. El estado vive en la página (sin guardar nada). */
(() => {
  const panal = document.querySelector(".ap-a__panal");
  const tarjeta = document.getElementById("ap-det");
  if (!panal || !tarjeta) return;
  const celdas = [...panal.querySelectorAll("[data-celda]")];
  const paneles = [...tarjeta.querySelectorAll(".ap-det__p")];
  let abierta = null;

  function mostrar(clave, moverFoco) {
    abierta = clave;
    celdas.forEach(c => {
      const es = c.dataset.celda === clave;
      c.setAttribute("aria-expanded", String(es));
      c.classList.toggle("is-sel", es);
    });
    paneles.forEach(p => { p.hidden = p.dataset.celda !== clave; });
    tarjeta.hidden = clave === null;
    if (clave !== null && moverFoco) tarjeta.querySelector(".ap-det__p:not([hidden]) [data-cerrar]")?.focus({ preventScroll: true });
    if (clave !== null) tarjeta.scrollIntoView({ block: "nearest", behavior: matchMedia("(prefers-reduced-motion: reduce)").matches ? "auto" : "smooth" });
  }

  function alternar(celda, desdeTeclado) {
    const clave = celda.dataset.celda;
    if (abierta === clave) { mostrar(null); celda.focus({ preventScroll: true }); } else mostrar(clave, desdeTeclado);
  }

  celdas.forEach(c => {
    c.addEventListener("click", () => alternar(c, false));
    c.addEventListener("keydown", e => {
      if (e.key === "Enter" || e.key === " ") { e.preventDefault(); alternar(c, true); }
    });
  });
  tarjeta.addEventListener("click", e => {
    if (!e.target.closest("[data-cerrar]")) return;
    const c = celdas.find(x => x.dataset.celda === abierta);
    mostrar(null); c?.focus({ preventScroll: true });
  });
  document.addEventListener("keydown", e => {
    if (e.key !== "Escape" || abierta === null) return;
    const c = celdas.find(x => x.dataset.celda === abierta);
    mostrar(null); c?.focus({ preventScroll: true });
  });
})();
