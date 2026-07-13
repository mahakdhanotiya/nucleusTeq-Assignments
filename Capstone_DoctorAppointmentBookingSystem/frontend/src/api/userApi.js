import api from './axiosInstance';

export const getProfile          = ()     => api.get('/users/profile');
export const updateProfile       = (data) => api.put('/users/profile', data);
export const updateDoctorProfile = (data) => api.put('/users/profile/doctor-profile', data);
export const changePassword      = (data) => api.put('/users/change-password', data);
export const updateActiveStatus   = (isActive) => api.patch(`/users/profile/active-status?is_active=${isActive}`);