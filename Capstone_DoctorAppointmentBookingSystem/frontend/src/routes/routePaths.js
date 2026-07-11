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
  PAYMENT_SUCCESS: '/payment-success/:appointmentId',
  MY_APPOINTMENTS: '/my-appointments',

  // Doctor
  DOCTOR_DASHBOARD: '/doctor/dashboard',
  DOCTOR_SLOTS: '/doctor/slots',
  DOCTOR_APPOINTMENT_DETAIL: '/doctor/appointments/:appointmentId',
  DOCTOR_LEAVE_REQUEST: '/doctor/leave-requests',

  // Admin
  ADMIN_DASHBOARD: '/admin/dashboard',
  ADMIN_LEAVE_REQUESTS: '/admin/leave-requests',

  // Shared
  PROFILE: '/profile',
  CHANGE_PASSWORD: '/change-password',
};

export const doctorDetailPath     = (userId) => `/doctors/${userId}`;
export const paymentPath          = (appointmentId) => `/payment/${appointmentId}`;
export const paymentSuccessPath   = (appointmentId) => `/payment-success/${appointmentId}`;
export const doctorApptDetailPath = (appointmentId) => `/doctor/appointments/${appointmentId}`;