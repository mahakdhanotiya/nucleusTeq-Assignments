import React from 'react';

/** Centered loading spinner. Use `full` for a full-page overlay. */
export default function Spinner({ full = false, label = 'Loading...' }) {
  const wrapperClass = full
    ? 'd-flex flex-column align-items-center justify-content-center'
    : 'd-flex flex-column align-items-center justify-content-center py-5';

  const style = full ? { minHeight: '60vh' } : undefined;

  return (
    <div className={wrapperClass} style={style}>
      <div className="spinner-border text-primary" role="status" style={{ width: '2.5rem', height: '2.5rem' }}>
        <span className="visually-hidden">{label}</span>
      </div>
      <p className="text-muted mt-3 mb-0 small">{label}</p>
    </div>
  );
}