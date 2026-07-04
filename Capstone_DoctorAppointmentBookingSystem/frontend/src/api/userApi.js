import api from './axiosInstance';

export const getProfile          = ()     => api.get('/users/me');
export const updateProfile       = (data) => api.put('/users/me', data);
export const updateDoctorProfile = (data) => api.put('/users/me/doctor-profile', data);
export const changePassword      = (data) => api.put('/users/change-password', data);