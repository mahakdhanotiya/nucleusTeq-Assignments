import React from 'react';
import { useNavigate } from 'react-router-dom';
import { doctorDetailPath } from '../../routes/routePaths';

export default function DoctorCard({ doctor }) {
  const navigate = useNavigate();

  const {
    user_id,
    full_name,
    specialization,
    qualification,
    experience_years,
    consultation_fee,
    clinic_address,
    available_slot_count,
  } = doctor;

  // Generate initials for avatar placeholder
  const getInitials = (name) => {
    if (!name) return 'Dr';
    const parts = name.replace(/^Dr\.\s*/i, '').split(/\s+/);
    const initials = parts.map((p) => p[0]).join('').toUpperCase();
    return initials.substring(0, 2);
  };

  const handleCardClick = () => {
    navigate(doctorDetailPath(user_id));
  };

  return (
    <div
      className="card card-hover h-100 border-0 shadow-sm"
      onClick={handleCardClick}
    >
      <div className="card-body p-4 d-flex flex-column justify-content-between">
        <div>
          {/* Top section: Avatar and basic info */}
          <div className="d-flex align-items-start gap-3 mb-3">
            <div className="doctor-avatar">
              {getInitials(full_name)}
            </div>
            <div>
              <h5 className="card-title fw-600 mb-1">{full_name}</h5>
              <p className="text-primary fw-600 small mb-1">
                {specialization || 'General Physician'}
              </p>
              <p className="text-muted small mb-0">{qualification || 'MBBS'}</p>
            </div>
          </div>

          <hr className="divider my-3" />

          {/* Details list */}
          <div className="mb-4">
            <div className="d-flex justify-content-between mb-2 small">
              <span className="text-muted">
                <i className="bi bi-briefcase me-2" />
                Experience
              </span>
              <span className="fw-600">{experience_years ?? 0} Years</span>
            </div>

            <div className="d-flex justify-content-between mb-2 small">
              <span className="text-muted">
                <i className="bi bi-currency-rupee me-2" />
                Consultation Fee
              </span>
              <span className="fw-600 text-dark">
                ₹{consultation_fee ?? 500}
              </span>
            </div>

            {clinic_address && (
              <div className="d-flex justify-content-between small">
                <span className="text-muted">
                  <i className="bi bi-geo-alt me-2" />
                  Clinic
                </span>
                <span className="text-end fw-600 text-truncate" style={{ maxWidth: '140px' }} title={clinic_address}>
                  {clinic_address}
                </span>
              </div>
            )}
          </div>
        </div>

        {/* Action and availability footer */}
        <div>
          <div className="d-flex align-items-center justify-content-between mt-auto pt-2">
            <div>
              {available_slot_count > 0 ? (
                <span className="badge rounded-pill bg-success-light text-success fw-600 px-3 py-2 small">
                  <i className="bi bi-calendar-check me-1" />
                  {available_slot_count} slots available
                </span>
              ) : (
                <span className="badge rounded-pill bg-light text-muted fw-600 px-3 py-2 small">
                  <i className="bi bi-calendar-x me-1" />
                  No slots available
                </span>
              )}
            </div>
            <button
              className="btn btn-primary btn-sm px-3 fw-600"
              onClick={(e) => {
                e.stopPropagation();
                handleCardClick();
              }}
            >
              Book Session
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
