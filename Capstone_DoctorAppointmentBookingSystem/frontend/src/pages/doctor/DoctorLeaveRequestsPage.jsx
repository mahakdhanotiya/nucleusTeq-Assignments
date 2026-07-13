import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { submitLeaveRequest } from '../../api/appointmentApi';
import { FieldError } from '../../components/common/ErrorMessage';
import useToast from '../../hooks/useToast';
import { RULES } from '../../utils/validators';

export default function DoctorLeaveRequestsPage() {
  const toast = useToast();
  const [submitting, setSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({ mode: 'onChange' });

  const onSubmit = async (data) => {
    try {
      setSubmitting(true);
      const payload = {
        date: data.date,
        start_time: data.startTime,
        end_time: data.endTime,
        reason: data.reason.trim(),
      };
      await submitLeaveRequest(payload);
      toast.success('Leave request submitted successfully!');
      reset();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to submit leave request.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container py-4" style={{ maxWidth: '600px' }}>
      <div className="page-header mb-4">
        <h1>Leave Request 🛑</h1>
        <p className="text-muted">Request the administrator to cancel all appointments/slots falling within a given time frame on a specific date.</p>
      </div>

      <div className="card border-0 shadow-sm p-4" style={{ borderRadius: '12px' }}>
        <form onSubmit={handleSubmit(onSubmit)} className="d-flex flex-column gap-3">
          {/* Target Date */}
          <div>
            <label htmlFor="date" className="form-label fw-600 text-dark">Target Date</label>
            <div className="input-group">
              <span className="input-group-text"><i className="bi bi-calendar-date"></i></span>
              <input
                type="date"
                id="date"
                className={`form-control ${errors.date ? 'is-invalid' : ''}`}
                {...register('date', RULES.slotDate)}
              />
            </div>
            <FieldError message={errors.date?.message} />
          </div>

          <div className="row g-3">
            {/* Start Time */}
            <div className="col-md-6">
              <label htmlFor="startTime" className="form-label fw-600 text-dark">Start Time</label>
              <div className="input-group">
                <span className="input-group-text"><i className="bi bi-clock"></i></span>
                <input
                  type="time"
                  id="startTime"
                  className={`form-control ${errors.startTime ? 'is-invalid' : ''}`}
                  {...register('startTime', RULES.time)}
                />
              </div>
              <FieldError message={errors.startTime?.message} />
            </div>

            {/* End Time */}
            <div className="col-md-6">
              <label htmlFor="endTime" className="form-label fw-600 text-dark">End Time</label>
              <div className="input-group">
                <span className="input-group-text"><i className="bi bi-clock-history"></i></span>
                <input
                  type="time"
                  id="endTime"
                  className={`form-control ${errors.endTime ? 'is-invalid' : ''}`}
                  {...register('endTime', RULES.time)}
                />
              </div>
              <FieldError message={errors.endTime?.message} />
            </div>
          </div>

          {/* Reason */}
          <div>
            <label htmlFor="reason" className="form-label fw-600 text-dark">Reason</label>
            <div className="input-group">
              <span className="input-group-text"><i className="bi bi-pencil-square"></i></span>
              <textarea
                id="reason"
                rows="4"
                placeholder="State the reason for leave..."
                className={`form-control ${errors.reason ? 'is-invalid' : ''}`}
                {...register('reason', {
                  required: 'Reason is required.',
                  minLength: { value: 5, message: 'Minimum 5 characters.' }
                })}
              />
            </div>
            <FieldError message={errors.reason?.message} />
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            className="btn btn-danger w-100 rounded-pill py-2.5 fw-600 mt-2 d-flex align-items-center justify-content-center gap-2"
            disabled={submitting}
          >
            {submitting ? (
              <>
                <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                Submitting Request...
              </>
            ) : (
              <>
                <i className="bi bi-send-fill"></i>
                Submit Leave Request
              </>
            )}
          </button>
        </form>
      </div>
    </div>
  );
}
