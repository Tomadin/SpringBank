/**
 * SpringBank — main.js
 * Funcionalidades: nav scroll, reveal animaciones, contador de stats, filtro de endpoints
 */

'use strict';

/* ============================================================
   NAV: añade clase al hacer scroll
   ============================================================ */
(function initNavScroll() {
  const nav = document.getElementById('nav');
  if (!nav) return;

  const SCROLL_THRESHOLD = 40;

  function onScroll() {
    if (window.scrollY > SCROLL_THRESHOLD) {
      nav.classList.add('nav--scrolled');
    } else {
      nav.classList.remove('nav--scrolled');
    }
  }

  window.addEventListener('scroll', onScroll, { passive: true });
  onScroll(); // estado inicial
})();

/* ============================================================
   SMOOTH SCROLL para enlaces de navegación internos
   ============================================================ */
(function initSmoothScroll() {
  document.querySelectorAll('a[href^="#"]').forEach(function (anchor) {
    anchor.addEventListener('click', function (e) {
      const targetId = this.getAttribute('href').slice(1);
      if (!targetId) return;

      const target = document.getElementById(targetId);
      if (!target) return;

      e.preventDefault();
      target.scrollIntoView({ behavior: 'smooth' });
    });
  });
})();

/* ============================================================
   REVEAL: Intersection Observer para animar elementos al entrar en viewport
   ============================================================ */
(function initReveal() {
  if (!('IntersectionObserver' in window)) {
    // Fallback: mostrar todos sin animación
    document.querySelectorAll('.reveal').forEach(function (el) {
      el.classList.add('is-visible');
    });
    return;
  }

  var observer = new IntersectionObserver(
    function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          entry.target.classList.add('is-visible');
          observer.unobserve(entry.target);
        }
      });
    },
    {
      threshold: 0.12,
      rootMargin: '0px 0px -48px 0px',
    }
  );

  document.querySelectorAll('.reveal').forEach(function (el) {
    observer.observe(el);
  });
})();

/* ============================================================
   CONTADOR DE STATS: anima los números del hero al entrar en pantalla
   ============================================================ */
(function initCounters() {
  var counterElements = document.querySelectorAll('.stat-card__num[data-target]');
  if (!counterElements.length) return;

  var DURATION = 1400; // ms
  var EASING_STEPS = 60;

  function easeOutQuart(t) {
    return 1 - Math.pow(1 - t, 4);
  }

  function animateCounter(el) {
    var target = parseInt(el.getAttribute('data-target'), 10);
    if (isNaN(target)) return;

    var startTime = null;

    function step(timestamp) {
      if (!startTime) startTime = timestamp;
      var elapsed = timestamp - startTime;
      var progress = Math.min(elapsed / DURATION, 1);
      var easedProgress = easeOutQuart(progress);
      var current = Math.round(easedProgress * target);

      el.textContent = current;

      if (progress < 1) {
        requestAnimationFrame(step);
      } else {
        el.textContent = target;
      }
    }

    requestAnimationFrame(step);
  }

  if (!('IntersectionObserver' in window)) {
    counterElements.forEach(function (el) {
      animateCounter(el);
    });
    return;
  }

  var counterObserver = new IntersectionObserver(
    function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          animateCounter(entry.target);
          counterObserver.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.5 }
  );

  counterElements.forEach(function (el) {
    counterObserver.observe(el);
  });
})();

/* ============================================================
   FILTRO DE ENDPOINTS: muestra/oculta filas por categoría
   ============================================================ */
(function initEndpointFilter() {
  var filterButtons = document.querySelectorAll('.filter-btn');
  var endpointRows  = document.querySelectorAll('.endpoint-row');

  if (!filterButtons.length || !endpointRows.length) return;

  filterButtons.forEach(function (btn) {
    btn.addEventListener('click', function () {
      var filter = this.getAttribute('data-filter');

      // Actualizar botones activos
      filterButtons.forEach(function (b) {
        b.classList.remove('filter-btn--active');
      });
      this.classList.add('filter-btn--active');

      // Mostrar/ocultar filas con animación
      endpointRows.forEach(function (row) {
        var group = row.getAttribute('data-group');

        if (filter === 'all' || group === filter) {
          row.classList.remove('hidden');
        } else {
          row.classList.add('hidden');
        }
      });
    });
  });
})();
