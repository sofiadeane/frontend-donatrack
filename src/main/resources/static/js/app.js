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
