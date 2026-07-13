/** Decodes a JWT payload without verifying the signature (client-side only). */
export function decodeToken(token) {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
  } catch {
    return null;
  }
}

/** Returns true if the token is present and not yet expired. */
export function isTokenValid(token) {
  if (!token) return false;
  const payload = decodeToken(token);
  if (!payload?.exp) return false;
  return payload.exp * 1000 > Date.now();
}

export const TOKEN_KEY = 'medibook_token';

export const saveToken  = (token) => localStorage.setItem(TOKEN_KEY, token);
export const getToken   = ()      => localStorage.getItem(TOKEN_KEY);
export const removeToken = ()     => localStorage.removeItem(TOKEN_KEY);