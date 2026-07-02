import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';

import ProtectedRoute from './components/common/ProtectedRoute';
import AuthLayout from './layouts/AuthLayout';
import DashboardLayout from './layouts/DashboardLayout';

import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import NotFoundPage from './pages/shared/PageNotFound';
import UnauthorizedPage from './pages/shared/UnauthorizedPage';

/**
 * Root route declarations.
 * Patient / Doctor / Admin feature pages are added here as their
 * respective modules are implemented — DashboardLayout is already
 * wired to support all three roles via role-based sidebar nav.
 */
export default function App() {
  return (
    <Routes>
      {/* ── Public auth routes ─────────────────────────── */}
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>

      <Route path="/unauthorized" element={<UnauthorizedPage />} />

      {/* ── Authenticated routes (any role) ───────────── */}
      <Route element={<ProtectedRoute allowedRoles={['PATIENT', 'DOCTOR', 'ADMIN']} />}>
        <Route element={<DashboardLayout />}>
          {/* Patient / Doctor / Admin / shared pages register their
              routes here in subsequent modules. */}
        </Route>
      </Route>

      {/* ── Fallbacks ──────────────────────────────────── */}
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}