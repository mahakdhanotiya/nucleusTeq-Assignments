import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { getProfile, updateProfile, updateDoctorProfile } from '../../api/userApi';
import { FieldError } from '../../components/common/ErrorMessage';
import useToast from '../../hooks/useToast';
import useAuth from '../../hooks/useAuth';
import { RULES } from '../../utils/validators';

export default function ProfilePage() {
  const toast = useToast();
  const { user, updateUser } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
  } = useForm({ mode: 'onChange' });

  const profilePhotoUrl = watch('profilePhotoUrl') || '';
  const fullName = watch('fullName') || '';

  useEffect(() => {
    fetchProfileData();
  }, []);

  const fetchProfileData = async () => {
    try {
      setLoading(true);
      const res = await getProfile();
      const data = res.data;
      setProfile(data);
      
      setValue('fullName', data.full_name || '');
      setValue('phoneNumber', data.phone_number || '');
      if (data.role === 'DOCTOR') {
        setValue('qualification', data.qualification || '');
        setValue('consultationFee', data.consultation_fee || 0);
        setValue('clinicAddress', data.clinic_address || '');
        setValue('profilePhotoUrl', data.profile_photo_url || '');
      }
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to fetch profile details.');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data) => {
    try {
      setUpdating(true);
      
      const generalData = {
        full_name: data.fullName.trim(),
        phone_number: data.phoneNumber.trim()
      };
      const generalRes = await updateProfile(generalData);

      if (profile?.role === 'DOCTOR') {
        const doctorData = {
          qualification: data.qualification.trim(),
          consultation_fee: Number(data.consultationFee),
          clinic_address: data.clinicAddress.trim(),
          profile_photo_url: data.profilePhotoUrl ? data.profilePhotoUrl.trim() : ''
        };
        const doctorRes = await updateDoctorProfile(doctorData);
        setProfile(doctorRes.data);
      } else {
        setProfile(generalRes.data);
      }

      if (user) {
        updateUser({
          full_name: data.fullName.trim(),
          phone_number: data.phoneNumber.trim()
        });
      }

      toast.success('Profile updated successfully!');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to update profile.');
    } finally {
      setUpdating(false);
    }
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '300px' }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  const roleBadgeColor = {
    PATIENT: 'bg-primary-light text-primary',
    DOCTOR: 'bg-success text-white',
    ADMIN: 'bg-danger text-white'
  }[profile?.role] || 'bg-secondary text-white';

  return (
    <div className="container-fluid py-2">
      {/* Page Header */}
      <div className="page-header mb-4">
        <h1>My Profile</h1>
        <p>Manage your account credentials, contact info, and role-specific configurations.</p>
      </div>

      <div className="card border-0 shadow-sm overflow-hidden mb-4">
        {/* Cover Header Graphic */}
        <div className="profile-cover d-flex align-items-end justify-content-end p-3">
          <span className="badge bg-white text-primary fw-600 px-3 py-2 rounded-pill shadow-sm">
            <i className="bi bi-shield-fill-check me-1"></i> Verified Account
          </span>
        </div>

        {/* Profile Card Header Body */}
        <div className="p-4 border-bottom bg-white">
          <div className="d-flex flex-column flex-sm-row align-items-center align-items-sm-start text-center text-sm-start gap-4">
            <div className="profile-avatar-container">
              {profile?.role === 'DOCTOR' && profilePhotoUrl ? (
                <img
                  src={profilePhotoUrl}
                  alt={fullName}
                  className="rounded-circle profile-avatar img-thumbnail"
                  style={{ width: '110px', height: '110px', objectFit: 'cover' }}
                  onError={(e) => {
                    e.target.src = `https://ui-avatars.com/name?name=${encodeURIComponent(fullName)}&background=2563eb&color=fff&size=110`;
                  }}
                />
              ) : (
                <div
                  className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center profile-avatar"
                  style={{ width: '110px', height: '110px', fontSize: '2.8rem', fontWeight: 'bold' }}
                >
                  {fullName?.[0]?.toUpperCase() || '?'}
                </div>
              )}
            </div>

            <div className="flex-grow-1 pt-2">
              <div className="d-flex flex-column flex-md-row align-items-center justify-content-between gap-3">
                <div>
                  <h2 className="fw-700 mb-1">{fullName || 'User Name'}</h2>
                  <p className="text-muted mb-0 d-flex align-items-center gap-2">
                    <i className="bi bi-envelope-at-fill text-primary"></i> {profile?.email}
                  </p>
                </div>
                <div>
                  <span className={`badge px-3 py-2 rounded-pill profile-role-badge ${roleBadgeColor}`}>
                    {profile?.role}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit(onSubmit)} noValidate className="p-4 bg-white">
          {profile?.role === 'DOCTOR' ? (
            /* Doctor View: Two columns for balance */
            <div className="row g-4">
              {/* Left Column: Personal Information */}
              <div className="col-lg-6">
                <h5 className="fw-600 mb-3 text-secondary d-flex align-items-center gap-2 border-bottom pb-2">
                  <i className="bi bi-person-bounding-box text-primary"></i> Personal Details
                </h5>
                
                <div className="d-flex flex-column gap-3">
                  {/* Full Name */}
                  <div>
                    <label htmlFor="fullName" className="form-label fw-600">Full Name</label>
                    <div className="input-group input-icon-group">
                      <span className="input-group-text"><i className="bi bi-person-circle"></i></span>
                      <input
                        type="text"
                        className={`form-control ${errors.fullName ? 'is-invalid' : ''}`}
                        id="fullName"
                        {...register('fullName', RULES.fullName)}
                      />
                    </div>
                    <FieldError message={errors.fullName?.message} />
                  </div>

                  {/* Email Address (Read-only) */}
                  <div>
                    <label htmlFor="email" className="form-label fw-600">Email Address (Read-only)</label>
                    <div className="input-group input-icon-group">
                      <span className="input-group-text"><i className="bi bi-lock-fill"></i></span>
                      <input
                        type="email"
                        className="form-control bg-light"
                        id="email"
                        value={profile?.email || ''}
                        disabled
                      />
                    </div>
                    <div className="form-text text-muted" style={{ fontSize: '.75rem' }}>
                      * Email cannot be updated to ensure account integrity.
                    </div>
                  </div>

                  {/* Phone Number */}
                  <div>
                    <label htmlFor="phoneNumber" className="form-label fw-600">Phone Number</label>
                    <div className="input-group input-icon-group">
                      <span className="input-group-text"><i className="bi bi-telephone-fill"></i></span>
                      <input
                        type="text"
                        className={`form-control ${errors.phoneNumber ? 'is-invalid' : ''}`}
                        id="phoneNumber"
                        {...register('phoneNumber', RULES.phone)}
                      />
                    </div>
                    <FieldError message={errors.phoneNumber?.message} />
                  </div>
                </div>
              </div>

              {/* Right Column: Professional Details */}
              <div className="col-lg-6">
                <h5 className="fw-600 mb-3 text-secondary d-flex align-items-center gap-2 border-bottom pb-2">
                  <i className="bi bi-heart-pulse-fill text-primary"></i> Professional Details
                </h5>
                
                <div className="d-flex flex-column gap-3">
                  <div className="row g-2 mb-1">
                    <div className="col-sm-6">
                      <span className="text-muted small">Specialization</span>
                      <div className="fw-600 py-1.5 px-3 bg-light rounded text-dark border d-flex align-items-center gap-2">
                        <i className="bi bi-patch-check text-primary"></i> {profile?.specialization || 'N/A'}
                      </div>
                    </div>
                    <div className="col-sm-6">
                      <span className="text-muted small">License Number</span>
                      <div className="fw-600 py-1.5 px-3 bg-light rounded text-dark border d-flex align-items-center gap-2">
                        <i className="bi bi-journal-check text-primary"></i> {profile?.license_number || 'N/A'}
                      </div>
                    </div>
                  </div>

                  {/* Qualification */}
                  <div>
                    <label htmlFor="qualification" className="form-label fw-600">Qualification</label>
                    <div className="input-group input-icon-group">
                      <span className="input-group-text"><i className="bi bi-mortarboard-fill"></i></span>
                      <input
                        type="text"
                        className={`form-control ${errors.qualification ? 'is-invalid' : ''}`}
                        id="qualification"
                        {...register('qualification', RULES.qualification)}
                      />
                    </div>
                    <FieldError message={errors.qualification?.message} />
                  </div>

                  {/* Consultation Fee */}
                  <div>
                    <label htmlFor="consultationFee" className="form-label fw-600">Consultation Fee (₹)</label>
                    <div className="input-group input-icon-group">
                      <span className="input-group-text"><i className="bi bi-currency-rupee"></i></span>
                      <input
                        type="number"
                        className={`form-control ${errors.consultationFee ? 'is-invalid' : ''}`}
                        id="consultationFee"
                        {...register('consultationFee', {
                          required: 'Consultation fee is required.',
                          min: { value: 0, message: 'Cannot be negative.' }
                        })}
                      />
                    </div>
                    <FieldError message={errors.consultationFee?.message} />
                  </div>

                  {/* Profile Photo URL */}
                  <div>
                    <label htmlFor="profilePhotoUrl" className="form-label fw-600">Profile Photo URL</label>
                    <div className="input-group input-icon-group">
                      <span className="input-group-text"><i className="bi bi-image-fill"></i></span>
                      <input
                        type="text"
                        className="form-control"
                        id="profilePhotoUrl"
                        placeholder="https://example.com/photo.jpg"
                        {...register('profilePhotoUrl')}
                      />
                    </div>
                  </div>

                  {/* Clinic Address */}
                  <div>
                    <label htmlFor="clinicAddress" className="form-label fw-600">Clinic Address</label>
                    <div className="input-group">
                      <span className="input-group-text bg-light"><i className="bi bi-geo-alt-fill text-muted"></i></span>
                      <textarea
                        rows="3"
                        className={`form-control ${errors.clinicAddress ? 'is-invalid' : ''}`}
                        id="clinicAddress"
                        placeholder="Address of your clinic"
                        {...register('clinicAddress', { required: 'Clinic address is required.' })}
                      ></textarea>
                    </div>
                    <FieldError message={errors.clinicAddress?.message} />
                  </div>
                </div>
              </div>
            </div>
          ) : (
            /* Patient/Admin View: Single centered column for clean simplicity */
            <div style={{ maxWidth: '600px', margin: '0 auto' }}>
              <h5 className="fw-600 mb-3 text-secondary d-flex align-items-center gap-2 border-bottom pb-2">
                <i className="bi bi-person-bounding-box text-primary"></i> Personal Details
              </h5>
              
              <div className="d-flex flex-column gap-3">
                {/* Full Name */}
                <div>
                  <label htmlFor="fullName" className="form-label fw-600">Full Name</label>
                  <div className="input-group input-icon-group">
                    <span className="input-group-text"><i className="bi bi-person-circle"></i></span>
                    <input
                      type="text"
                      className={`form-control ${errors.fullName ? 'is-invalid' : ''}`}
                      id="fullName"
                      {...register('fullName', RULES.fullName)}
                    />
                  </div>
                  <FieldError message={errors.fullName?.message} />
                </div>

                {/* Email Address (Read-only) */}
                <div>
                  <label htmlFor="email" className="form-label fw-600">Email Address (Read-only)</label>
                  <div className="input-group input-icon-group">
                    <span className="input-group-text"><i className="bi bi-lock-fill"></i></span>
                    <input
                      type="email"
                      className="form-control bg-light"
                      id="email"
                      value={profile?.email || ''}
                      disabled
                    />
                  </div>
                  <div className="form-text text-muted" style={{ fontSize: '.75rem' }}>
                    * Email cannot be updated to ensure account integrity.
                  </div>
                </div>

                {/* Phone Number */}
                <div>
                  <label htmlFor="phoneNumber" className="form-label fw-600">Phone Number</label>
                  <div className="input-group input-icon-group">
                    <span className="input-group-text"><i className="bi bi-telephone-fill"></i></span>
                    <input
                      type="text"
                      className={`form-control ${errors.phoneNumber ? 'is-invalid' : ''}`}
                      id="phoneNumber"
                      {...register('phoneNumber', RULES.phone)}
                    />
                  </div>
                  <FieldError message={errors.phoneNumber?.message} />
                </div>
              </div>
            </div>
          )}

          {/* Save Button */}
          <div className="d-flex justify-content-end mt-4 pt-3 border-top">
            <button
              type="submit"
              className="btn btn-primary px-5 py-2.5 shadow-sm fw-600 transition-all rounded-pill"
              disabled={updating}
            >
              {updating ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  Saving Changes...
                </>
              ) : (
                <>
                  <i className="bi bi-check-circle-fill me-2"></i>Save Changes
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
