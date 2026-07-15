import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import Spinner from './Spinner';

/**
 * Route guard. Redirects to /login if unauthenticated,
 * or to /unauthorized if the user's role is not in `allowedRoles`.
 * Renders the matched child route via <Outlet /> when authorized.
 */
export default function ProtectedRoute({ allowedRoles }) {
  const { user, token, ready } = useAuth();

  if (!ready) return <Spinner full label="Checking session..." />;

  if (!token || !user) return <Navigate to="/login" replace />;

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <Outlet />;
}