import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';

import { login as loginApi } from '../../api/authApi';
import { FieldError } from '../../components/common/ErrorMessage';
import useAuth from '../../hooks/useAuth';
import useToast from '../../hooks/useToast';
import { RULES } from '../../utils/validators';

export default function LoginPage() {
  const { login } = useAuth();
  const toast = useToast();
  const [submitting, setSubmitting] = useState(false);
  const [authError, setAuthError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm({ mode: 'onBlur' });

  const onSubmit = async (data) => {
    setSubmitting(true);
    setAuthError('');
    try {
      const res = await loginApi(data);
      const { access_token, user } = res.data;
      toast.success('Login successful. Welcome back!');
      login(access_token, user);
    } catch (err) {
      if (!err.toasted) {
        const message = err.response?.data?.message || 'Login failed. Please try again.';
        setAuthError(message);
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <h4 className="mb-4 fw-600">Log In</h4>

      {/* Global Form-level Error Alert */}
      {authError && (
        <div className="alert alert-danger d-flex align-items-center py-2 px-3 mb-3 small" role="alert">
          <i className="bi bi-exclamation-triangle-fill me-2 fs-6"></i>
          <div>{authError}</div>
        </div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        {/* Email */}
        <div className="mb-3">
          <label className="form-label">Email</label>
          <input
            type="email"
            className={`form-control ${errors.email || authError ? 'is-invalid' : ''}`}
            placeholder="you@example.com"
            {...register('email', {
              ...RULES.email,
              onChange: () => setAuthError('') // Clear errors on input change
            })}
          />
          <FieldError message={errors.email?.message} />
        </div>

        {/* Password */}
        <div className="mb-4">
          <label className="form-label">Password</label>
          <div className="input-group">
            <input
              type={showPassword ? 'text' : 'password'}
              className={`form-control ${errors.password || authError ? 'is-invalid' : ''}`}
              placeholder="••••••••"
              {...register('password', {
                required: 'Password is required.',
                onChange: () => setAuthError('') // Clear errors on input change
              })}
            />
            <button
              className="btn btn-outline-secondary"
              type="button"
              onClick={() => setShowPassword(!showPassword)}
            >
              <i className={`bi ${showPassword ? 'bi-eye-slash' : 'bi-eye'}`} />
            </button>
          </div>
          <FieldError message={errors.password?.message} />
        </div>

        <button type="submit" className="btn btn-primary w-100 py-2" disabled={submitting}>
          {submitting && <span className="spinner-border spinner-border-sm me-2" />}
          Log In
        </button>
      </form>

      <p className="text-center text-muted small mt-4 mb-0">
        Don&apos;t have an account?{' '}
        <Link to="/register" className="text-primary fw-600 text-decoration-none">
          Register here
        </Link>
      </p>
    </>
  );
}