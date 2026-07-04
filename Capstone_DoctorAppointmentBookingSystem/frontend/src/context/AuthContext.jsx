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
  const [showExpiryWarning, setShowExpiryWarning] = useState(false);

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

  /** Setup timers for session warning and auto-logout. */
  useEffect(() => {
    if (!token) {
      setShowExpiryWarning(false);
      return;
    }

    const payload = decodeToken(token);
    if (!payload?.exp) return;

    const calculateTimers = () => {
      const expiryMs = payload.exp * 1000;
      const timeLeft = expiryMs - Date.now();

      // Warning time is 2 minutes before expiry (120,000 ms)
      const warningDelay = timeLeft - 120 * 1000;

      const warningTimer = setTimeout(() => {
        setShowExpiryWarning(true);
      }, Math.max(0, warningDelay));

      const logoutTimer = setTimeout(() => {
        logout();
      }, Math.max(0, timeLeft));

      return { warningTimer, logoutTimer };
    };

    const timers = calculateTimers();

    return () => {
      clearTimeout(timers.warningTimer);
      clearTimeout(timers.logoutTimer);
    };
  }, [token]);

  /** Called after a successful login response. */
  const login = useCallback((tokenStr, userData) => {
    saveToken(tokenStr);
    setToken(tokenStr);
    setUser(userData);
    setShowExpiryWarning(false);

    // Role-based redirect
    switch (userData.role) {
      case 'DOCTOR': navigate('/doctor/dashboard', { replace: true }); break;
      case 'ADMIN':  navigate('/admin/dashboard',  { replace: true }); break;
      default:       navigate('/',                 { replace: true }); break;
    }
  }, [navigate]);

  /** Updates user data in context state. */
  const updateUser = useCallback((userData) => {
    setUser((prev) => (prev ? { ...prev, ...userData } : null));
  }, []);

  /** Clears auth state and redirects to login. */
  const logout = useCallback(() => {
    removeToken();
    setToken(null);
    setUser(null);
    setShowExpiryWarning(false);
    navigate('/login', { replace: true });
  }, [navigate]);

  const dismissExpiryWarning = useCallback(() => {
    setShowExpiryWarning(false);
  }, []);

  const value = useMemo(
    () => ({ user, token, ready, login, logout, updateUser, showExpiryWarning, dismissExpiryWarning }),
    [user, token, ready, login, logout, updateUser, showExpiryWarning, dismissExpiryWarning]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}