import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getDoctorDetail } from '../../api/doctorApi';
import { bookAppointment } from '../../api/appointmentApi';
import useToast from '../../hooks/useToast';

export default function DoctorDetailPage() {
  const { userId } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const toastRef = useRef(toast);

  const [doctor, setDoctor] = useState(null);
  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [bookingLoading, setBookingLoading] = useState(false);

  // Helper to format date as YYYY-MM-DD
  const formatDate = (dateObj) => {
    const year = dateObj.getFullYear();
    const month = String(dateObj.getMonth() + 1).padStart(2, '0');
    const day = String(dateObj.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  // State for selected date (defaults to today)
  const todayStr = formatDate(new Date());
  const [selectedDate, setSelectedDate] = useState(todayStr);
  const [selectedSlot, setSelectedSlot] = useState(null);

  // Fetch doctor details and slots whenever the selected date or doctor ID changes
  useEffect(() => {
    let active = true;
    setLoading(true);

    const fetchDetail = async () => {
      try {
        const res = await getDoctorDetail(userId, { slot_date: selectedDate });
        if (active) {
          setDoctor(res.data);
          setSlots(res.data?.available_slots || []);
          // Clear selected slot if it doesn't belong to the new date/list
          setSelectedSlot(null);
        }
      } catch (err) {
        if (active && !err.toasted) {
          toastRef.current.error(
            err.response?.data?.message || 'Failed to fetch doctor details. Please try again.'
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    fetchDetail();

    return () => {
      active = false;
    };
  }, [userId, selectedDate]);

  // Generate next 7 days starting from today for quick selection
  const getNext7Days = () => {
    const daysList = [];
    const today = new Date();
    for (let i = 0; i < 7; i++) {
      const nextDay = new Date();
      nextDay.setDate(today.getDate() + i);
      daysList.push(nextDay);
    }
    return daysList;
  };

  const next7Days = getNext7Days();

  // Handle booking creation
  const handleBooking = async () => {
    if (!selectedSlot) return;

    setBookingLoading(true);
    try {
      const payload = {
        doctor_id: userId,
        slot_id: selectedSlot.id,
        appointment_date: selectedDate,
      };
      const res = await bookAppointment(payload);
      toastRef.current.success('Appointment booked successfully! Redirecting to payment...');
      
      // Navigate to payment page with appointment ID
      if (res.data?.id) {
        navigate(`/payment/${res.data.id}`);
      } else {
        navigate('/my-appointments');
      }
    } catch (err) {
      if (!err.toasted) {
        toastRef.current.error(
          err.response?.data?.message || 'Failed to book appointment. The slot might have been taken.'
        );
      }
      // Re-fetch slots to update availability in case of conflict
      const res = await getDoctorDetail(userId, { slot_date: selectedDate });
      setSlots(res.data?.available_slots || []);
      setSelectedSlot(null);
    } finally {
      setBookingLoading(false);
    }
  };

  // Helper for initials avatar
  const getInitials = (name) => {
    if (!name) return 'Dr';
    const parts = name.replace(/^Dr\.\s*/i, '').split(/\s+/);
    const initials = parts.map((p) => p[0]).join('').toUpperCase();
    return initials.substring(0, 2);
  };

  // Format slot times for nice display
  const formatTime = (time24) => {
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

  return (
    <div className="container py-4">
      {/* Back Button */}
      <button
        onClick={() => navigate(-1)}
        className="btn btn-outline-secondary rounded-pill px-3 mb-4 transition-all"
      >
        <i className="bi bi-arrow-left me-2" />
        Back to Directory
      </button>

      {/* Main content grid */}
      {doctor && (
        <div className="row g-4">
          {/* Left Column: Doctor Profile Card */}
          <div className="col-12 col-lg-5">
            <div className="card border-0 shadow-sm p-4 text-center h-100">
              <div
                className="doctor-avatar mx-auto mb-3 d-flex align-items-center justify-content-center text-white font-weight-bold shadow-sm"
                style={{
                  width: '100px',
                  height: '100px',
                  borderRadius: '50%',
                  fontSize: '2.2rem',
                  background: 'linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%)',
                }}
              >
                {getInitials(doctor.full_name)}
              </div>
              <h3 className="fw-bold mb-2 text-dark">{doctor.full_name}</h3>
              <div className="d-flex justify-content-center mb-3">
                <span className="badge rounded-pill bg-primary bg-opacity-10 text-primary px-3 py-2 fw-600 border border-primary border-opacity-10">
                  {doctor.specialization || 'General Physician'}
                </span>
              </div>

              <hr className="divider my-4" />

              <div className="text-start">
                <h6 className="fw-bold mb-3 text-muted text-uppercase tracking-wider" style={{ fontSize: '0.75rem' }}>Doctor Information</h6>
                <div className="d-flex align-items-center mb-3">
                  <div className="bg-primary bg-opacity-10 p-2.5 rounded-3 me-3 text-primary">
                    <i className="bi bi-award-fill fs-5" />
                  </div>
                  <div>
                    <div className="text-muted small">Qualification</div>
                    <div className="fw-600 text-dark">{doctor.qualification || 'MBBS'}</div>
                  </div>
                </div>

                <div className="d-flex align-items-center mb-3">
                  <div className="bg-primary bg-opacity-10 p-2.5 rounded-3 me-3 text-primary">
                    <i className="bi bi-briefcase-fill fs-5" />
                  </div>
                  <div>
                    <div className="text-muted small">Experience</div>
                    <div className="fw-600 text-dark">{doctor.experience_years ?? 0} Years Experience</div>
                  </div>
                </div>

                <div className="d-flex align-items-center mb-3">
                  <div className="bg-primary bg-opacity-10 p-2.5 rounded-3 me-3 text-primary">
                    <i className="bi bi-currency-rupee fs-5" />
                  </div>
                  <div>
                    <div className="text-muted small">Consultation Fee</div>
                    <div className="fw-600 text-dark">₹{doctor.consultation_fee ?? 500}</div>
                  </div>
                </div>

                {doctor.clinic_address && (
                  <div className="d-flex align-items-start mb-3">
                    <div className="bg-primary bg-opacity-10 p-2.5 rounded-3 me-3 text-primary">
                      <i className="bi bi-geo-alt-fill fs-5" />
                    </div>
                    <div>
                      <div className="text-muted small">Clinic Address</div>
                      <div className="fw-600 text-dark text-wrap" style={{ maxWidth: '280px' }}>
                        {doctor.clinic_address}
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Right Column: Slot Selection & Scheduler */}
          <div className="col-12 col-lg-7">
            <div className="card border-0 shadow-sm p-4 h-100 d-flex flex-column justify-content-between">
              <div>
                <h4 className="fw-bold text-dark mb-4">Select Date & Time</h4>

                {/* Horizontal Date Picker Cards */}
                <div className="d-flex gap-2 overflow-auto pb-3 mb-4 scrollbar-hidden">
                  {next7Days.map((day) => {
                    const dayStr = formatDate(day);
                    const isSelected = dayStr === selectedDate;
                    const isToday = dayStr === todayStr;
                    const dayNum = day.getDate();
                    const dayName = day.toLocaleDateString('en-US', { weekday: 'short' });

                    return (
                      <button
                        key={dayStr}
                        type="button"
                        onClick={() => setSelectedDate(dayStr)}
                        className={`btn p-3 d-flex flex-column align-items-center justify-content-center rounded-3 transition-all ${
                          isSelected
                            ? 'btn-primary shadow-sm scale-up'
                            : 'btn-light text-dark border border-light-subtle hover-bg-light'
                        }`}
                        style={{ minWidth: '75px', flexShrink: 0, transition: 'all 0.2s ease-in-out' }}
                      >
                        <span className="small opacity-75">{dayName}</span>
                        <span className="fs-4 fw-bold my-1">{dayNum}</span>
                        {isToday ? (
                          <span className="badge bg-white text-primary rounded-pill uppercase fw-bold" style={{ fontSize: '0.6rem' }}>Today</span>
                        ) : (
                          <span style={{ height: '14.4px' }}></span>
                        )}
                      </button>
                    );
                  })}
                </div>

                {/* Custom Date Selection Picker */}
                <div className="mb-4 d-flex align-items-center gap-3">
                  <label htmlFor="custom-date" className="form-label text-muted small fw-600 mb-0">
                    Or Choose Another Date:
                  </label>
                  <input
                    type="date"
                    id="custom-date"
                    min={todayStr}
                    value={selectedDate}
                    onChange={(e) => setSelectedDate(e.target.value)}
                    className="form-control rounded-pill px-3 py-1.5"
                    style={{ maxWidth: '200px' }}
                  />
                </div>

                <hr className="divider my-4" />

                {/* Available Slots Grid */}
                <h5 className="fw-bold text-dark mb-3">Available Sessions</h5>

                {loading ? (
                  <div className="text-center py-5">
                    <div className="spinner-border text-primary mb-3" role="status">
                      <span className="visually-hidden">Loading...</span>
                    </div>
                    <p className="text-muted">Loading available slots...</p>
                  </div>
                ) : slots.length > 0 ? (
                  <div className="row g-3">
                    {slots.map((slot) => {
                      const isSelected = selectedSlot?.id === slot.id;
                      return (
                        <div key={slot.id} className="col-6 col-sm-4">
                          <button
                            type="button"
                            onClick={() => setSelectedSlot(slot)}
                            className={`btn w-100 py-3 px-2 rounded-3 text-center transition-all ${
                              isSelected
                                ? 'btn-primary shadow-sm'
                                : 'btn-outline-primary bg-white text-dark border-light-subtle hover-bg-light'
                            }`}
                            style={{ transition: 'all 0.2s ease' }}
                          >
                            <i className={`bi ${isSelected ? 'bi-check-circle-fill' : 'bi-clock'} me-2`} />
                            {formatTime(slot.start_time)}
                          </button>
                        </div>
                      );
                    })}
                  </div>
                ) : (
                  <div className="text-center py-5 border border-dashed rounded-3 bg-light bg-opacity-50">
                    <i className="bi bi-calendar-x text-muted fs-1 mb-2 d-block" />
                    <p className="fw-bold text-dark mb-1">No Available Slots</p>
                    <p className="text-muted small mx-auto mb-0" style={{ maxWidth: '300px' }}>
                      There are no open consulting slots for this doctor on the selected date. Please pick another date.
                    </p>
                  </div>
                )}
              </div>

              {/* Booking Confirmation / CTA */}
              <div className="mt-5 pt-3 border-top">
                {selectedSlot && (
                  <div className="bg-primary bg-opacity-10 border border-primary border-opacity-10 rounded-3 p-3 mb-4 d-flex justify-content-between align-items-center">
                    <div>
                      <div className="text-muted small">Selected Appointment</div>
                      <div className="fw-bold text-dark">
                        {new Date(selectedDate).toLocaleDateString('en-US', {
                          weekday: 'long',
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric',
                        })}{' '}
                        at {formatTime(selectedSlot.start_time)}
                      </div>
                    </div>
                    <div className="text-end">
                      <div className="text-muted small">Total Fee</div>
                      <div className="fs-5 fw-bold text-primary">₹{doctor.consultation_fee ?? 500}</div>
                    </div>
                  </div>
                )}

                <button
                  type="button"
                  disabled={!selectedSlot || bookingLoading}
                  onClick={handleBooking}
                  className={`btn w-100 py-3 rounded-pill fw-bold text-uppercase tracking-wider transition-all ${
                    selectedSlot 
                      ? 'btn-primary shadow-sm hover-up' 
                      : 'btn-secondary text-white-50 opacity-50'
                  }`}
                  style={{ transition: 'all 0.2s ease-in-out' }}
                >
                  {bookingLoading ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true" />
                      Securing Your Slot...
                    </>
                  ) : (
                    'Confirm and Pay Booking'
                  )}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
