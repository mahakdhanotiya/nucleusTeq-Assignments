import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getAppointmentDetail } from '../../api/appointmentApi';
import { getPayment } from '../../api/paymentApi';
import useToast from '../../hooks/useToast';
import Spinner from '../../components/common/Spinner';

export default function PaymentSuccessPage() {
  const { appointmentId } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const toastRef = useRef(toast);

  const [appointment, setAppointment] = useState(null);
  const [payment, setPayment] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;

    const fetchData = async () => {
      try {
        const [apptRes, paymentRes] = await Promise.all([
          getAppointmentDetail(appointmentId),
          getPayment(appointmentId),
        ]);
        if (active) {
          setAppointment(apptRes.data);
          setPayment(paymentRes.data);
        }
      } catch (err) {
        if (active && !err.toasted) {
          toastRef.current.error('Failed to load booking confirmation details.');
        }
      } finally {
        if (active) setLoading(false);
      }
    };

    fetchData();
    return () => { active = false; };
  }, [appointmentId]);

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString('en-IN', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const formatTime = (time24) => {
    if (!time24) return '';
    try {
      const [hourStr, minStr] = time24.split(':');
      const hour = parseInt(hourStr, 10);
      const ampm = hour >= 12 ? 'PM' : 'AM';
      return `${hour % 12 || 12}:${minStr} ${ampm}`;
    } catch {
      return time24;
    }
  };

  const getInitials = (name) => {
    if (!name) return 'Dr';
    return name
      .replace(/^Dr\.\s*/i, '')
      .split(/\s+/)
      .map((p) => p[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  if (loading) {
    return <Spinner label="Loading your booking confirmation..." />;
  }

  return (
    <div className="min-vh-100 bg-light d-flex flex-column">
      {/* Secure Navbar */}
      <header className="bg-white border-bottom py-3 shadow-sm mb-4">
        <div className="container d-flex justify-content-between align-items-center">
          <div className="d-flex align-items-center text-primary fw-bold fs-4">
            <i className="bi bi-heart-pulse-fill me-2" />
            MediBook
          </div>
          <div className="text-muted small d-flex align-items-center gap-2">
            <i className="bi bi-shield-lock-fill text-success fs-5" />
            <span className="fw-600">Secure Checkout</span>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container py-2 flex-grow-1 d-flex justify-content-center">
        <div style={{ maxWidth: '620px', width: '100%' }}>

          {/* Success Header Card */}
          <div
            className="card border-0 text-white text-center p-5 mb-4"
            style={{ background: 'linear-gradient(135deg, #16a34a 0%, #15803d 100%)', borderRadius: '16px' }}
          >
            {/* Animated Checkmark */}
            <div
              className="rounded-circle d-flex align-items-center justify-content-center mx-auto mb-4"
              style={{
                width: '90px',
                height: '90px',
                background: 'rgba(255,255,255,0.2)',
                border: '3px solid rgba(255,255,255,0.4)',
              }}
            >
              <i className="bi bi-check-lg" style={{ fontSize: '3rem' }} />
            </div>
            <h2 className="fw-bold mb-2">Payment Successful!</h2>
            <p className="mb-0 opacity-75">Your appointment has been confirmed and booked.</p>

            {/* Transaction Ref */}
            {payment?.transaction_ref && (
              <div
                className="mt-4 px-4 py-2 rounded-pill d-inline-block mx-auto"
                style={{ background: 'rgba(255,255,255,0.15)', fontSize: '0.85rem' }}
              >
                <i className="bi bi-receipt me-2" />
                Transaction ID: <strong>{payment.transaction_ref}</strong>
              </div>
            )}
          </div>

          {/* Booking Details Card */}
          {appointment && (
            <div className="card border-0 shadow-sm p-4 mb-4">
              <h5 className="fw-bold text-dark mb-4 d-flex align-items-center gap-2">
                <i className="bi bi-clipboard2-check text-primary" />
                Booking Details
              </h5>

              {/* Doctor Info */}
              <div className="d-flex align-items-center gap-3 mb-4">
                <div
                  className="rounded-circle d-flex align-items-center justify-content-center text-white fw-bold flex-shrink-0"
                  style={{
                    width: '55px',
                    height: '55px',
                    fontSize: '1.2rem',
                    background: 'linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%)',
                  }}
                >
                  {getInitials(appointment.doctor_details.full_name)}
                </div>
                <div>
                  <div className="fw-bold text-dark fs-6">{appointment.doctor_details.full_name}</div>
                  <div className="text-muted small">{appointment.doctor_details.specialization || 'General Physician'}</div>
                  {appointment.doctor_details.clinic_address && (
                    <div className="text-muted small">
                      <i className="bi bi-geo-alt me-1" />
                      {appointment.doctor_details.clinic_address}
                    </div>
                  )}
                </div>
              </div>

              <hr className="divider my-3" />

              {/* Appointment Info Grid */}
              <div className="row g-3 mb-3">
                <div className="col-6">
                  <div
                    className="p-3 rounded-3 text-center"
                    style={{ background: '#f0fdf4', border: '1px solid #bbf7d0' }}
                  >
                    <i className="bi bi-calendar3 text-success mb-1 d-block fs-5" />
                    <div className="text-muted small mb-1">Date</div>
                    <div className="fw-bold text-dark small">{formatDate(appointment.appointment_date)}</div>
                  </div>
                </div>
                <div className="col-6">
                  <div
                    className="p-3 rounded-3 text-center"
                    style={{ background: '#eff6ff', border: '1px solid #bfdbfe' }}
                  >
                    <i className="bi bi-clock text-primary mb-1 d-block fs-5" />
                    <div className="text-muted small mb-1">Time</div>
                    <div className="fw-bold text-dark small">
                      {formatTime(appointment.start_time)} – {formatTime(appointment.end_time)}
                    </div>
                  </div>
                </div>
              </div>

              <hr className="divider my-3" />

              {/* Payment Summary */}
              <div className="d-flex justify-content-between py-2">
                <span className="text-muted small">Consultation Fee</span>
                <span className="fw-600">₹{payment?.amount ?? appointment.doctor_details.consultation_fee ?? 0}</span>
              </div>
              <div className="d-flex justify-content-between py-2">
                <span className="text-muted small">Platform Fee</span>
                <span className="fw-600 text-success">₹0 (Free)</span>
              </div>
              <div className="d-flex justify-content-between py-2">
                <span className="text-muted small">Payment Status</span>
                <span className="badge bg-success rounded-pill px-3 py-1.5 fw-600">
                  <i className="bi bi-check-circle me-1" />
                  Paid
                </span>
              </div>

              <hr className="divider my-3" />

              <div className="d-flex justify-content-between align-items-center">
                <span className="fw-bold text-dark">Total Paid</span>
                <span className="fs-5 fw-bold text-success">
                  ₹{payment?.amount ?? appointment.doctor_details.consultation_fee ?? 0}
                </span>
              </div>
            </div>
          )}

          {/* Action Buttons */}
          <div className="d-flex flex-column flex-sm-row gap-3">
            <button
              className="btn btn-primary rounded-pill py-3 fw-bold flex-fill"
              onClick={() => navigate('/my-appointments')}
            >
              <i className="bi bi-calendar2-check me-2" />
              View My Appointments
            </button>
            <button
              className="btn btn-outline-secondary rounded-pill py-3 fw-600 flex-fill"
              onClick={() => navigate('/')}
            >
              <i className="bi bi-search me-2" />
              Find Another Doctor
            </button>
          </div>

          {/* Help Note */}
          <p className="text-center text-muted small mt-4 mb-0">
            <i className="bi bi-envelope me-1" />
            A confirmation has been recorded. Please visit your appointments page for details.
          </p>
        </div>
      </main>

      {/* Secure Footer */}
      <footer className="bg-white border-top py-3 mt-4 text-center text-muted small">
        <div className="container">
          &copy; {new Date().getFullYear()} MediBook. All rights reserved. Secured by mock 256-bit encryption.
        </div>
      </footer>
    </div>
  );
}
