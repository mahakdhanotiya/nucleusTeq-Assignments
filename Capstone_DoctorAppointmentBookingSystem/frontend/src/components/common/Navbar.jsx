import React from 'react';
import { Link } from 'react-router-dom';

/** Public-facing top navbar used on auth pages only. */
export default function Navbar() {
  return (
    <nav className="app-navbar navbar navbar-expand-lg py-3">
      <div className="container">
        <Link className="navbar-brand" to="/login">
          <i className="bi bi-heart-pulse-fill me-2" />
          MediBook
        </Link>
      </div>
    </nav>
  );
}