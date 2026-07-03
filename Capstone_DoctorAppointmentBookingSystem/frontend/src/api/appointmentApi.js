import api from './axiosInstance';

export const bookAppointment = (data) =>
  api.post('/appointments', data);

export const cancelAppointment = (id, data) =>
  api.patch(`/appointments/${id}/cancel`, data);

export const getMyAppointments = (params) =>
  api.get('/appointments/me', { params });

export const getDoctorAppointments = (params) =>
  api.get('/appointments/doctor/me', { params });

export const updateAppointmentStatus = (id, data) =>
  api.patch(`/appointments/${id}/status`, data);

export const getAppointmentDetail = (id) =>
  api.get(`/appointments/${id}`);