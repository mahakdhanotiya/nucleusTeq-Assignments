/** Client-side validation rules. */

export const RULES = {
  fullName: {
    required: 'Full name is required.',
    minLength: { value: 2, message: 'Minimum 2 characters.' },
    pattern: {
      value: /^[A-Za-z\s]{2,}$/,
      message: 'Only alphabets and spaces allowed.',
    },
  },

  email: {
    required: 'Email is required.',
    pattern: {
      value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      message: 'Enter a valid email address.',
    },
  },

  phone: {
    required: 'Phone number is required.',
    pattern: {
      value: /^\d{10}$/,
      message: 'Phone number must be exactly 10 digits.',
    },
  },

  password: {
    required: 'Password is required.',
    minLength: { value: 8,  message: 'Minimum 8 characters.' },
    maxLength: { value: 12, message: 'Maximum 12 characters.' },
    validate: (v) => {
      if (!/[A-Z]/.test(v)) return 'Must contain at least one uppercase letter.';
      if (!/[!@#$%^&*(),.?":{}|<>_\-+=]/.test(v))
        return 'Must contain at least one special character.';
      return true;
    },
  },

  dateOfBirth: {
    required: 'Date of birth is required.',
    validate: (v) =>
      new Date(v) < new Date() || 'Date of birth must be a past date.',
  },

  qualification: {
    required: 'Qualification is required.',
    minLength: { value: 2, message: 'Minimum 2 characters.' },
  },

  specialization: {
    required: 'Specialization is required.',
  },

  experienceYears: {
    required: 'Experience is required.',
    min: { value: 0, message: 'Cannot be negative.' },
    valueAsNumber: true,
  },

  licenseNumber: {
    required: 'License number is required.',
  },

  slotDate: {
    required: 'Date is required.',
    validate: (v) => {
      const d = new Date(v);
      d.setHours(0, 0, 0, 0);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return d >= today || 'Date must be today or a future date.';
    },
  },

  time: {
    required: 'Time is required.',
    pattern: {
      value: /^([01]\d|2[0-3]):[0-5]\d$/,
      message: 'Use HH:MM 24-hour format.',
    },
  },

  appointmentDate: {
    required: 'Appointment date is required.',
    validate: (v) => {
      const d = new Date(v);
      d.setHours(0, 0, 0, 0);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return d >= today || 'Date must be today or a future date.';
    },
  },
};