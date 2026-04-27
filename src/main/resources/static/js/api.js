// ═══════════════════════════════════════════════════
// SpringBank — Módulo central de API
// JWT payload: { sub: username, name: nombre, exp, iat }
// El rol NO está en el JWT; se obtiene llamando al API.
// ═══════════════════════════════════════════════════

const BASE = '/api/v1';

// ─── JWT ──────────────────────────────────────────
export const decodeJwt = (token) => {
  try {
    const part = token.split('.')[1];
    const b64  = part.replace(/-/g, '+').replace(/_/g, '/');
    const pad  = b64.padEnd(b64.length + (4 - b64.length % 4) % 4, '=');
    return JSON.parse(atob(pad));
  } catch { return null; }
};

// ─── Storage ──────────────────────────────────────
export const getToken    = () => localStorage.getItem('sb_token');
export const getRefresh  = () => localStorage.getItem('sb_refresh');
export const getUsername = () => localStorage.getItem('sb_username');
export const getClienteId= () => localStorage.getItem('sb_clienteId');
export const getRol      = () => localStorage.getItem('sb_rol');
export const getNombre   = () => localStorage.getItem('sb_nombre');

export const saveTokens = (token, refresh) => {
  localStorage.setItem('sb_token',   token);
  localStorage.setItem('sb_refresh', refresh);
  const p = decodeJwt(token);
  if (p?.sub)  localStorage.setItem('sb_username', p.sub);
  if (p?.name) localStorage.setItem('sb_nombre',   p.name);
};

export const saveUser = (rol, clienteId) => {
  if (rol)       localStorage.setItem('sb_rol',       rol);
  if (clienteId != null) localStorage.setItem('sb_clienteId', String(clienteId));
};

export const clearSession = () => {
  ['sb_token','sb_refresh','sb_username','sb_clienteId','sb_rol','sb_nombre']
    .forEach(k => localStorage.removeItem(k));
  window.location.href = '/login';
};

// ─── Guards ───────────────────────────────────────
export const requireAuth = () => {
  const t = getToken();
  if (!t) { window.location.href = '/login'; return false; }
  const p = decodeJwt(t);
  if (!p || p.exp * 1000 < Date.now()) { clearSession(); return false; }
  return true;
};

export const requireRole = (role) => {
  if (!requireAuth()) return false;
  const rol = getRol();
  if (rol && rol !== role) {
    window.location.href = rol === 'ADMIN' ? '/dashboard-admin' : '/dashboard';
    return false;
  }
  return true;
};

// ─── Toast ────────────────────────────────────────
export const toast = (msg, tipo = 'success') => {
  let c = document.getElementById('toast-container');
  if (!c) {
    c = document.createElement('div');
    c.id = 'toast-container';
    document.body.appendChild(c);
  }
  const icons = { success: '✓', error: '✕', warning: '⚠' };
  const el = document.createElement('div');
  el.className = `toast t-${tipo}`;
  el.innerHTML = `<span class="t-icon">${icons[tipo] ?? '•'}</span><span>${msg}</span>`;
  c.appendChild(el);
  requestAnimationFrame(() => requestAnimationFrame(() => el.classList.add('show')));
  setTimeout(() => {
    el.classList.remove('show');
    el.addEventListener('transitionend', () => el.remove(), { once: true });
  }, 3200);
};

// ─── Formateadores ────────────────────────────────
export const fmtMoney = (v) =>
  new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS', minimumFractionDigits: 2 }).format(v ?? 0);

export const fmtDate = (iso) => {
  if (!iso) return '—';
  return new Intl.DateTimeFormat('es-AR', { day:'2-digit', month:'short', year:'numeric', hour:'2-digit', minute:'2-digit' }).format(new Date(iso));
};

// ─── Error helper ─────────────────────────────────
export const errMsg = async (res) => {
  try {
    const ct = res.headers.get('content-type') ?? '';
    if (ct.includes('application/json')) {
      const d = await res.json();
      return d.message || d.mensaje || d.error || `Error ${res.status}`;
    }
    const text = await res.text();
    return text || `Error ${res.status}`;
  } catch { return `Error ${res.status}`; }
};

// ─── Fetch wrapper con auto-refresh ───────────────
// Devuelve el dato ya parseado (JSON u objeto, o texto plano).
// Lanza Error en respuestas no-2xx.
let refreshing = false;
let queue = [];

export const api = async (url, opts = {}) => {
  const token = getToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(opts.headers ?? {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };

  let res = await fetch(`${BASE}${url}`, { ...opts, headers });

  if (res.status === 401) {
    const rt = getRefresh();
    if (!rt) { clearSession(); return; }

    if (!refreshing) {
      refreshing = true;
      try {
        const rr = await fetch(`${BASE}/auth/refresh`, {
          method: 'POST',
          headers: { Authorization: `Bearer ${rt}`, 'Content-Type': 'application/json' }
        });
        if (rr.ok) {
          const d = await rr.json();
          saveTokens(d.token, d.refreshToken);
          queue.forEach(cb => cb(d.token));
          queue = [];
          res = await fetch(`${BASE}${url}`, { ...opts, headers: { ...headers, Authorization: `Bearer ${d.token}` } });
        } else { clearSession(); return; }
      } catch { clearSession(); return; }
      finally { refreshing = false; }
    } else {
      await new Promise(r => queue.push(r));
      const nt = getToken();
      res = await fetch(`${BASE}${url}`, { ...opts, headers: { ...headers, Authorization: `Bearer ${nt}` } });
    }
  }

  if (!res.ok) {
    const msg = await errMsg(res);
    if (res.status === 403) toast(msg || 'No tenés permisos para esta acción', 'error');
    throw new Error(msg);
  }

  const ct = res.headers.get('content-type') ?? '';
  if (ct.includes('application/json')) return res.json();
  return res.text();
};
