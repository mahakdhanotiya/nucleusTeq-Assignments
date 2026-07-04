import React, { useEffect, useState } from 'react';
import { getUserDashboardStats, getAppointmentDashboardStats } from '../../api/adminApi';
import useToast from '../../hooks/useToast';
import { Link } from 'react-router-dom';

export default function AdminDashboardPage() {
  const toast = useToast();
  const [loading, setLoading] = useState(true);
  const [userStats, setUserStats] = useState(null);
  const [apptStats, setApptStats] = useState(null);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const [usersRes, apptsRes] = await Promise.all([
        getUserDashboardStats(),
        getAppointmentDashboardStats()
      ]);
      setUserStats(usersRes.data);
      setApptStats(apptsRes.data);
    } catch (err) {
      toast.error('Failed to fetch dashboard statistics.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '350px' }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  // Calculate percentages for appointment statuses
  const totalAppts = apptStats?.total_appointments || 0;
  const getPct = (val) => (totalAppts > 0 ? Math.round((val / totalAppts) * 100) : 0);

  const statuses = [
    { 
      label: 'Confirmed', 
      count: apptStats?.confirmed_appointments || 0, 
      color: 'bg-primary', 
      textClass: 'text-primary',
      icon: 'bi-calendar-check-fill'
    },
    { 
      label: 'Completed', 
      count: apptStats?.completed_appointments || 0, 
      color: 'bg-success', 
      textClass: 'text-success',
      icon: 'bi-check-circle-fill'
    },
    { 
      label: 'Cancelled', 
      count: apptStats?.cancelled_appointments || 0, 
      color: 'bg-danger', 
      textClass: 'text-danger',
      icon: 'bi-x-circle-fill'
    },
    { 
      label: 'No-Show', 
      count: apptStats?.no_show_appointments || 0, 
      color: 'bg-warning', 
      textClass: 'text-warning',
      icon: 'bi-exclamation-triangle-fill'
    }
  ];

  return (
    <div className="container-fluid py-2">
      {/* Page Header */}
      <div className="page-header mb-4 d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3">
        <div>
          <h1>Administrator Dashboard</h1>
          <p className="text-muted">Overview of patient registrations, medical practitioners, and overall booking metrics.</p>
        </div>
        <button className="btn btn-outline-primary shadow-sm" onClick={fetchStats}>
          <i className="bi bi-arrow-clockwise me-1"></i> Refresh Stats
        </button>
      </div>

      {/* Stats Cards Row */}
      <div className="row g-4 mb-4">
        {/* Total Patients */}
        <div className="col-md-6 col-lg-3">
          <div className="card border-0 shadow-sm p-3 h-100 position-relative overflow-hidden">
            <div className="d-flex align-items-center gap-3">
              <div className="p-3 rounded bg-primary-light text-primary fs-3">
                <i className="bi bi-people-fill"></i>
              </div>
              <div>
                <span className="text-muted small fw-600 uppercase">Total Patients</span>
                <h2 className="mb-0 fw-700 mt-1">{userStats?.total_patients || 0}</h2>
              </div>
            </div>
            <div className="position-absolute" style={{ right: '-10px', bottom: '-20px', fontSize: '6rem', opacity: 0.05 }}>
              <i className="bi bi-people-fill"></i>
            </div>
          </div>
        </div>

        {/* Total Doctors */}
        <div className="col-md-6 col-lg-3">
          <div className="card border-0 shadow-sm p-3 h-100 position-relative overflow-hidden">
            <div className="d-flex align-items-center gap-3">
              <div className="p-3 rounded bg-success-light text-success fs-3">
                <i className="bi bi-heart-pulse-fill"></i>
              </div>
              <div>
                <span className="text-muted small fw-600 uppercase">Total Doctors</span>
                <h2 className="mb-0 fw-700 mt-1">{userStats?.total_doctors || 0}</h2>
              </div>
            </div>
            <div className="position-absolute" style={{ right: '-10px', bottom: '-20px', fontSize: '6rem', opacity: 0.05 }}>
              <i className="bi bi-heart-pulse-fill"></i>
            </div>
          </div>
        </div>

        {/* Active Doctors */}
        <div className="col-md-6 col-lg-3">
          <div className="card border-0 shadow-sm p-3 h-100 position-relative overflow-hidden">
            <div className="d-flex align-items-center gap-3">
              <div className="p-3 rounded bg-info-light text-info fs-3" style={{ backgroundColor: '#e0f2fe', color: '#0284c7' }}>
                <i className="bi bi-person-check-fill"></i>
              </div>
              <div>
                <span className="text-muted small fw-600 uppercase">Active Doctors</span>
                <h2 className="mb-0 fw-700 mt-1">{userStats?.active_doctors || 0}</h2>
              </div>
            </div>
            <div className="position-absolute" style={{ right: '-10px', bottom: '-20px', fontSize: '6rem', opacity: 0.05 }}>
              <i className="bi bi-person-check-fill"></i>
            </div>
          </div>
        </div>

        {/* Total Bookings */}
        <div className="col-md-6 col-lg-3">
          <div className="card border-0 shadow-sm p-3 h-100 position-relative overflow-hidden">
            <div className="d-flex align-items-center gap-3">
              <div className="p-3 rounded bg-warning-light text-warning fs-3" style={{ backgroundColor: '#fef3c7', color: '#d97706' }}>
                <i className="bi bi-journal-bookmark-fill"></i>
              </div>
              <div>
                <span className="text-muted small fw-600 uppercase">Total Appointments</span>
                <h2 className="mb-0 fw-700 mt-1">{totalAppts}</h2>
              </div>
            </div>
            <div className="position-absolute" style={{ right: '-10px', bottom: '-20px', fontSize: '6rem', opacity: 0.05 }}>
              <i className="bi bi-journal-bookmark-fill"></i>
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4">
        {/* Left Column: Appointments Breakdown */}
        <div className="col-lg-7">
          <div className="card border-0 shadow-sm p-4 h-100">
            <h4 className="fw-600 mb-4 border-bottom pb-2">
              <i className="bi bi-pie-chart-fill me-2 text-primary"></i>Appointment Metrics
            </h4>

            {totalAppts === 0 ? (
              <div className="text-center py-5">
                <i className="bi bi-calendar2-x fs-1 text-muted mb-3 d-block"></i>
                <p className="text-muted">No appointments booked in the system yet.</p>
              </div>
            ) : (
              <div className="d-flex flex-column gap-4">
                {/* Horizontal combined progress bar */}
                <div>
                  <label className="form-label fw-600 mb-2 small text-muted">Distribution Profile</label>
                  <div className="progress" style={{ height: '24px', borderRadius: '12px' }}>
                    {statuses.map((status) => {
                      const pct = getPct(status.count);
                      return pct > 0 ? (
                        <div
                          key={status.label}
                          className={`progress-bar ${status.color}`}
                          role="progressbar"
                          style={{ width: `${pct}%` }}
                          title={`${status.label}: ${status.count} (${pct}%)`}
                        >
                          {pct > 5 ? `${pct}%` : ''}
                        </div>
                      ) : null;
                    })}
                  </div>
                </div>

                {/* Individual status listings with indicators */}
                <div className="row g-3">
                  {statuses.map((status) => {
                    const pct = getPct(status.count);
                    return (
                      <div className="col-sm-6" key={status.label}>
                        <div className="p-3 bg-light rounded border d-flex justify-content-between align-items-center">
                          <div className="d-flex align-items-center gap-2">
                            <span className={status.textClass}><i className={`bi ${status.icon}`}></i></span>
                            <span className="fw-600">{status.label}</span>
                          </div>
                          <div className="text-end">
                            <h5 className="mb-0 fw-700">{status.count}</h5>
                            <span className="text-muted small">{pct}% of total</span>
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Right Column: Quick actions / Alert Panels */}
        <div className="col-lg-5">
          <div className="card border-0 shadow-sm p-4 h-100 d-flex flex-column justify-content-between">
            <div>
              <h4 className="fw-600 mb-3 border-bottom pb-2">
                <i className="bi bi-shield-lock-fill me-2 text-primary"></i>System Supervision
              </h4>
              <p className="text-muted small">
                As an administrator, you have permission to supervise all practitioner registrations and regulate service access rules.
              </p>

              <div className="alert bg-primary-light text-primary border-0 rounded p-3 mb-4">
                <div className="d-flex gap-2">
                  <i className="bi bi-info-circle-fill fs-5"></i>
                  <div>
                    <h6 className="fw-600 mb-1">Doctor Verification Needed?</h6>
                    <p className="mb-0 small" style={{ opacity: 0.9 }}>
                      Doctors registering in the system remain in a <strong>PENDING</strong> status. You must verify and approve them before they can offer consultation slots to patients.
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div>
              <Link to="/admin/doctors" className="btn btn-primary w-100 py-2.5 rounded-pill shadow-sm fw-600">
                <i className="bi bi-people-fill me-2"></i>Go to Doctor Management
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
