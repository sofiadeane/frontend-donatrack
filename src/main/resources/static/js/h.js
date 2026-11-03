// Variante H · comportamientos propios. Mejora progresiva: sin JS la página funciona igual.
(() => {
  "use strict";
  const reducir = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

  // Las hormigas del hero se animan con SMIL, que no respeta prefers-reduced-motion: se congelan a mitad de camino.
  if (reducir) {
    document.querySelectorAll(".hm-escena").forEach((svg) => {
      svg.pauseAnimations?.();
      svg.setCurrentTime?.(5);
    });
  }
})();

// Mis donaciones: la subcategoría solo ofrece las de la categoría elegida (igual que en app.js).
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
