import React from 'react';
import { Link } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';

export default function NotFoundPage() {
  const { user } = useAuth();

  let homePath = '/';
  if (user) {
    if (user.role === 'DOCTOR') homePath = '/doctor/dashboard';
    else if (user.role === 'ADMIN') homePath = '/admin/dashboard';
  }

  return (
    <div className="d-flex flex-column align-items-center justify-content-center text-center" style={{ minHeight: '100vh' }}>
      <i className="bi bi-compass text-primary" style={{ fontSize: '3rem' }} />
      <h2 className="mt-3 mb-1">404 — Page Not Found</h2>
      <p className="text-muted mb-4">The page you're looking for doesn't exist.</p>
      <Link to={homePath} className="btn btn-primary">
        <i className="bi bi-house me-1" /> Go Home
      </Link>
    </div>
  );
}