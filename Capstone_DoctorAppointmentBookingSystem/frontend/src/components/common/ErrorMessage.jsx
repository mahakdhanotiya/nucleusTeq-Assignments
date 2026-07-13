import React from 'react';

/** Inline field error text used beneath React Hook Form inputs. */
export function FieldError({ message }) {
  if (!message) return null;
  return <div className="invalid-feedback d-block">{message}</div>;
}

/** Page-level error/empty-state block. */
export default function ErrorMessage({ title = 'Something went wrong', message, onRetry }) {
  return (
    <div className="text-center py-5">
      <i className="bi bi-exclamation-circle text-danger" style={{ fontSize: '2.5rem' }} />
      <h5 className="mt-3 mb-1">{title}</h5>
      {message && <p className="text-muted small mb-3">{message}</p>}
      {onRetry && (
        <button className="btn btn-outline-primary btn-sm" onClick={onRetry}>
          <i className="bi bi-arrow-clockwise me-1" /> Retry
        </button>
      )}
    </div>
  );
}