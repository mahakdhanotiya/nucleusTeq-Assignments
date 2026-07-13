import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { changePassword } from '../../api/userApi';
import { FieldError } from '../../components/common/ErrorMessage';
import useToast from '../../hooks/useToast';
import { RULES } from '../../utils/validators';

export default function ChangePasswordPage() {
  const toast = useToast();
  const [updating, setUpdating] = useState(false);
  
  // Show/hide password states
  const [showOldPassword, setShowOldPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    reset,
    setError,
    formState: { errors },
  } = useForm({ mode: 'onChange' });

  const newPasswordValue = watch('newPassword') || '';

  // Password rules checklist calculations
  const hasMinLength = newPasswordValue.length >= 8 && newPasswordValue.length <= 12;
  const hasUppercase = /[A-Z]/.test(newPasswordValue);
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>_\-+=]/.test(newPasswordValue);

  const onSubmit = async (data) => {
    try {
      setUpdating(true);
      await changePassword({
        old_password: data.oldPassword,
        new_password: data.newPassword
      });

      toast.success('Password changed successfully!');
      reset();
    } catch (err) {
      const serverMessage = err.response?.data?.message;
      const isIncorrectPass = serverMessage && (
        serverMessage.toLowerCase().includes('incorrect_password') ||
        serverMessage.toLowerCase().includes('incorrect password') ||
        serverMessage.toLowerCase().includes('current password')
      );

      if (isIncorrectPass) {
        setError('oldPassword', {
          type: 'server',
          message: serverMessage
        });
      } else {
        toast.error(serverMessage || 'Failed to change password. Please check your current password.');
      }
    } finally {
      setUpdating(false);
    }
  };

  return (
    <div className="container-fluid py-2" style={{ maxWidth: '640px', margin: '0 auto' }}>
      {/* Page Header */}
      <div className="page-header mb-4 text-center">
        <h1>Change Password</h1>
        <p className="text-muted">Update your account password regularly to keep your credentials secure.</p>
      </div>

      <div className="card border-0 shadow-sm overflow-hidden">
        {/* Decorative Top Accent Banner */}
        <div 
          style={{ 
            height: '8px', 
            background: 'linear-gradient(90deg, var(--primary) 0%, #3b82f6 50%, #60a5fa 100%)' 
          }} 
        />

        <div className="p-4 bg-white">
          <div className="text-center mb-4">
            <div 
              className="d-flex align-items-center justify-content-center bg-primary-light text-primary rounded-circle mx-auto mb-2"
              style={{ width: '60px', height: '60px' }}
            >
              <i className="bi bi-shield-lock-fill fs-3"></i>
            </div>
            <h5 className="fw-600 mb-0">Update Security Password</h5>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} noValidate>
            <div className="row g-3">
              {/* Current Password */}
              <div className="col-12">
                <label htmlFor="oldPassword" className="form-label fw-600">Current Password</label>
                <div className="input-group input-icon-group">
                  <span className="input-group-text"><i className="bi bi-key-fill"></i></span>
                  <input
                    type={showOldPassword ? 'text' : 'password'}
                    className={`form-control ${errors.oldPassword ? 'is-invalid' : ''}`}
                    id="oldPassword"
                    placeholder="Enter current password"
                    {...register('oldPassword', { required: 'Current password is required.' })}
                  />
                  <button
                    className="btn btn-outline-secondary"
                    type="button"
                    onClick={() => setShowOldPassword(!showOldPassword)}
                  >
                    <i className={`bi ${showOldPassword ? 'bi-eye-slash' : 'bi-eye'}`} />
                  </button>
                </div>
                <FieldError message={errors.oldPassword?.message} />
              </div>

              {/* New Password */}
              <div className="col-12">
                <label htmlFor="newPassword" className="form-label fw-600">New Password</label>
                <div className="input-group input-icon-group">
                  <span className="input-group-text"><i className="bi bi-lock-fill"></i></span>
                  <input
                    type={showNewPassword ? 'text' : 'password'}
                    className={`form-control ${errors.newPassword ? 'is-invalid' : ''}`}
                    id="newPassword"
                    placeholder="Enter new password (8-12 characters)"
                    {...register('newPassword', {
                      required: RULES.password.required,
                      minLength: RULES.password.minLength,
                      maxLength: RULES.password.maxLength,
                      validate: RULES.password.validate
                    })}
                  />
                  <button
                    className="btn btn-outline-secondary"
                    type="button"
                    onClick={() => setShowNewPassword(!showNewPassword)}
                  >
                    <i className={`bi ${showNewPassword ? 'bi-eye-slash' : 'bi-eye'}`} />
                  </button>
                </div>
                <FieldError message={errors.newPassword?.message} />

                {/* Password strength dynamic helper list */}
                <div className="strength-tracker mt-3">
                  <span className="text-muted small fw-600 d-block mb-2">Password Requirements Checklist:</span>
                  <div className="d-flex flex-column gap-1.5">
                    <div className={`strength-item ${hasMinLength ? 'valid' : 'invalid'}`}>
                      <i className={`bi ${hasMinLength ? 'bi-check-circle-fill text-success' : 'bi-circle text-muted'}`}></i>
                      <span>Length between 8 and 12 characters</span>
                    </div>
                    <div className={`strength-item ${hasUppercase ? 'valid' : 'invalid'}`}>
                      <i className={`bi ${hasUppercase ? 'bi-check-circle-fill text-success' : 'bi-circle text-muted'}`}></i>
                      <span>Contains at least one uppercase letter (A-Z)</span>
                    </div>
                    <div className={`strength-item ${hasSpecialChar ? 'valid' : 'invalid'}`}>
                      <i className={`bi ${hasSpecialChar ? 'bi-check-circle-fill text-success' : 'bi-circle text-muted'}`}></i>
                      <span>Contains at least one special character (!@#$%^&*)</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Confirm New Password */}
              <div className="col-12">
                <label htmlFor="confirmPassword" className="form-label fw-600">Confirm New Password</label>
                <div className="input-group input-icon-group">
                  <span className="input-group-text"><i className="bi bi-lock-fill"></i></span>
                  <input
                    type={showConfirmPassword ? 'text' : 'password'}
                    className={`form-control ${errors.confirmPassword ? 'is-invalid' : ''}`}
                    id="confirmPassword"
                    placeholder="Re-enter new password to confirm"
                    {...register('confirmPassword', {
                      required: 'Confirm password is required.',
                      validate: (value) => value === newPasswordValue || 'Passwords do not match.'
                    })}
                  />
                  <button
                    className="btn btn-outline-secondary"
                    type="button"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  >
                    <i className={`bi ${showConfirmPassword ? 'bi-eye-slash' : 'bi-eye'}`} />
                  </button>
                </div>
                <FieldError message={errors.confirmPassword?.message} />
              </div>
            </div>

            {/* Actions */}
            <div className="d-flex justify-content-end gap-2 mt-4 pt-3 border-top">
              <button
                type="submit"
                className="btn btn-primary px-4 py-2.5 fw-600 rounded-pill shadow-sm transition-all"
                disabled={updating}
              >
                {updating ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    Updating Password...
                  </>
                ) : (
                  <>
                    <i className="bi bi-shield-check me-2"></i>Update Password
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
