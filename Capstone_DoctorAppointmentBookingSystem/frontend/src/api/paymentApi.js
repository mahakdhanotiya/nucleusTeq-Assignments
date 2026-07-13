import api from './axiosInstance';

export const processPayment = (appointmentId, data) =>
  api.post(`/payments/${appointmentId}/process`, data);

export const getPayment = (appointmentId) =>
  api.get(`/payments/${appointmentId}`);