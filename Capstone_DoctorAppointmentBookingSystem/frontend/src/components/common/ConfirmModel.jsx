import React from 'react';

/**
 * Reusable confirmation dialog (Bootstrap modal markup, controlled via props).
 * No Bootstrap JS dependency — visibility controlled purely by `show`.
 */
export default function ConfirmModal({
  show,
  title = 'Are you sure?',
  message,
  confirmLabel = 'Confirm',
  cancelLabel = 'Cancel',
  variant = 'danger',
  loading = false,
  onConfirm,
  onCancel,
}) {
  if (!show) return null;

  return (
    <>
      <div className="modal d-block" tabIndex="-1" role="dialog">
        <div className="modal-dialog modal-dialog-centered" role="document">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">{title}</h5>
              <button type="button" className="btn-close" onClick={onCancel} disabled={loading} />
            </div>
            <div className="modal-body">
              <p className="mb-0">{message}</p>
            </div>
            <div className="modal-footer">
              <button className="btn btn-outline-secondary" onClick={onCancel} disabled={loading}>
                {cancelLabel}
              </button>
              <button className={`btn btn-${variant}`} onClick={onConfirm} disabled={loading}>
                {loading && <span className="spinner-border spinner-border-sm me-2" />}
                {confirmLabel}
              </button>
            </div>
          </div>
        </div>
      </div>
      <div className="modal-backdrop fade show" />
    </>
  );
}