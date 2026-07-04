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
  ],
  ADMIN: [
    { to: '/admin/dashboard', label: 'Dashboard',      icon: 'bi-speedometer2' },
    { to: '/admin/doctors',   label: 'Manage Doctors', icon: 'bi-people-fill' },
  ],
};

const ROLE_LABEL = { PATIENT: 'Patient', DOCTOR: 'Doctor', ADMIN: 'Administrator' };

/**
 * Shared shell for every authenticated page: sidebar + topbar + content slot.
 * Sidebar links are derived from the logged-in user's role.
 * Used via React Router's <Outlet /> so each role's pages reuse this layout.
 */
export default function DashboardLayout() {
  const { user, logout } = useAuth();
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