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

      if (status === 401) {
        removeToken();
        toast.error('Session expired. Please log in again.');
        // Redirect without react-router (instance lives outside component tree)
        window.location.href = '/login';
      } else if (status === 403) {
        toast.error(message);
      } else if (status === 422) {
        // Validation errors — let the calling component handle detail
      } else if (status >= 500) {
        toast.error('Server error. Please try again later.');
      }

      return Promise.reject(err);
    }
  );

  return instance;
}

const api = createInstance(process.env.REACT_APP_API_BASE_URL);

export default api;
