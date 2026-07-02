import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';

import { register as registerApi } from '../../api/authApi';
import { FieldError } from '../../components/common/ErrorMessage';
import useToast from '../../hooks/useToast';
import { RULES } from '../../utils/validators';

const SPECIALIZATIONS = [
  'Cardiologist', 'Dermatologist', 'Dentist', 'Neurologist',
  'Orthopedic', 'Pediatrician', 'General Physician', 'Gynecologist',
];

export default function RegisterPage() {
  const navigate = useNavigate();
  const toast = useToast();
  const [submitting, setSubmitting] = useState(false);
  const [role, setRole] = useState('PATIENT');

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({ mode: 'onBlur', defaultValues: { role: 'PATIENT' } });

  const selectedRole = watch('role', role);

  const onSubmit = async (data) => {
    setSubmitting(true);
    try {
      // Strip irrelevant fields before sending, based on role
      const payload = { ...data };
      if (data.role === 'PATIENT') {
        delete payload.qualification;
        delete payload.specialization;
        delete payload.experience_years;
        delete payload.license_number;
      } else {
        delete payload.gender;
        delete payload.date_of_birth;
        payload.experience_years = Number(payload.experience_years);
      }

      await registerApi(payload);

      toast.success(
        data.role === 'DOCTOR'
          ? 'Registration successful. Your account is pending admin approval.'
          : 'Registration successful. Please log in.'
      );
      navigate('/login');
    } catch (err) {
      const message = err.response?.data?.message || 'Registration failed. Please try again.';
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <h4 className="mb-4 fw-600">Create an Account</h4>

      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        {/* Role toggle */}
        <div className="mb-3">
          <label className="form-label">I am a</label>
          <div className="btn-group w-100" role="group">
            <input
              type="radio"
              className="btn-check"
              id="role-patient"
              value="PATIENT"
              {...register('role')}
              onChange={() => setRole('PATIENT')}
              defaultChecked
            />
            <label className="btn btn-outline-primary" htmlFor="role-patient">
              <i className="bi bi-person me-1" /> Patient
            </label>

            <input
              type="radio"
              className="btn-check"
              id="role-doctor"
              value="DOCTOR"
              {...register('role')}
              onChange={() => setRole('DOCTOR')}
            />
            <label className="btn btn-outline-primary" htmlFor="role-doctor">
              <i className="bi bi-clipboard2-pulse me-1" /> Doctor
            </label>
          </div>
        </div>

        {/* Shared fields */}
        <div className="mb-3">
          <label className="form-label">Full Name</label>
          <input
            className={`form-control ${errors.full_name ? 'is-invalid' : ''}`}
            placeholder="John Doe"
            {...register('full_name', RULES.fullName)}
          />
          <FieldError message={errors.full_name?.message} />
        </div>

        <div className="row">
          <div className="col-md-6 mb-3">
            <label className="form-label">Email</label>
            <input
              type="email"
              className={`form-control ${errors.email ? 'is-invalid' : ''}`}
              placeholder="you@example.com"
              {...register('email', RULES.email)}
            />
            <FieldError message={errors.email?.message} />
          </div>

          <div className="col-md-6 mb-3">
            <label className="form-label">Phone Number</label>
            <input
              className={`form-control ${errors.phone_number ? 'is-invalid' : ''}`}
              placeholder="9876543210"
              maxLength={10}
              {...register('phone_number', RULES.phone)}
            />
            <FieldError message={errors.phone_number?.message} />
          </div>
        </div>

        <div className="mb-3">
          <label className="form-label">Password</label>
          <input
            type="password"
            className={`form-control ${errors.password ? 'is-invalid' : ''}`}
            placeholder="8-12 chars, 1 uppercase, 1 special"
            {...register('password', RULES.password)}
          />
          <FieldError message={errors.password?.message} />
        </div>

        {/* Patient-only fields */}
        {selectedRole === 'PATIENT' && (
          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Gender</label>
              <select
                className={`form-select ${errors.gender ? 'is-invalid' : ''}`}
                {...register('gender', { required: 'Gender is required.' })}
              >
                <option value="">Select gender</option>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </select>
              <FieldError message={errors.gender?.message} />
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label">Date of Birth</label>
              <input
                type="date"
                className={`form-control ${errors.date_of_birth ? 'is-invalid' : ''}`}
                {...register('date_of_birth', RULES.dateOfBirth)}
              />
              <FieldError message={errors.date_of_birth?.message} />
            </div>
          </div>
        )}

        {/* Doctor-only fields */}
        {selectedRole === 'DOCTOR' && (
          <>
            <div className="row">
              <div className="col-md-6 mb-3">
                <label className="form-label">Qualification</label>
                <input
                  className={`form-control ${errors.qualification ? 'is-invalid' : ''}`}
                  placeholder="MBBS, MD"
                  {...register('qualification', RULES.qualification)}
                />
                <FieldError message={errors.qualification?.message} />
              </div>

              <div className="col-md-6 mb-3">
                <label className="form-label">Specialization</label>
                <select
                  className={`form-select ${errors.specialization ? 'is-invalid' : ''}`}
                  {...register('specialization', RULES.specialization)}
                >
                  <option value="">Select specialization</option>
                  {SPECIALIZATIONS.map((s) => (
                    <option key={s} value={s}>{s}</option>
                  ))}
                </select>
                <FieldError message={errors.specialization?.message} />
              </div>
            </div>

            <div className="row">
              <div className="col-md-6 mb-3">
                <label className="form-label">Experience (years)</label>
                <input
                  type="number"
                  min="0"
                  className={`form-control ${errors.experience_years ? 'is-invalid' : ''}`}
                  placeholder="5"
                  {...register('experience_years', RULES.experienceYears)}
                />
                <FieldError message={errors.experience_years?.message} />
              </div>

              <div className="col-md-6 mb-3">
                <label className="form-label">License Number</label>
                <input
                  className={`form-control ${errors.license_number ? 'is-invalid' : ''}`}
                  placeholder="LIC-2024-001"
                  {...register('license_number', RULES.licenseNumber)}
                />
                <FieldError message={errors.license_number?.message} />
              </div>
            </div>

            <div className="alert alert-warning d-flex align-items-start gap-2 small mb-3">
              <i className="bi bi-info-circle mt-1" />
              <span>Doctor accounts require Admin approval before you can log in.</span>
            </div>
          </>
        )}

        <button type="submit" className="btn btn-primary w-100 py-2 mt-2" disabled={submitting}>
          {submitting && <span className="spinner-border spinner-border-sm me-2" />}
          Create Account
        </button>
      </form>

      <p className="text-center text-muted small mt-4 mb-0">
        Already have an account?{' '}
        <Link to="/login" className="text-primary fw-600 text-decoration-none">
          Log in here
        </Link>
      </p>
    </>
  );
}