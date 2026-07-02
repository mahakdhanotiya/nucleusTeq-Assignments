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

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ mode: 'onBlur' });

  const onSubmit = async (data) => {
    setSubmitting(true);
    try {
      const res = await loginApi(data);
      const { access_token, user } = res.data;
      toast.success('Login successful. Welcome back!');
      login(access_token, user);
    } catch (err) {
      const message = err.response?.data?.message || 'Login failed. Please try again.';
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <h4 className="mb-4 fw-600">Log In</h4>

      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <div className="mb-3">
          <label className="form-label">Email</label>
          <input
            type="email"
            className={`form-control ${errors.email ? 'is-invalid' : ''}`}
            placeholder="you@example.com"
            {...register('email', RULES.email)}
          />
          <FieldError message={errors.email?.message} />
        </div>

        <div className="mb-4">
          <label className="form-label">Password</label>
          <input
            type="password"
            className={`form-control ${errors.password ? 'is-invalid' : ''}`}
            placeholder="••••••••"
            {...register('password', { required: 'Password is required.' })}
          />
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