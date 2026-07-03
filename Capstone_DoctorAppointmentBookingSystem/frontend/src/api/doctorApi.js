import api from './axiosInstance';

export const searchDoctors = (params) =>
  api.get('/doctors/search', { params });

export const getDoctorDetail = (userId, params) =>
  api.get(`/doctors/${userId}`, { params });