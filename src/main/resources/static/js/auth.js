import { saveTokens, saveUser, clearSession, decodeJwt, getToken, getRefresh, getRol, errMsg } from './api.js';

const BASE = '/api/v1';

// ─── Validadores ──────────────────────────────────
export const validDni      = (v) => /^\d{8}$/.test(v.trim());
export const validEmail    = (v) => { const e = v.trim(); return e.length >= 5 && e.includes('@') && e.includes('.') && !e.includes(' '); };
export const validPassword = (v) => v.length >= 8;

export const passwordStrength = (p) => {
  if (!p) return null;
  let s = 0;
  if (p.length >= 8)             s++;
  if (p.length >= 12)            s++;
  if (/[A-Z]/.test(p))          s++;
  if (/[0-9]/.test(p))          s++;
  if (/[^A-Za-z0-9]/.test(p))   s++;
  return s <= 2 ? 'weak' : s <= 3 ? 'medium' : 'strong';
};

// ─── Obtener info de usuario post-login ───────────
const fetchUserInfo = async (username, token) => {
  try {
    const res = await fetch(`${BASE}/usuarios/${username}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    if (res.ok) {
      const d = await res.json();
      // d.clienteId puede venir como número o como objeto según el DTO
      const cid = d.clienteId ?? d.cliente ?? null;
      saveUser(d.rol, cid);
    }
  } catch { /* no crítico */ }
};

// ─── Login ────────────────────────────────────────
export const login = async (username, password) => {
  const res = await fetch(`${BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  if (!res.ok) throw new Error(await errMsg(res));
  const d = await res.json();
  saveTokens(d.token, d.refreshToken);
  await fetchUserInfo(username, d.token);
  redirect();
};

// ─── Registro cliente + usuario ───────────────────
export const registrar = async ({ nombre, apellido, email, dni, username, password }) => {
  const res = await fetch(`${BASE}/auth/registrar-cliente-usuario`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nombre, apellido, email, dni, username, password, rol: 'CLIENTE' })
  });
  if (!res.ok) throw new Error(await errMsg(res));
  const d = await res.json();
  saveTokens(d.token, d.refreshToken);
  await fetchUserInfo(username, d.token);
  window.location.href = '/dashboard';
};

// ─── Logout ───────────────────────────────────────
export const logout = async () => {
  const t = getToken();
  if (t) {
    try {
      await fetch(`${BASE}/auth/logout`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${t}` }
      });
    } catch { /* ignorar */ }
  }
  clearSession();
};

// ─── Redirect según rol ───────────────────────────
export const redirect = () => {
  const rol = getRol();
  window.location.href = rol === 'ADMIN' ? '/dashboard-admin' : '/dashboard';
};

// ─── Redirigir si ya está autenticado ─────────────
export const redirectIfLoggedIn = () => {
  const t = getToken();
  if (!t) return;
  const p = decodeJwt(t);
  if (!p || p.exp * 1000 < Date.now()) return;
  redirect();
};
