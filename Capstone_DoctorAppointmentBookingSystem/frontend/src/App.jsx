import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';

import ProtectedRoute from './components/common/ProtectedRoute';
import AuthLayout from './layouts/AuthLayout';
import DashboardLayout from './layouts/DashboardLayout';

import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import NotFoundPage from './pages/shared/PageNotFound';
import UnauthorizedPage from './pages/shared/UnauthorizedPage';


import PatientHomePage from './pages/patient/PatientHomePage';
import DoctorDetailPage from './pages/patient/DoctorDetailPage';
import PatientAppointmentsPage from './pages/patient/PatientAppointmentsPage';
import PaymentPage from './pages/patient/PaymentPage';
import PaymentSuccessPage from './pages/patient/PaymentSuccessPage';

import DoctorDashboardPage from './pages/doctor/DoctorDashboardPage';
import DoctorAppointmentsPage from './pages/doctor/DoctorAppointmentsPage';

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

      {/* ── Patient routes ─────────────────────────────── */}
      <Route element={<ProtectedRoute allowedRoles={['PATIENT']} />}>
        <Route element={<DashboardLayout />}>
          <Route path="/" element={<PatientHomePage />} />
          <Route path="/doctors/:userId" element={<DoctorDetailPage />} />
          <Route path="/my-appointments" element={<PatientAppointmentsPage />} />
        </Route>
        {/* Full-screen secure checkout views */}
        <Route path="/payment/:appointmentId" element={<PaymentPage />} />
        <Route path="/payment-success/:appointmentId" element={<PaymentSuccessPage />} />
      </Route>

      {/* ── Doctor routes ──────────────────────────────── */}
      <Route element={<ProtectedRoute allowedRoles={['DOCTOR']} />}>
        <Route element={<DashboardLayout />}>
          <Route path="/doctor/dashboard" element={<DoctorDashboardPage />} />
          <Route path="/doctor/appointments" element={<DoctorAppointmentsPage />} />
        </Route>
      </Route>

      {/* ── Authenticated routes (any role) ───────────── */}
      <Route element={<ProtectedRoute allowedRoles={['PATIENT', 'DOCTOR', 'ADMIN']} />}>
        <Route element={<DashboardLayout />}>
          {/* Shared pages (profile, change-password) will be
              registered here in subsequent modules. */}
        </Route>
      </Route>

      {/* ── Fallbacks ──────────────────────────────────── */}
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}