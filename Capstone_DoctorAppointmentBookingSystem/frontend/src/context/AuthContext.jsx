import React, { createContext, useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import {
  decodeToken,
  getToken,
  isTokenValid,
  removeToken,
  saveToken,
} from '../utils/tokenHelpers';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user,  setUser]  = useState(null);
  const [token, setToken] = useState(null);
  const [ready, setReady] = useState(false); // prevents flicker on first load

  const navigate = useNavigate();

  /** Hydrate state from localStorage on mount. */
  useEffect(() => {
    const stored = getToken();
    if (stored && isTokenValid(stored)) {
      const payload = decodeToken(stored);
      setToken(stored);
      setUser({ id: payload.sub, email: payload.email, role: payload.role });
    } else {
      removeToken();
    }
    setReady(true);
  }, []);

  /** Called after a successful login response. */
  const login = useCallback((tokenStr, userData) => {
    saveToken(tokenStr);
    setToken(tokenStr);
    setUser(userData);

    // Role-based redirect
    switch (userData.role) {
      case 'DOCTOR': navigate('/doctor/dashboard', { replace: true }); break;
      case 'ADMIN':  navigate('/admin/dashboard',  { replace: true }); break;
      default:       navigate('/',                 { replace: true }); break;
    }
  }, [navigate]);

  /** Clears auth state and redirects to login. */
  const logout = useCallback(() => {
    removeToken();
    setToken(null);
    setUser(null);
    navigate('/login', { replace: true });
  }, [navigate]);

  const value = useMemo(
    () => ({ user, token, ready, login, logout }),
    [user, token, ready, login, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}