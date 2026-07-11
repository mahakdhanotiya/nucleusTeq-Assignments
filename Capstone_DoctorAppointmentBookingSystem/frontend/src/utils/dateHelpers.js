/** Date/time utilities used across the application. */

/** Formats an ISO date string to "15 Jan 2025". */
export function formatDate(dateStr) {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleDateString('en-GB', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  });
}

/** Formats "09:00" → "09:00 AM" using 12-hour display. */
export function formatTime(timeStr) {
  if (!timeStr) return '—';
  const [h, m] = timeStr.split(':').map(Number);
  const period = h >= 12 ? 'PM' : 'AM';
  const hour   = h % 12 || 12;
  return `${String(hour).padStart(2, '0')}:${String(m).padStart(2, '0')} ${period}`;
}

/** Returns true if the appointment can still be cancelled (>2 hours before start). */
export function canCancel(appointmentDate, startTime) {
  if (!appointmentDate || !startTime) return false;
  const [h, m]     = startTime.split(':').map(Number);
  const apptMs     = new Date(appointmentDate);
  apptMs.setHours(h, m, 0, 0);
  return apptMs.getTime() - Date.now() > 2 * 60 * 60 * 1000;
}

/** Returns true if the appointment time has already passed (for doctor to mark status). */
export function hasAppointmentPassed(appointmentDate, endTime) {
  if (!appointmentDate || !endTime) return false;
  const [h, m] = endTime.split(':').map(Number);
  const endMs  = new Date(appointmentDate);
  endMs.setHours(h, m, 0, 0);
  return Date.now() > endMs.getTime();
}

/** Returns today's date as "YYYY-MM-DD" for date input min attributes. */
export function todayISO() {
  return new Date().toISOString().split('T')[0];
}