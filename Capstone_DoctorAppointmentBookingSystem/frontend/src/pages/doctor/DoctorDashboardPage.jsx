import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { getDoctorAppointments } from '../../api/appointmentApi';
import { getProfile, updateActiveStatus } from '../../api/userApi';
import useAuth from '../../hooks/useAuth';
import useToast from '../../hooks/useToast';
import Spinner from '../../components/common/Spinner';

export default function DoctorDashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();

  const [todayAppts, setTodayAppts] = useState([]);
  const [upcomingAppts, setUpcomingAppts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isActive, setIsActive] = useState(true);
  const [toggling, setToggling] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const [todayRes, upcomingRes, profileRes] = await Promise.all([
          getDoctorAppointments({ view: 'today' }),
          getDoctorAppointments({ view: 'upcoming' }),
          getProfile()
        ]);
        setTodayAppts(todayRes.data || []);
        setUpcomingAppts(upcomingRes.data || []);
        setIsActive(profileRes.data.is_active ?? true);
      } catch (err) {
        if (!err.toasted) {
          toast.error('Failed to load dashboard data.');
        }
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleToggleActive = async () => {
    try {
      setToggling(true);
      const nextVal = !isActive;
      await updateActiveStatus(nextVal);
      setIsActive(nextVal);
      toast.success(`Profile availability status set to ${nextVal ? 'Active' : 'Inactive'}.`);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to update active status.');
    } finally {
      setToggling(false);
    }
  };

  // Greeting based on time of day
  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good Morning';
    if (hour < 17) return 'Good Afternoon';
    return 'Good Evening';
  };

  // Format time helper
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

  // Get initials
  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map((p) => p[0]).join('').toUpperCase().slice(0, 2);
  };

  // Count confirmed today
  const confirmedToday = todayAppts.filter((a) => a.status === 'CONFIRMED').length;
  const completedToday = todayAppts.filter((a) => a.status === 'COMPLETED').length;
  const upcomingCount = upcomingAppts.filter((a) => a.status === 'CONFIRMED').length;

  return (
    <div>
      {/* Welcome Header */}
      <div className="page-header mb-4">
        <h1>{getGreeting()}, Doctor! 👋</h1>
        <p>Here's an overview of your schedule and appointments for today.</p>
      </div>

      {/* Loading */}
      {loading && <Spinner label="Loading your dashboard..." />}

      {!loading && (
        <>
          {/* Summary Stats */}
          <div className="row g-3 mb-4">
            {/* Today's Appointments */}
            <div className="col-12 col-md-4">
              <div
                className="card border-0 shadow-sm p-4 h-100"
                style={{ borderLeft: '4px solid #0d6efd', borderRadius: '12px' }}
              >
                <div className="d-flex align-items-center justify-content-between">
                  <div>
                    <div className="text-muted small fw-600 mb-1">Today's Appointments</div>
                    <div className="fs-2 fw-bold text-dark">{todayAppts.length}</div>
                  </div>
                  <div
                    className="rounded-circle d-flex align-items-center justify-content-center"
                    style={{ width: '56px', height: '56px', background: '#dbeafe' }}
                  >
                    <i className="bi bi-calendar-day text-primary fs-4" />
                  </div>
                </div>
                <div className="text-muted small mt-2">
                  <span className="text-primary fw-600">{confirmedToday}</span> pending ·{' '}
                  <span className="text-success fw-600">{completedToday}</span> completed
                </div>
              </div>
            </div>

            {/* Upcoming */}
            <div className="col-12 col-md-4">
              <div
                className="card border-0 shadow-sm p-4 h-100"
                style={{ borderLeft: '4px solid #8b5cf6', borderRadius: '12px' }}
              >
                <div className="d-flex align-items-center justify-content-between">
                  <div>
                    <div className="text-muted small fw-600 mb-1">Upcoming Appointments</div>
                    <div className="fs-2 fw-bold text-dark">{upcomingCount}</div>
                  </div>
                  <div
                    className="rounded-circle d-flex align-items-center justify-content-center"
                    style={{ width: '56px', height: '56px', background: '#ede9fe' }}
                  >
                    <i className="bi bi-calendar-event fs-4" style={{ color: '#8b5cf6' }} />
                  </div>
                </div>
                <div className="text-muted small mt-2">Confirmed and scheduled ahead</div>
              </div>
            </div>

            {/* Quick Actions */}
            <div className="col-12 col-md-4">
              <div
                className="card border-0 shadow-sm p-4 h-100"
                style={{ borderLeft: '4px solid #16a34a', borderRadius: '12px' }}
              >
                <div className="text-muted small fw-600 mb-3">Quick Actions</div>
                <div className="d-flex flex-column gap-2">
                  <button
                    className="btn btn-primary rounded-pill py-2 fw-600 d-flex align-items-center justify-content-center gap-2"
                    onClick={() => navigate('/doctor/appointments')}
                  >
                    <i className="bi bi-calendar-check" />
                    View Appointments
                  </button>
                  <button
                    className="btn btn-outline-secondary rounded-pill py-2 fw-600 d-flex align-items-center justify-content-center gap-2 mb-2"
                    onClick={() => navigate('/doctor/slots')}
                  >
                    <i className="bi bi-calendar3" />
                    Manage Availability
                  </button>
                  <div className="border-top pt-3 mt-1">
                    <div className="form-check form-switch d-flex align-items-center justify-content-between p-0 m-0">
                      <label className="form-check-label fw-600 text-dark" htmlFor="activeStatusToggle">
                        Profile Availability
                      </label>
                      <input
                        className="form-check-input ms-0 cursor-pointer"
                        type="checkbox"
                        role="switch"
                        id="activeStatusToggle"
                        checked={isActive}
                        disabled={toggling}
                        onChange={handleToggleActive}
                        style={{ width: '2.5em', height: '1.25em' }}
                      />
                    </div>
                    <small className="text-muted d-block mt-1" style={{ fontSize: '0.75rem' }}>
                      {isActive
                        ? 'Your profile is active. Patients can search and book appointments.'
                        : 'Your profile is inactive. Patients cannot book you.'}
                    </small>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Today's Schedule Preview */}
          <div className="card border-0 shadow-sm p-4" style={{ borderRadius: '12px' }}>
            <div className="d-flex align-items-center justify-content-between mb-4">
              <h5 className="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
                <i className="bi bi-calendar-day text-primary" />
                Today's Schedule
              </h5>
              {todayAppts.length > 0 && (
                <button
                  className="btn btn-outline-primary btn-sm rounded-pill px-3 fw-600"
                  onClick={() => navigate('/doctor/appointments')}
                >
                  View All →
                </button>
              )}
            </div>

            {todayAppts.length === 0 ? (
              <div className="text-center py-4">
                <i className="bi bi-calendar-x text-muted d-block mb-3" style={{ fontSize: '2.5rem', opacity: 0.4 }} />
                <h6 className="fw-bold text-dark mb-1">No Appointments Today</h6>
                <p className="text-muted small mb-0">Your schedule is clear for today. Enjoy your day!</p>
              </div>
            ) : (
              <div className="d-flex flex-column gap-3">
                {todayAppts.slice(0, 5).map((appt) => {
                  const statusColors = {
                    CONFIRMED: { bg: '#dbeafe', color: '#1d4ed8', label: 'Confirmed' },
                    COMPLETED: { bg: '#dcfce7', color: '#15803d', label: 'Completed' },
                    CANCELLED: { bg: '#fee2e2', color: '#b91c1c', label: 'Cancelled' },
                    ABSENT:    { bg: '#fee2e2', color: '#b91c1c', label: 'Absent' },
                  };
                  const badge = statusColors[appt.status] || statusColors.CONFIRMED;

                  return (
                    <div
                      key={appt.id}
                      className="d-flex align-items-center justify-content-between p-3 rounded-3"
                      style={{ background: '#f8f9fa' }}
                    >
                      <div className="d-flex align-items-center gap-3">
                        <div
                          className="rounded-circle d-flex align-items-center justify-content-center text-white fw-bold flex-shrink-0"
                          style={{
                            width: '44px',
                            height: '44px',
                            fontSize: '0.9rem',
                            background: 'linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%)',
                          }}
                        >
                          {getInitials(appt.patient_name)}
                        </div>
                        <div>
                          <div className="fw-bold text-dark small">{appt.patient_name}</div>
                          <div className="text-muted" style={{ fontSize: '0.75rem' }}>
                            <i className="bi bi-telephone me-1" />
                            {appt.patient_phone}
                          </div>
                        </div>
                      </div>

                      <div className="d-flex align-items-center gap-3">
                        <div className="text-end">
                          <div className="fw-600 small text-dark">
                            {formatTime(appt.start_time)} – {formatTime(appt.end_time)}
                          </div>
                        </div>
                        <span
                          className="badge rounded-pill px-3 py-2 fw-600"
                          style={{ background: badge.bg, color: badge.color, fontSize: '0.75rem' }}
                        >
                          {badge.label}
                        </span>
                      </div>
                    </div>
                  );
                })}

                {todayAppts.length > 5 && (
                  <div className="text-center">
                    <button
                      className="btn btn-link text-primary small fw-600 text-decoration-none"
                      onClick={() => navigate('/doctor/appointments')}
                    >
                      + {todayAppts.length - 5} more appointments →
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
