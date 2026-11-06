/* Variante K: botón de modo claro/oscuro (se recuerda en este navegador), frutos/raíces/pasos que aparecen una sola vez y el selector de Quién participa. */
(function () {
  var raiz = document.documentElement;
  document.addEventListener("click", function (e) {
    var b = e.target.closest("[data-k-modo]");
    if (!b) return;
    var oscuro = raiz.getAttribute("data-modo") !== "oscuro";
    if (oscuro) raiz.setAttribute("data-modo", "oscuro"); else raiz.removeAttribute("data-modo");
    b.setAttribute("aria-pressed", String(oscuro));
    try { localStorage.setItem("k-modo", oscuro ? "oscuro" : "claro"); } catch (err) { /* sin almacenamiento */ }
  });
  // Quién participa: cada planta es una pestaña; flechas para moverse entre ellas.
  function iniciarEleccion() {
    document.querySelectorAll("[data-k-elige]").forEach(function (g) {
      var tabs = Array.prototype.slice.call(g.querySelectorAll('[role="tab"]'));
      function elegir(t, foco) {
        tabs.forEach(function (x) {
          var si = x === t;
          x.setAttribute("aria-selected", String(si));
          x.tabIndex = si ? 0 : -1;
          document.getElementById(x.getAttribute("aria-controls")).classList.toggle("is-activo", si);
        });
        if (foco) t.focus();
      }
      tabs.forEach(function (t, i) {
        t.addEventListener("click", function () { elegir(t); });
        t.addEventListener("keydown", function (e) {
          var d = e.key === "ArrowRight" ? 1 : e.key === "ArrowLeft" ? -1 : 0;
          if (e.key === "Home") { d = -i; } else if (e.key === "End") { d = tabs.length - 1 - i; }
          if (!d) return;
          e.preventDefault();
          elegir(tabs[(i + d + tabs.length) % tabs.length], true);
        });
      });
    });
  }
  document.addEventListener("DOMContentLoaded", function () {
    var b = document.querySelector("[data-k-modo]");
    if (b) b.setAttribute("aria-pressed", String(raiz.getAttribute("data-modo") === "oscuro"));
    // Si las raíces no entran (celular), arrancan centradas: la plantita y la raíz principal quedan a la vista.
    document.querySelectorAll(".k-raices").forEach(function (r) { r.scrollLeft = (r.scrollWidth - r.clientWidth) / 2; });
    var piezas = document.querySelectorAll(".k-anima");
    iniciarEleccion();
    if (!("IntersectionObserver" in window)) { document.querySelectorAll(".k-anima, .k-revela").forEach(function (p) { p.classList.add("aparece"); }); return; }
    var io = new IntersectionObserver(function (es) {
      es.forEach(function (en) { if (en.isIntersecting) { en.target.classList.add("aparece"); io.unobserve(en.target); } });
    }, { threshold: 0.3 });
    piezas.forEach(function (p) { io.observe(p); });
    // Pasos de Cómo funciona: aparecen de a uno al scrollear; si entran varios juntos, se escalonan.
    var pasos = document.querySelectorAll(".k-revela");
    var ip = new IntersectionObserver(function (es) {
      es.filter(function (en) { return en.isIntersecting; }).forEach(function (en, i) {
        en.target.style.animationDelay = i * 220 + "ms";
        en.target.classList.add("aparece");
        ip.unobserve(en.target);
      });
    }, { threshold: 0.25, rootMargin: "0px 0px -12% 0px" });
    pasos.forEach(function (p) { ip.observe(p); });
  });
})();
