import React, { useState } from 'react';
import { NavLink, Outlet } from 'react-router-dom';

import useAuth from '../hooks/useAuth';

/** Navigation items per role. Keeps DashboardLayout role-agnostic and reusable. */
const NAV_ITEMS = {
  PATIENT: [
    { to: '/',                label: 'Find Doctors',     icon: 'bi-search' },
    { to: '/my-appointments', label: 'My Appointments',  icon: 'bi-calendar-check' },
  ],
  DOCTOR: [
    { to: '/doctor/dashboard',    label: 'Dashboard',           icon: 'bi-grid-1x2' },
    { to: '/doctor/appointments', label: 'Appointments',        icon: 'bi-calendar-check' },
    { to: '/doctor/slots',        label: 'Manage Availability', icon: 'bi-calendar3' },
    { to: '/doctor/leave-requests', label: 'Leave Request', icon: 'bi-calendar-x' },
  ],
  ADMIN: [
    { to: '/admin/dashboard', label: 'Dashboard',      icon: 'bi-speedometer2' },
    { to: '/admin/doctors',   label: 'Manage Doctors', icon: 'bi-people-fill' },
    { to: '/admin/leave-requests', label: 'Doctor Leave Requests', icon: 'bi-journal-x' },
  ],
};

const ROLE_LABEL = { PATIENT: 'Patient', DOCTOR: 'Doctor', ADMIN: 'Administrator' };

/**
 * Shared shell for every authenticated page: sidebar + topbar + content slot.
 * Sidebar links are derived from the logged-in user's role.
 * Used via React Router's <Outlet /> so each role's pages reuse this layout.
 */
export default function DashboardLayout() {
  const { user, logout, showExpiryWarning, dismissExpiryWarning } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const navItems = NAV_ITEMS[user?.role] || [];

  return (
    <div className="dashboard-layout">
      {/* ── Sidebar ─────────────────────────────────────── */}
      <aside className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        <span className="sidebar-brand">
          <i className="bi bi-heart-pulse-fill me-2" />
          MediBook
        </span>

        <nav className="sidebar-nav">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end
              className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
              onClick={() => setSidebarOpen(false)}
            >
              <i className={`bi ${item.icon}`} />
              {item.label}
            </NavLink>
          ))}

          <div className="divider mx-3" />

          <NavLink
            to="/profile"
            className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
            onClick={() => setSidebarOpen(false)}
          >
            <i className="bi bi-person-circle" />
            My Profile
          </NavLink>
          <NavLink
            to="/change-password"
            className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
            onClick={() => setSidebarOpen(false)}
          >
            <i className="bi bi-shield-lock" />
            Change Password
          </NavLink>
        </nav>

        <div className="sidebar-footer">
          <button className="btn btn-outline-danger btn-sm w-100" onClick={logout}>
            <i className="bi bi-box-arrow-right me-1" />
            Logout
          </button>
        </div>
      </aside>

      {/* ── Main content ────────────────────────────────── */}
      <div className="dashboard-main">
        {showExpiryWarning && (
          <div 
            className="alert alert-warning alert-dismissible fade show border-0 rounded-0 m-0 py-2.5 px-4 d-flex justify-content-between align-items-center shadow-sm w-100" 
            role="alert" 
            style={{ zIndex: 1050, backgroundColor: '#fef3c7', color: '#b45309' }}
          >
            <div className="d-flex align-items-center gap-2">
              <i className="bi bi-exclamation-triangle-fill fs-5"></i>
              <span className="fw-600 small">Your session is about to expire in less than 2 minutes. Please save any unsaved changes to prevent losing work.</span>
            </div>
            <button 
              type="button" 
              className="btn-close py-2.5" 
              style={{ filter: 'invert(32%) sepia(87%) saturate(464%) hue-rotate(6deg) brightness(92%) contrast(89%)' }} 
              aria-label="Close" 
              onClick={dismissExpiryWarning}
            ></button>
          </div>
        )}

        <header className="dashboard-topbar">
          <button
            className="btn btn-outline-secondary btn-sm d-md-none"
            onClick={() => setSidebarOpen((o) => !o)}
          >
            <i className="bi bi-list" />
          </button>

          <div className="ms-auto d-flex align-items-center gap-2">
            <div className="text-end d-none d-sm-block">
              <div className="fw-600 small">{user?.email}</div>
              <div className="text-muted" style={{ fontSize: '.75rem' }}>
                {ROLE_LABEL[user?.role] || user?.role}
              </div>
            </div>
            <div
              className="doctor-avatar"
              style={{ width: 38, height: 38, fontSize: '1rem' }}
            >
              {user?.email?.[0]?.toUpperCase() || '?'}
            </div>
          </div>
        </header>

        <main className="dashboard-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}