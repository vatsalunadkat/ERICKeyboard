/* ERICK Website — Interactivity: theme, font controls, nav */

(function () {
  "use strict";

  // ---- Theme toggle (light / dark) — DISABLED: hidden for now, light theme is default ----
  /*
  var themeBtn = document.getElementById("theme-toggle");
  var html = document.documentElement;

  function applyTheme(theme) {
    html.setAttribute("data-theme", theme);
    if (themeBtn) themeBtn.textContent = theme === "dark" ? "☀️" : "🌙";
    try { localStorage.setItem("erick-theme", theme); } catch (e) { /* ignore */ }
  }

  // Initialise from stored preference or system
  var stored = null;
  try { stored = localStorage.getItem("erick-theme"); } catch (e) { /* ignore */ }
  if (stored === "dark" || stored === "light") {
    applyTheme(stored);
  } else if (window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches) {
    applyTheme("dark");
  }

  if (themeBtn) {
    themeBtn.addEventListener("click", function () {
      var current = html.getAttribute("data-theme") || "light";
      applyTheme(current === "dark" ? "light" : "dark");
    });
  }
  */

  // ---- Font size controls ----
  var FONT_MIN = 14;
  var FONT_MAX = 22;
  var FONT_STEP = 2;
  var fontDecBtn = document.getElementById("font-decrease");
  var fontIncBtn = document.getElementById("font-increase");

  function getFontSize() {
    var stored;
    try { stored = localStorage.getItem("erick-font-size"); } catch (e) { /* ignore */ }
    return stored ? parseInt(stored, 10) : 16;
  }

  function setFontSize(size) {
    size = Math.max(FONT_MIN, Math.min(FONT_MAX, size));
    document.documentElement.style.setProperty("--base-font-size", size + "px");
    try { localStorage.setItem("erick-font-size", String(size)); } catch (e) { /* ignore */ }
  }

  // Apply stored font size on load
  setFontSize(getFontSize());

  if (fontDecBtn) {
    fontDecBtn.addEventListener("click", function () {
      setFontSize(getFontSize() - FONT_STEP);
    });
  }
  if (fontIncBtn) {
    fontIncBtn.addEventListener("click", function () {
      setFontSize(getFontSize() + FONT_STEP);
    });
  }

  // ---- Dyslexia font toggle ----
  var dyslexiaBtn = document.getElementById("dyslexia-toggle");

  function applyDyslexia(on) {
    if (on) {
      document.body.classList.add("dyslexia-font");
      if (dyslexiaBtn) dyslexiaBtn.classList.add("active");
    } else {
      document.body.classList.remove("dyslexia-font");
      if (dyslexiaBtn) dyslexiaBtn.classList.remove("active");
    }
    try { localStorage.setItem("erick-dyslexia", on ? "1" : "0"); } catch (e) { /* ignore */ }
  }

  // Restore
  var dyslexiaStored = null;
  try { dyslexiaStored = localStorage.getItem("erick-dyslexia"); } catch (e) { /* ignore */ }
  if (dyslexiaStored === "1") applyDyslexia(true);

  if (dyslexiaBtn) {
    dyslexiaBtn.addEventListener("click", function () {
      var isOn = document.body.classList.contains("dyslexia-font");
      applyDyslexia(!isOn);
    });
  }

  // ---- Mobile nav toggle ----
  var toggle = document.querySelector(".nav-toggle");
  var navLinks = document.querySelector(".nav-links");

  if (toggle && navLinks) {
    toggle.addEventListener("click", function () {
      var isOpen = navLinks.classList.toggle("open");
      toggle.setAttribute("aria-expanded", String(isOpen));
    });

    navLinks.querySelectorAll("a").forEach(function (link) {
      link.addEventListener("click", function () {
        navLinks.classList.remove("open");
        toggle.setAttribute("aria-expanded", "false");
      });
    });
  }

  // ---- Active nav link highlight ----
  var currentPage = window.location.pathname.split("/").pop() || "index.html";
  document.querySelectorAll(".nav-links a").forEach(function (link) {
    var href = link.getAttribute("href");
    if (href && href.split("#")[0] === currentPage) {
      link.classList.add("active");
    }
  });

  // ---- Scroll-reveal animation ----
  if ("IntersectionObserver" in window) {
    var revealItems = document.querySelectorAll(
      ".feature-card, .a11y-feature, .step, .persona-card"
    );

    revealItems.forEach(function (el) {
      el.style.opacity = "0";
      el.style.transform = "translateY(24px)";
      el.style.transition = "opacity 0.5s ease, transform 0.5s ease";
    });

    var observer = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry) {
          if (entry.isIntersecting) {
            entry.target.style.opacity = "1";
            entry.target.style.transform = "translateY(0)";
            observer.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.15 }
    );

    revealItems.forEach(function (el) {
      observer.observe(el);
    });
  }
})();
