/** Centralized route path constants — avoids magic strings across the app. */
export const ROUTES = {
  // Public
  LOGIN: '/login',
  REGISTER: '/register',
  UNAUTHORIZED: '/unauthorized',

  // Patient
  HOME: '/',
  DOCTOR_DETAIL: '/doctors/:userId',
  PAYMENT: '/payment/:appointmentId',
  MY_APPOINTMENTS: '/my-appointments',

  // Doctor
  DOCTOR_DASHBOARD: '/doctor/dashboard',
  DOCTOR_SLOTS: '/doctor/slots',
  DOCTOR_APPOINTMENT_DETAIL: '/doctor/appointments/:appointmentId',

  // Admin
  ADMIN_DASHBOARD: '/admin/dashboard',

  // Shared
  PROFILE: '/profile',
  CHANGE_PASSWORD: '/change-password',
};

export const doctorDetailPath  = (userId) => `/doctors/${userId}`;
export const paymentPath       = (appointmentId) => `/payment/${appointmentId}`;
export const doctorApptDetailPath = (appointmentId) => `/doctor/appointments/${appointmentId}`;