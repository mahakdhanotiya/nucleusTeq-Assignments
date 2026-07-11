import { toast } from 'react-toastify';

/** Thin wrapper around react-toastify for consistent notification calls. */
export default function useToast() {
  return {
    success: (msg) => toast.success(msg),
    error:   (msg) => toast.error(msg),
    info:    (msg) => toast.info(msg),
    warn:    (msg) => toast.warn(msg),
  };
}