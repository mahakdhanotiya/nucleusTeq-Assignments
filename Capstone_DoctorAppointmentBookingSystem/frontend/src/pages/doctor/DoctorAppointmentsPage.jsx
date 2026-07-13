import React, { useCallback, useEffect, useState } from 'react';

import { getDoctorAppointments, updateAppointmentStatus } from '../../api/appointmentApi';
import useToast from '../../hooks/useToast';
import Spinner from '../../components/common/Spinner';

const STATUS_BADGES = {
  CONFIRMED: { bg: '#dbeafe', color: '#1d4ed8', icon: 'bi-clock-fill', label: 'Confirmed' },
  COMPLETED: { bg: '#dcfce7', color: '#15803d', icon: 'bi-check-circle-fill', label: 'Completed' },
  CANCELLED: { bg: '#fee2e2', color: '#b91c1c', icon: 'bi-x-circle-fill', label: 'Cancelled' },
  ABSENT:    { bg: '#fee2e2', color: '#b91c1c', icon: 'bi-dash-circle-fill', label: 'Absent' },
};

const VIEW_TABS = [
  { key: 'upcoming', label: 'Upcoming', icon: 'bi-calendar-event' },
  { key: 'today',    label: "Today's",  icon: 'bi-calendar-day' },
  { key: 'all',      label: 'All',      icon: 'bi-calendar3' },
];

