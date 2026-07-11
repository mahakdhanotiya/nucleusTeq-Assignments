import api from './axiosInstance';

export const register = (data) => {
  const url = data.role === 'DOCTOR' ? '/auth/register/doctor' : '/auth/register/patient';
  return api.post(url, data);
};

export const login = (data) => api.post('/auth/login', data);