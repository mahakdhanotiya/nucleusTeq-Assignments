import api from './axiosInstance';

export const createSlot = (data) =>
  api.post('/slots', data);

export const getMySlots = (params) =>
  api.get('/slots/my', { params });

export const getDoctorSlots = (doctorId, params) =>
  api.get(`/slots/doctor/${doctorId}`, { params });

export const updateSlot = (id, data) =>
  api.put(`/slots/${id}`, data);

export const deleteSlot = (id) =>
  api.delete(`/slots/${id}`);