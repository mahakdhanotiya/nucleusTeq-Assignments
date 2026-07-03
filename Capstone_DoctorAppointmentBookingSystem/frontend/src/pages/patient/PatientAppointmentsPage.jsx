import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { getMyAppointments, cancelAppointment } from '../../api/appointmentApi';
import useToast from '../../hooks/useToast';
import { paymentPath } from '../../routes/routePaths';

const STATUS_BADGES = {
  CONFIRMED: { bg: '#dbeafe', color: '#1d4ed8', icon: 'bi-clock-fill', label: 'Confirmed' },
  COMPLETED: { bg: '#dcfce7', color: '#15803d', icon: 'bi-check-circle-fill', label: 'Completed' },
  CANCELLED: { bg: '#fee2e2', color: '#b91c1c', icon: 'bi-x-circle-fill', label: 'Cancelled' },
  NO_SHOW:   { bg: '#f3f4f6', color: '#4b5563', icon: 'bi-dash-circle-fill', label: 'No Show' },
};

const TABS = [
  { key: 'upcoming', label: 'Upcoming', icon: 'bi-calendar-event' },
  { key: 'past',     label: 'Past History', icon: 'bi-clock-history' },
];

export default function PatientAppointmentsPage() {
  const navigate = useNavigate();
  const toast = useToast();

  const [activeTab, setActiveTab] = useState('upcoming');
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [cancelModal, setCancelModal] = useState({ open: false, id: null });
  const [cancelReason, setCancelReason] = useState('');
  const [cancelling, setCancelling] = useState(false);
  const [dateFilter, setDateFilter] = useState('');

  const fetchAppointments = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getMyAppointments();
      const all = res.data || [];
      setAppointments(all);
    } catch (err) {
      if (!err.toasted) {
        toast.error('Failed to load appointments.');
      }
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchAppointments();
  }, [fetchAppointments]);

  // Filter based on active tab and optional date
  const filtered = appointments.filter((a) => {
    const matchesTab = activeTab === 'upcoming'
      ? a.status === 'CONFIRMED'
      : ['COMPLETED', 'CANCELLED', 'NO_SHOW'].includes(a.status);

    if (!matchesTab) return false;

    if (dateFilter) {
      return a.appointment_date === dateFilter;
    }
    return true;
  });

  // Cancel logic
  const openCancelModal = (id) => {
    setCancelModal({ open: true, id });
    setCancelReason('');
  };

  const closeCancelModal = () => {
    setCancelModal({ open: false, id: null });
    setCancelReason('');
  };

  const handleCancel = async () => {
    if (!cancelModal.id) return;
    setCancelling(true);
    try {
      await cancelAppointment(cancelModal.id, { reason: cancelReason || undefined });
      toast.success('Appointment cancelled successfully.');
      closeCancelModal();
      fetchAppointments();
    } catch (err) {
      if (!err.toasted) {
        toast.error(
          err.response?.data?.message || 'Failed to cancel. Please try again.'
        );
      }
    } finally {
      setCancelling(false);
    }
  };

  // Helper: format date
  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-IN', {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  // Helper: format time
  const formatTime = (time24) => {
    if (!time24) return '';
    try {
      const [hourStr, minStr] = time24.split(':');
      const hour = parseInt(hourStr, 10);
      const ampm = hour >= 12 ? 'PM' : 'AM';
      const formattedHour = hour % 12 || 12;
      return `${formattedHour}:${minStr} ${ampm}`;
    } catch {
      return time24;
    }
  };

  // Helper: get initials
  const getInitials = (name) => {
    if (!name) return '?';
    return name
      .replace(/^Dr\.\s*/i, '')
      .split(' ')
      .map((p) => p[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  // Check if appointment can be cancelled (>2 hours before start)
  const canCancel = (appt) => {
    if (appt.status !== 'CONFIRMED') return false;
    try {
      const [h, m] = appt.start_time.split(':').map(Number);
      const apptDate = new Date(appt.appointment_date);
      apptDate.setHours(h, m, 0, 0);
      const now = new Date();
      const diffMs = apptDate.getTime() - now.getTime();
      return diffMs > 2 * 60 * 60 * 1000; // >2 hours
    } catch {
      return false;
    }
  };

  return (
    <div>
      {/* Page Header */}
      <div className="page-header mb-4">
        <h1>My Appointments</h1>
        <p>View and manage your upcoming and past appointments.</p>
      </div>

      {/* Tabs and Date Filter */}
      <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
        <div className="d-flex gap-2">
          {TABS.map((tab) => (
            <button
              key={tab.key}
              className={`btn rounded-pill px-4 py-2 fw-600 d-flex align-items-center gap-2 ${
                activeTab === tab.key
                  ? 'btn-primary'
                  : 'btn-outline-secondary'
              }`}
              onClick={() => {
                setActiveTab(tab.key);
                setDateFilter('');
              }}
            >
              <i className={`bi ${tab.icon}`} />
              {tab.label}
            </button>
          ))}
        </div>

        {/* Date filter input */}
        <div className="d-flex align-items-center gap-2">
          <label className="form-label mb-0 fw-600 small text-muted">Filter by date:</label>
          <input
            type="date"
            className="form-control form-control-sm"
            style={{ maxWidth: '200px' }}
            value={dateFilter}
            onChange={(e) => setDateFilter(e.target.value)}
          />
          {dateFilter && (
            <button
              className="btn btn-sm btn-outline-secondary rounded-pill"
              onClick={() => setDateFilter('')}
            >
              <i className="bi bi-x-lg" />
            </button>
          )}
        </div>
      </div>

      {/* Loading State */}
      {loading && (
        <div className="d-flex flex-column align-items-center justify-content-center py-5">
          <div className="spinner-border text-primary mb-3" style={{ width: '3rem', height: '3rem' }} role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
          <p className="text-muted">Loading your appointments...</p>
        </div>
      )}

      {/* Empty State */}
      {!loading && filtered.length === 0 && (
        <div className="card border-0 shadow-sm p-5 text-center">
          <i
            className={`bi ${activeTab === 'upcoming' ? 'bi-calendar-x' : 'bi-clock-history'} text-muted d-block mb-3`}
            style={{ fontSize: '3rem', opacity: 0.4 }}
          />
          <h5 className="fw-bold text-dark mb-2">
            {activeTab === 'upcoming' ? 'No Upcoming Appointments' : 'No Past Appointments'}
          </h5>
          <p className="text-muted mb-4">
            {activeTab === 'upcoming'
              ? "You don't have any confirmed upcoming appointments."
              : 'Your past appointment history will appear here.'}
          </p>
          {activeTab === 'upcoming' && (
            <button
              className="btn btn-primary rounded-pill px-4 py-2 fw-600 mx-auto"
              onClick={() => navigate('/')}
            >
              <i className="bi bi-search me-2" />
              Find a Doctor
            </button>
          )}
        </div>
      )}

      {/* Appointment Cards */}
      {!loading && filtered.length > 0 && (
        <div className="row g-3">
          {filtered.map((appt) => {
            const badge = STATUS_BADGES[appt.status] || STATUS_BADGES.CONFIRMED;
            return (
              <div key={appt.id} className="col-12">
                <div className="card border-0 shadow-sm p-4 hover-lift">
                  <div className="d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-3">
                    {/* Left: Doctor Info */}
                    <div className="d-flex align-items-center gap-3 flex-grow-1">
                      <div
                        className="rounded-circle d-flex align-items-center justify-content-center text-white fw-bold flex-shrink-0"
                        style={{
                          width: '52px',
                          height: '52px',
                          fontSize: '1.1rem',
                          background: 'linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%)',
                        }}
                      >
                        {getInitials(appt.doctor_name)}
                      </div>
                      <div>
                        <div className="fw-bold text-dark">{appt.doctor_name}</div>
                        <div className="text-muted small">{appt.doctor_specialization || 'General Physician'}</div>
                      </div>
                    </div>

                    {/* Center: Date & Time */}
                    <div className="d-flex align-items-center gap-4 flex-shrink-0">
                      <div className="d-flex align-items-center gap-2">
                        <i className="bi bi-calendar3 text-primary" />
                        <span className="fw-600 small">{formatDate(appt.appointment_date)}</span>
                      </div>
                      <div className="d-flex align-items-center gap-2">
                        <i className="bi bi-clock text-primary" />
                        <span className="fw-600 small">
                          {formatTime(appt.start_time)} – {formatTime(appt.end_time)}
                        </span>
                      </div>
                    </div>

                    {/* Right: Status Badge + Actions */}
                    <div className="d-flex align-items-center gap-3 flex-shrink-0">
                      <span
                        className="badge rounded-pill px-3 py-2 fw-600 d-flex align-items-center gap-1"
                        style={{ background: badge.bg, color: badge.color, fontSize: '0.8rem' }}
                      >
                        <i className={`bi ${badge.icon}`} />
                        {badge.label}
                      </span>

                      {/* Cancel button for upcoming confirmed */}
                      {activeTab === 'upcoming' && canCancel(appt) && (
                        <button
                          className="btn btn-outline-danger btn-sm rounded-pill px-3 fw-600"
                          onClick={() => openCancelModal(appt.id)}
                        >
                          <i className="bi bi-x-lg me-1" />
                          Cancel
                        </button>
                      )}

                      {/* If confirmed but within 2hr window, show disabled message */}
                      {activeTab === 'upcoming' && appt.status === 'CONFIRMED' && !canCancel(appt) && (
                        <span className="text-muted small" title="Cancellation window has closed (less than 2 hours remaining)">
                          <i className="bi bi-lock-fill me-1" />
                          Locked
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Cancel Modal */}
      {cancelModal.open && (
        <>
          <div className="modal-backdrop fade show" onClick={closeCancelModal} />
          <div className="modal fade show d-block" tabIndex={-1} role="dialog">
            <div className="modal-dialog modal-dialog-centered">
              <div className="modal-content border-0 shadow-lg" style={{ borderRadius: '16px' }}>
                <div className="modal-header border-0 pb-0">
                  <h5 className="modal-title fw-bold d-flex align-items-center gap-2">
                    <i className="bi bi-exclamation-triangle-fill text-warning" />
                    Cancel Appointment
                  </h5>
                  <button type="button" className="btn-close" onClick={closeCancelModal} />
                </div>
                <div className="modal-body pt-3">
                  <p className="text-muted mb-3">
                    Are you sure you want to cancel this appointment? This action cannot be undone.
                  </p>
                  <label className="form-label fw-600 small text-muted">
                    Reason for cancellation (optional)
                  </label>
                  <textarea
                    className="form-control"
                    rows={3}
                    placeholder="e.g., Schedule conflict, personal reasons..."
                    value={cancelReason}
                    onChange={(e) => setCancelReason(e.target.value)}
                  />
                </div>
                <div className="modal-footer border-0 pt-0">
                  <button
                    className="btn btn-outline-secondary rounded-pill px-4"
                    onClick={closeCancelModal}
                    disabled={cancelling}
                  >
                    Keep Appointment
                  </button>
                  <button
                    className="btn btn-danger rounded-pill px-4 fw-600"
                    onClick={handleCancel}
                    disabled={cancelling}
                  >
                    {cancelling ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" />
                        Cancelling...
                      </>
                    ) : (
                      <>
                        <i className="bi bi-x-circle me-1" />
                        Yes, Cancel
                      </>
                    )}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
