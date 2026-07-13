import api from './axiosInstance';

// --- Admin endpoints ---
export const getAllDoctors = () =>
  api.get('/admin/doctors');

export const approveDoctor = (id) =>
  api.patch(`/admin/doctors/${id}/approve`);

export const rejectDoctor = (id) =>
  api.patch(`/admin/doctors/${id}/reject`);

export const activateDoctor = (id) =>
  api.patch(`/admin/doctors/${id}/activate`);

export const deactivateDoctor = (id) =>
  api.patch(`/admin/doctors/${id}/deactivate`);

export const getUserDashboardStats = () =>
  api.get('/admin/dashboard/users');

export const getAppointmentDashboardStats = () =>
  api.get('/admin/dashboard/appointments');