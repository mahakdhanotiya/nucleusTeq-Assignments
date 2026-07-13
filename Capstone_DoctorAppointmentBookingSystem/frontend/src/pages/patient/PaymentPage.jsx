import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getAppointmentDetail } from '../../api/appointmentApi';
import { processPayment } from '../../api/paymentApi';
import useToast from '../../hooks/useToast';
import { paymentSuccessPath } from '../../routes/routePaths';
import Spinner from '../../components/common/Spinner';

// Mock card input validation
const CARD_PATTERNS = {
  number: /^\d{16}$/,
  expiry: /^(0[1-9]|1[0-2])\/\d{2}$/,
  cvv:    /^\d{3,4}$/,
  name:   /^[a-zA-Z\s]{2,}$/,
};

const formatCardNumber = (value) =>
  value.replace(/\D/g, '').slice(0, 16).replace(/(.{4})/g, '$1 ').trim();

const formatExpiry = (value) => {
  const digits = value.replace(/\D/g, '').slice(0, 4);
  if (digits.length >= 3) return `${digits.slice(0, 2)}/${digits.slice(2)}`;
  return digits;
};

export default function PaymentPage() {
  const { appointmentId } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const toastRef = useRef(toast);

  const [appointment, setAppointment] = useState(null);
  const [pageLoading, setPageLoading] = useState(true);
  const [paymentLoading, setPaymentLoading] = useState(false);
  const [alreadyPaid, setAlreadyPaid] = useState(false);

  // Mock card form state
  const [cardNumber, setCardNumber] = useState('');
  const [cardName, setCardName] = useState('');
  const [expiry, setExpiry] = useState('');
  const [cvv, setCvv] = useState('');
  const [errors, setErrors] = useState({});

  // Fetch appointment details and check payment status
  useEffect(() => {
    let active = true;

    const fetchData = async () => {
      try {
        const apptRes = await getAppointmentDetail(appointmentId);
        if (!active) return;

        setAppointment(apptRes.data);

        // If payment is already marked SUCCESS, show success state
        if (apptRes.data?.payment?.status === 'SUCCESS') {
          setAlreadyPaid(true);
        }
      } catch (err) {
        if (active && !err.toasted) {
          toastRef.current.error(
            err.response?.data?.message || 'Failed to load appointment details.'
          );
        }
      } finally {
        if (active) setPageLoading(false);
      }
    };

    fetchData();
    return () => { active = false; };
  }, [appointmentId]);

  // Validate all form fields
  const validate = () => {
    const newErrors = {};
    const rawNumber = cardNumber.replace(/\s/g, '');

    // Card Number
    if (!rawNumber) {
      newErrors.cardNumber = 'Card number is required.';
    } else if (!CARD_PATTERNS.number.test(rawNumber)) {
      newErrors.cardNumber = 'Enter a valid 16-digit card number.';
    }

    // Cardholder Name
    if (!cardName.trim()) {
      newErrors.cardName = 'Cardholder name is required.';
    } else if (!CARD_PATTERNS.name.test(cardName.trim())) {
      newErrors.cardName = 'Name must contain only letters (min 2 characters).';
    }

    // Expiry Date
    if (!expiry) {
      newErrors.expiry = 'Expiry date is required.';
    } else if (!CARD_PATTERNS.expiry.test(expiry)) {
      newErrors.expiry = 'Enter a valid expiry in MM/YY format (e.g. 08/27).';
    } else {
      // Validate the expiry date is not in the past
      const [month, year] = expiry.split('/').map(Number);
      const now = new Date();
      const expiryDate = new Date(2000 + year, month - 1, 1);
      if (expiryDate < new Date(now.getFullYear(), now.getMonth(), 1)) {
        newErrors.expiry = 'This card has expired. Please use a valid card.';
      }
    }

    // CVV
    if (!cvv) {
      newErrors.cvv = 'CVV is required.';
    } else if (!CARD_PATTERNS.cvv.test(cvv)) {
      newErrors.cvv = 'Enter a valid 3 or 4 digit CVV.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // Validate fields in real-time as user leaves them (matching auth page's onBlur mode)
  const handleBlur = (field) => {
    const newErrors = { ...errors };

    if (field === 'cardNumber') {
      const rawNumber = cardNumber.replace(/\s/g, '');
      if (!rawNumber) {
        newErrors.cardNumber = 'Card number is required.';
      } else if (!CARD_PATTERNS.number.test(rawNumber)) {
        newErrors.cardNumber = 'Enter a valid 16-digit card number.';
      } else {
        delete newErrors.cardNumber;
      }
    }

    if (field === 'cardName') {
      if (!cardName.trim()) {
        newErrors.cardName = 'Cardholder name is required.';
      } else if (!CARD_PATTERNS.name.test(cardName.trim())) {
        newErrors.cardName = 'Name must contain only letters (min 2 characters).';
      } else {
        delete newErrors.cardName;
      }
    }

    if (field === 'expiry') {
      if (!expiry) {
        newErrors.expiry = 'Expiry date is required.';
      } else if (!CARD_PATTERNS.expiry.test(expiry)) {
        newErrors.expiry = 'Enter a valid expiry in MM/YY format (e.g. 08/27).';
      } else {
        const [month, year] = expiry.split('/').map(Number);
        const now = new Date();
        const expiryDate = new Date(2000 + year, month - 1, 1);
        if (expiryDate < new Date(now.getFullYear(), now.getMonth(), 1)) {
          newErrors.expiry = 'This card has expired. Please use a valid card.';
        } else {
          delete newErrors.expiry;
        }
      }
    }

    if (field === 'cvv') {
      if (!cvv) {
        newErrors.cvv = 'CVV is required.';
      } else if (!CARD_PATTERNS.cvv.test(cvv)) {
        newErrors.cvv = 'Enter a valid 3 or 4 digit CVV.';
      } else {
        delete newErrors.cvv;
      }
    }

    setErrors(newErrors);
  };

  const handlePayment = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setPaymentLoading(true);
    try {
      await processPayment(appointmentId, {});
      toastRef.current.success('Payment successful! Your appointment is confirmed.');
      navigate(paymentSuccessPath(appointmentId));
    } catch (err) {
      if (!err.toasted) {
        toastRef.current.error(
          err.response?.data?.message || 'Payment failed. Please try again.'
        );
      }
    } finally {
      setPaymentLoading(false);
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-IN', {
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
      const formattedHour = hour % 12 || 12;
      return `${formattedHour}:${minStr} ${ampm}`;
    } catch {
      return time24;
    }
  };

  // Loading skeleton
  if (pageLoading) {
    return <Spinner label="Loading payment checkout details..." />;
  }

  // Already paid — show success
  if (alreadyPaid) {
    return (
      <div className="d-flex justify-content-center py-5">
        <div className="card border-0 shadow-sm p-5 text-center" style={{ maxWidth: '480px', width: '100%' }}>
          <div className="mb-4">
            <div
              className="rounded-circle d-flex align-items-center justify-content-center mx-auto"
              style={{ width: '80px', height: '80px', background: 'linear-gradient(135deg, #16a34a, #22c55e)' }}
            >
              <i className="bi bi-check-lg text-white" style={{ fontSize: '2.5rem' }} />
            </div>
          </div>
          <h4 className="fw-bold text-dark mb-2">Payment Already Completed</h4>
          <p className="text-muted mb-4">This appointment has already been paid for successfully.</p>
          <button className="btn btn-primary rounded-pill px-4 fw-600" onClick={() => navigate('/my-appointments')}>
            View My Appointments
          </button>
        </div>
      </div>
    );
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
      <main className="container py-4 flex-grow-1 d-flex align-items-center">
        <div className="row g-4 justify-content-center w-100">
          {/* Left: Card Payment Form */}
          <div className="col-12 col-lg-7">
            <div className="card border-0 shadow-sm p-4">
              <h5 className="fw-bold text-dark mb-4 d-flex align-items-center gap-2">
                <i className="bi bi-credit-card-2-front text-primary" />
                Card Details
              </h5>

              {/* Mock Payment Notice */}
              <div className="alert border-0 mb-4 d-flex align-items-start gap-2"
                style={{ background: '#fffbeb', borderLeft: '4px solid #f59e0b !important', borderRadius: '8px' }}>
                <i className="bi bi-info-circle-fill text-warning mt-1" />
                <div>
                  <div className="fw-600 small text-warning-emphasis">Mock Payment Environment</div>
                  <div className="text-muted small">This is a simulated payment. No real transaction will occur. Enter any 16-digit card number to proceed.</div>
                </div>
              </div>

              <form onSubmit={handlePayment} noValidate>
                {/* Card Number */}
                <div className="mb-3">
                  <label htmlFor="card-number" className="form-label fw-600 small text-muted">
                    Card Number
                  </label>
                  <div className="input-group">
                    <span className="input-group-text bg-white border-end-0">
                      <i className="bi bi-credit-card text-muted" />
                    </span>
                    <input
                      id="card-number"
                      type="text"
                      inputMode="numeric"
                      className={`form-control border-start-0 ps-0 ${errors.cardNumber ? 'is-invalid' : ''}`}
                      placeholder="1234 5678 9012 3456"
                      value={cardNumber}
                      onChange={(e) => setCardNumber(formatCardNumber(e.target.value))}
                      onBlur={() => handleBlur('cardNumber')}
                      maxLength={19}
                    />
                    {errors.cardNumber && (
                      <div className="invalid-feedback">{errors.cardNumber}</div>
                    )}
                  </div>
                </div>

                {/* Cardholder Name */}
                <div className="mb-3">
                  <label htmlFor="card-name" className="form-label fw-600 small text-muted">
                    Cardholder Name
                  </label>
                  <div className="input-group">
                    <span className="input-group-text bg-white border-end-0">
                      <i className="bi bi-person text-muted" />
                    </span>
                    <input
                      id="card-name"
                      type="text"
                      className={`form-control border-start-0 ps-0 ${errors.cardName ? 'is-invalid' : ''}`}
                      placeholder="Name as on card"
                      value={cardName}
                      onChange={(e) => setCardName(e.target.value)}
                      onBlur={() => handleBlur('cardName')}
                    />
                    {errors.cardName && (
                      <div className="invalid-feedback">{errors.cardName}</div>
                    )}
                  </div>
                </div>

                {/* Expiry and CVV */}
                <div className="row g-3 mb-4">
                  <div className="col-6">
                    <label htmlFor="expiry" className="form-label fw-600 small text-muted">
                      Expiry Date
                    </label>
                    <input
                      id="expiry"
                      type="text"
                      inputMode="numeric"
                      className={`form-control ${errors.expiry ? 'is-invalid' : ''}`}
                      placeholder="MM/YY"
                      value={expiry}
                      onChange={(e) => setExpiry(formatExpiry(e.target.value))}
                      onBlur={() => handleBlur('expiry')}
                      maxLength={5}
                    />
                    {errors.expiry && (
                      <div className="invalid-feedback">{errors.expiry}</div>
                    )}
                  </div>
                  <div className="col-6">
                    <label htmlFor="cvv" className="form-label fw-600 small text-muted">
                      CVV
                    </label>
                    <div className="input-group">
                      <input
                        id="cvv"
                        type="password"
                        inputMode="numeric"
                        className={`form-control ${errors.cvv ? 'is-invalid' : ''}`}
                        placeholder="•••"
                        value={cvv}
                        onChange={(e) => setCvv(e.target.value.replace(/\D/g, '').slice(0, 4))}
                        onBlur={() => handleBlur('cvv')}
                        maxLength={4}
                      />
                      <span className="input-group-text bg-white">
                        <i className="bi bi-lock text-muted small" />
                      </span>
                      {errors.cvv && (
                        <div className="invalid-feedback">{errors.cvv}</div>
                      )}
                    </div>
                  </div>
                </div>

                {/* Pay Button */}
                <button
                  type="submit"
                  disabled={paymentLoading}
                  className="btn btn-primary w-100 py-3 rounded-pill fw-bold text-uppercase"
                  style={{ letterSpacing: '0.05em' }}
                >
                  {paymentLoading ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true" />
                      Processing Payment...
                    </>
                  ) : (
                    <>
                      <i className="bi bi-lock-fill me-2" />
                      Pay ₹{appointment?.payment?.amount ?? appointment?.doctor_details?.consultation_fee ?? 0}
                    </>
                  )}
                </button>

                <p className="text-center text-muted small mt-3 mb-0">
                  <i className="bi bi-shield-lock me-1" />
                  Your payment is secured with 256-bit SSL encryption.
                </p>
              </form>
            </div>
          </div>

          {/* Right: Booking Summary */}
          <div className="col-12 col-lg-5">
            <div className="card border-0 shadow-sm p-4">
              <h5 className="fw-bold text-dark mb-4 d-flex align-items-center gap-2">
                <i className="bi bi-clipboard2-check text-primary" />
                Booking Summary
              </h5>

              {appointment && (
                <>
                  {/* Doctor Info */}
                  <div className="d-flex align-items-center gap-3 mb-4">
                    <div
                      className="rounded-circle d-flex align-items-center justify-content-center text-white fw-bold flex-shrink-0"
                      style={{
                        width: '50px',
                        height: '50px',
                        fontSize: '1.1rem',
                        background: 'linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%)',
                      }}
                    >
                      {appointment.doctor_details.full_name
                        .replace(/^Dr\.\s*/i, '')
                        .split(' ')
                        .map((p) => p[0])
                        .join('')
                        .toUpperCase()
                        .slice(0, 2)}
                    </div>
                    <div>
                      <div className="fw-bold text-dark">{appointment.doctor_details.full_name}</div>
                      <div className="text-muted small">{appointment.doctor_details.specialization || 'General Physician'}</div>
                    </div>
                  </div>

                  <hr className="divider my-3" />

                  {/* Appointment Details */}
                  <div className="mb-1">
                    <div className="d-flex justify-content-between align-items-center py-2">
                      <span className="text-muted small d-flex align-items-center gap-2">
                        <i className="bi bi-calendar3 text-primary" />
                        Date
                      </span>
                      <span className="fw-600 small text-end" style={{ maxWidth: '200px' }}>
                        {formatDate(appointment.appointment_date)}
                      </span>
                    </div>
                    <div className="d-flex justify-content-between align-items-center py-2">
                      <span className="text-muted small d-flex align-items-center gap-2">
                        <i className="bi bi-clock text-primary" />
                        Time
                      </span>
                      <span className="fw-600 small">
                        {formatTime(appointment.start_time)} – {formatTime(appointment.end_time)}
                      </span>
                    </div>
                    {appointment.doctor_details.clinic_address && (
                      <div className="d-flex justify-content-between align-items-center py-2">
                        <span className="text-muted small d-flex align-items-center gap-2">
                          <i className="bi bi-geo-alt text-primary" />
                          Clinic
                        </span>
                        <span className="fw-600 small text-end" style={{ maxWidth: '200px' }}>
                          {appointment.doctor_details.clinic_address}
                        </span>
                      </div>
                    )}
                  </div>

                  <hr className="divider my-3" />

                  {/* Fee Breakdown */}
                  <div className="d-flex justify-content-between align-items-center py-2">
                    <span className="text-muted small">Consultation Fee</span>
                    <span className="fw-600">₹{appointment.payment?.amount ?? appointment.doctor_details.consultation_fee ?? 0}</span>
                  </div>
                  <div className="d-flex justify-content-between align-items-center py-2">
                    <span className="text-muted small">Platform Fee</span>
                    <span className="fw-600 text-success">₹0</span>
                  </div>

                  <hr className="divider my-3" />

                  <div className="d-flex justify-content-between align-items-center">
                    <span className="fw-bold text-dark">Total Amount</span>
                    <span className="fs-5 fw-bold text-primary">
                      ₹{appointment.payment?.amount ?? appointment.doctor_details.consultation_fee ?? 0}
                    </span>
                  </div>
                </>
              )}
            </div>

            {/* Cancel Booking Link */}
            <div className="text-center mt-3">
              <button
                className="btn btn-link text-muted small text-decoration-none"
                onClick={() => navigate(-1)}
              >
                <i className="bi bi-arrow-left me-1" />
                Go back and change the slot
              </button>
            </div>
          </div>
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
