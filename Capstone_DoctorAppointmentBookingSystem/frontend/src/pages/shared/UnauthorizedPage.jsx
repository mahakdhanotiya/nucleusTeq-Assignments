import React from 'react';
import { Link } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';

export default function UnauthorizedPage() {
  const { logout } = useAuth();

  return (
    <div className="d-flex flex-column align-items-center justify-content-center text-center" style={{ minHeight: '100vh' }}>
      <i className="bi bi-shield-exclamation text-danger" style={{ fontSize: '3rem' }} />
      <h2 className="mt-3 mb-1">403 — Access Denied</h2>
      <p className="text-muted mb-4">You don't have permission to view this page.</p>
      <div className="d-flex gap-2">
        <Link to="/" className="btn btn-primary">
          <i className="bi bi-house me-1" /> Go Home
        </Link>
        <button className="btn btn-outline-secondary" onClick={logout}>
          Log Out
        </button>
      </div>
    </div>
  );
}