export default function DoctorAppointmentsPage() {
  const toast = useToast();

  const [activeView, setActiveView] = useState('upcoming');
  const [dateFilter, setDateFilter] = useState('');
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(null); // appointmentId being acted on

  const fetchAppointments = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getDoctorAppointments({
        view: activeView,
        appointment_date: activeView === 'all' && dateFilter ? dateFilter : undefined,
      });
      setAppointments(res.data || []);
    } catch (err) {
      if (!err.toasted) {
        toast.error('Failed to load appointments.');
      }
    } finally {
      setLoading(false);
    }
  }, [activeView, dateFilter]);

  useEffect(() => {
    fetchAppointments();
  }, [fetchAppointments]);

  // Check if appointment time has passed (doctor can only mark after end_time)
  const hasTimePassed = (appt) => {
    try {
      const [h, m] = appt.end_time.split(':').map(Number);
      const apptDate = new Date(appt.appointment_date);
      apptDate.setHours(h, m, 0, 0);
      return new Date() > apptDate;
    } catch {
      return false;
    }
  };

  // Filter appointments locally by date if activeView is 'upcoming' and a date filter is selected
  const displayedAppointments = appointments.filter((appt) => {
    if (activeView === 'upcoming' && dateFilter) {
      return appt.appointment_date === dateFilter;
    }
    return true;
  });

  const handleStatusUpdate = async (appointmentId, status) => {
    setActionLoading(appointmentId);
    try {
      await updateAppointmentStatus(appointmentId, { status });
      toast.success(
        status === 'COMPLETED'
          ? 'Appointment marked as completed!'
          : 'Appointment marked as no-show.'
      );
      fetchAppointments();
    } catch (err) {
      if (!err.toasted) {
        toast.error(
          err.response?.data?.message || 'Failed to update status.'
        );
      }
    } finally {
      setActionLoading(null);
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
      .split(' ')
      .map((p) => p[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  return (
    <div>
      {/* Page Header */}
      <div className="page-header mb-4">
        <h1>Patient Appointments</h1>
        <p>View and manage your scheduled patient appointments.</p>
      </div>

      {/* Tabs & Filters */}
      <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
        <div className="d-flex gap-2">
          {VIEW_TABS.map((tab) => (
            <button
              key={tab.key}
              className={`btn rounded-pill px-4 py-2 fw-600 d-flex align-items-center gap-2 ${
                activeView === tab.key
                  ? 'btn-primary'
                  : 'btn-outline-secondary'
              }`}
              onClick={() => {
                setActiveView(tab.key);
                if (tab.key !== 'all' && tab.key !== 'upcoming') setDateFilter('');
              }}
            >
              <i className={`bi ${tab.icon}`} />
              {tab.label}
            </button>
          ))}
        </div>

        {/* Date filter for "Upcoming" and "All" views */}
        {(activeView === 'all' || activeView === 'upcoming') && (
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
        )}
      </div>

      {/* Loading State */}
      {loading && <Spinner label="Loading appointments..." />}

      {/* Empty State */}
      {!loading && displayedAppointments.length === 0 && (
        <div className="card border-0 shadow-sm p-5 text-center">
          <i className="bi bi-calendar-x text-muted d-block mb-3" style={{ fontSize: '3rem', opacity: 0.4 }} />
          <h5 className="fw-bold text-dark mb-2">No Appointments Found</h5>
          <p className="text-muted mb-0">
            {activeView === 'upcoming'
              ? 'You have no upcoming patient appointments.'
              : activeView === 'today'
                ? 'No appointments scheduled for today.'
                : dateFilter
                  ? `No appointments found for ${formatDate(dateFilter)}.`
                  : 'No appointments to display.'}
          </p>
        </div>
      )}

      {/* Appointment Cards */}
      {!loading && displayedAppointments.length > 0 && (
        <div className="row g-3">
          {displayedAppointments.map((appt) => {
            const badge = STATUS_BADGES[appt.status] || STATUS_BADGES.CONFIRMED;
            const timePassed = hasTimePassed(appt);
            const isConfirmed = appt.status === 'CONFIRMED';
            const isActing = actionLoading === appt.id;

            return (
              <div key={appt.id} className="col-12">
                <div className="card border-0 shadow-sm p-4 hover-lift">
                  <div className="d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-3">
                    {/* Left: Patient Info */}
                    <div className="d-flex align-items-center gap-3 flex-grow-1">
                      <div
                        className="rounded-circle d-flex align-items-center justify-content-center text-white fw-bold flex-shrink-0"
                        style={{
                          width: '52px',
                          height: '52px',
                          fontSize: '1.1rem',
                          background: 'linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%)',
                        }}
                      >
                        {getInitials(appt.patient_name)}
                      </div>
                      <div>
                        <div className="fw-bold text-dark">{appt.patient_name}</div>
                        <div className="text-muted small d-flex align-items-center gap-1">
                          <i className="bi bi-telephone" />
                          {appt.patient_phone}
                        </div>
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

                    {/* Right: Status + Actions */}
                    <div className="d-flex align-items-center gap-2 flex-shrink-0">
                      <span
                        className="badge rounded-pill px-3 py-2 fw-600 d-flex align-items-center gap-1"
                        style={{ background: badge.bg, color: badge.color, fontSize: '0.8rem' }}
                      >
                        <i className={`bi ${badge.icon}`} />
                        {badge.label}
                      </span>

                      {/* Action buttons — only for CONFIRMED & time has passed */}
                      {isConfirmed && timePassed && (
                        <div className="d-flex gap-2">
                          <button
                            className="btn btn-success btn-sm rounded-pill px-3 fw-600"
                            onClick={() => handleStatusUpdate(appt.id, 'COMPLETED')}
                            disabled={isActing}
                          >
                            {isActing ? (
                              <span className="spinner-border spinner-border-sm" />
                            ) : (
                              <>
                                <i className="bi bi-check-lg me-1" />
                                Completed
                              </>
                            )}
                          </button>
                          <button
                            className="btn btn-outline-secondary btn-sm rounded-pill px-3 fw-600"
                            onClick={() => handleStatusUpdate(appt.id, 'ABSENT')}
                            disabled={isActing}
                          >
                            <i className="bi bi-person-x me-1" />
                            Absent
                          </button>
                        </div>
                      )}

                      {/* Upcoming appointment — show waiting indicator */}
                      {isConfirmed && !timePassed && (
                        <span className="text-muted small">
                          <i className="bi bi-hourglass-split me-1" />
                          Awaiting
                        </span>
                      )}
                    </div>
                  </div>
                  {appt.status === 'CANCELLED' && appt.cancellation_reason && (
                    <div className="mt-3 p-3 bg-light rounded-3 text-danger small d-flex align-items-center gap-2 border-start border-danger border-3">
                      <i className="bi bi-exclamation-octagon-fill text-danger" />
                      <div>
                        <strong>Cancellation Reason:</strong> {appt.cancellation_reason}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
