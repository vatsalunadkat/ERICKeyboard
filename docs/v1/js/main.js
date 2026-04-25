/* ERICK Website - Interactivity: theme, font controls, nav */

(function () {
  "use strict";

  // ---- Theme toggle (light / dark) - DISABLED: hidden for now, light theme is default ----
  // var themeBtn = document.getElementById("theme-toggle");
  // var html = document.documentElement;
  //
  // function applyTheme(theme) {
  //   html.setAttribute("data-theme", theme);
  //   if (themeBtn) themeBtn.textContent = theme === "dark" ? "☀️" : "🌙";
  //   try { localStorage.setItem("erick-theme", theme); } catch (e) {}
  // }
  //
  // // Initialise from stored preference or system
  // var stored = null;
  // try { stored = localStorage.getItem("erick-theme"); } catch (e) {}
  // if (stored === "dark" || stored === "light") {
  //   applyTheme(stored);
  // } else if (window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches) {
  //   applyTheme("dark");
  // }
  //
  // if (themeBtn) {
  //   themeBtn.addEventListener("click", function () {
  //     var current = html.getAttribute("data-theme") || "light";
  //     applyTheme(current === "dark" ? "light" : "dark");
  //   });
  // }

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

  // ---- Media lightbox ----
  var lightboxTargets = document.querySelectorAll(".demo-item img, .demo-item video, .release-media img, .release-media video");

  if (lightboxTargets.length) {
    var lightbox = document.createElement("div");
    lightbox.className = "media-lightbox";
    lightbox.hidden = true;
    lightbox.innerHTML = [
      '<div class="media-lightbox-dialog" role="dialog" aria-modal="true" aria-label="Expanded image view">',
      '  <button class="media-lightbox-close" type="button" aria-label="Close expanded image">×</button>',
      '  <figure class="media-lightbox-figure">',
      '    <div class="media-lightbox-image-wrap">',
      '      <img class="media-lightbox-image" alt="" hidden />',
      '      <video class="media-lightbox-video" controls loop playsinline hidden></video>',
      '    </div>',
      '    <figcaption class="media-lightbox-caption"></figcaption>',
      '  </figure>',
      '</div>'
    ].join("");
    document.body.appendChild(lightbox);

    var lightboxDialog = lightbox.querySelector(".media-lightbox-dialog");
    var lightboxImage = lightbox.querySelector(".media-lightbox-image");
    var lightboxVideo = lightbox.querySelector(".media-lightbox-video");
    var lightboxCaption = lightbox.querySelector(".media-lightbox-caption");
    var lightboxClose = lightbox.querySelector(".media-lightbox-close");
    var lastTrigger = null;
    var lastTriggerWasPlaying = false;

    function getCaption(target) {
      return target.getAttribute("data-media-caption") || target.alt || target.getAttribute("aria-label") || "Media";
    }

    function getMediaSource(target) {
      if (target.currentSrc) {
        return target.currentSrc;
      }
      if (target.src) {
        return target.src;
      }

      var source = target.querySelector("source");
      return source ? source.src : "";
    }

    function isVideoTarget(target) {
      return target.tagName === "VIDEO";
    }

    function playIfPossible(target) {
      var playPromise = target.play();

      if (playPromise && playPromise.catch) {
        playPromise.catch(function () {});
      }
    }

    function closeLightbox() {
      var triggerToFocus = lastTrigger;

      lightbox.hidden = true;
      document.body.classList.remove("lightbox-open");
      lightboxImage.hidden = true;
      lightboxImage.removeAttribute("src");
      lightboxImage.alt = "";
      lightboxVideo.pause();
      lightboxVideo.hidden = true;
      lightboxVideo.removeAttribute("src");
      lightboxVideo.removeAttribute("aria-label");
      lightboxVideo.load();
      lightboxCaption.textContent = "";

      if (lastTrigger && isVideoTarget(lastTrigger) && lastTriggerWasPlaying) {
        playIfPossible(lastTrigger);
      }

      lastTrigger = null;
      lastTriggerWasPlaying = false;

      if (triggerToFocus) {
        triggerToFocus.focus();
      }
    }

    function openLightbox(target) {
      var caption = getCaption(target);

      lastTrigger = target;
      lastTriggerWasPlaying = isVideoTarget(target) && !target.paused;
      lightboxCaption.textContent = caption;

      if (isVideoTarget(target)) {
        target.pause();
        lightboxImage.hidden = true;
        lightboxImage.removeAttribute("src");
        lightboxImage.alt = "";

        lightboxVideo.src = getMediaSource(target);
        lightboxVideo.muted = target.muted;
        lightboxVideo.hidden = false;
        lightboxVideo.setAttribute("aria-label", caption);
        lightboxVideo.load();
      } else {
        lightboxVideo.pause();
        lightboxVideo.hidden = true;
        lightboxVideo.removeAttribute("src");
        lightboxVideo.removeAttribute("aria-label");
        lightboxVideo.load();

        lightboxImage.src = getMediaSource(target);
        lightboxImage.alt = caption;
        lightboxImage.hidden = false;
      }

      lightbox.hidden = false;
      document.body.classList.add("lightbox-open");
      lightboxClose.focus();

      if (isVideoTarget(target)) {
        playIfPossible(lightboxVideo);
      }
    }

    lightboxTargets.forEach(function (media) {
      var caption = getCaption(media);

      media.tabIndex = 0;
      media.setAttribute("role", "button");
      media.setAttribute("aria-label", caption + " - open larger view");

      media.addEventListener("click", function () {
        openLightbox(media);
      });

      media.addEventListener("keydown", function (event) {
        if (event.key === "Enter" || event.key === " ") {
          event.preventDefault();
          openLightbox(media);
        }
      });
    });

    lightbox.addEventListener("click", function (event) {
      if (!lightboxDialog.contains(event.target) || event.target === lightboxClose) {
        closeLightbox();
      }
    });

    document.addEventListener("keydown", function (event) {
      if (event.key === "Escape" && !lightbox.hidden) {
        closeLightbox();
      }
    });
  }
})();