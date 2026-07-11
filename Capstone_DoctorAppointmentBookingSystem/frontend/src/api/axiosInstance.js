import axios from 'axios';
import { toast } from 'react-toastify';
import { getToken, removeToken } from '../utils/tokenHelpers';

/** Creates an Axios instance pointing at the given base URL. */
function createInstance(baseURL) {
  const instance = axios.create({ baseURL, timeout: 10000 });

  // Attach JWT on every request
  instance.interceptors.request.use((config) => {
    const token = getToken();
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  });

  // Global response error handling
  instance.interceptors.response.use(
    (res) => res,
    (err) => {
      const status  = err.response?.status;
      const message = err.response?.data?.message || 'Something went wrong.';

      const isAuthLogin = err.config?.url?.includes('/auth/login');
      if (status === 401 && !isAuthLogin) {
        removeToken();
        toast.error('Session expired. Please log in again.');
        err.toasted = true;
        // Redirect without react-router (instance lives outside component tree)
        window.location.href = '/login';
      }

      return Promise.reject(err);
    }
  );

  return instance;
}

const getBaseURL = () => {
  const envUrl = process.env.REACT_APP_API_BASE_URL;
  if (!envUrl || envUrl === 'undefined' || envUrl.trim() === '') {
    return 'http://127.0.0.1:8000';
  }
  return envUrl;
};

const api = createInstance(getBaseURL());

export default api;
