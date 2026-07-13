import api from './axiosInstance';

export const bookAppointment = (data) =>
  api.post('/appointments', data);

export const cancelAppointment = (id, data) =>
  api.patch(`/appointments/${id}/cancel`, data);

export const getMyAppointments = (params) =>
  api.get('/appointments/patient', { params });

export const getDoctorAppointments = (params) =>
  api.get('/appointments/doctor', { params });

export const updateAppointmentStatus = (id, data) =>
  api.patch(`/appointments/${id}/status`, data);

export const getAppointmentDetail = (id) =>
  api.get(`/appointments/${id}`);

// Leave Request APIs
export const submitLeaveRequest = (data) =>
  api.post('/appointments/doctor/cancel-request', data);

export const getLeaveRequests = (params) =>
  api.get('/appointments/admin/cancel-requests', { params });

export const approveLeaveRequest = (id) =>
  api.put(`/appointments/admin/cancel-requests/${id}/approve`);

export const rejectLeaveRequest = (id) =>
  api.put(`/appointments/admin/cancel-requests/${id}/reject`);