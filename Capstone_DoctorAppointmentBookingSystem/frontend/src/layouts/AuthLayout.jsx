import React from 'react';
import { Outlet } from 'react-router-dom';

/** Centered card layout shared by Login and Register pages. */
export default function AuthLayout() {
  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div className="auth-logo">
          <i className="bi bi-heart-pulse-fill me-2" />
          MediBook
        </div>
        <p className="auth-subtitle">Your trusted doctor appointment platform</p>
        <Outlet />
      </div>
    </div>
  );
}