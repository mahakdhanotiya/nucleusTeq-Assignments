import { setPassword } from "../actions/auth.js";
import { showFieldError, clearErrors } from "../lib/utils/ui.js";

const params = new URLSearchParams(window.location.search);
let token = params.get("token");
const msgEl = document.getElementById("setMsg");

// Removed immediate blocking logic to avoid confusion
console.log("Token detected:", token ? "Yes" : "No");

document.getElementById("setPasswordForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  clearErrors("setPasswordForm");
  const password = document.getElementById("password").value;
  const confirmPassword = document.getElementById("confirmPassword").value;

  let hasError = false;
  if (!password) { showFieldError("password", "Password is required"); hasError = true; }
  else if (password.length < 6) { showFieldError("password", "Min 6 characters required"); hasError = true; }
  
  if (!confirmPassword) { showFieldError("confirmPassword", "Please confirm your password"); hasError = true; }

  if (!hasError && password !== confirmPassword) {
    showFieldError("confirmPassword", "Passwords do not match");
    hasError = true;
  }

  if (hasError) return;

  if (!token) {
    msgEl.className = "msg error";
    msgEl.textContent = "Invalid setup link. Please use the link sent to your email.";
    msgEl.style.display = "block";
    return;
  }

  try {
    const data = await setPassword(token, password);

    if (data.success) {
      msgEl.className = "msg success";
      msgEl.textContent = "Password set successfully! Redirecting to login...";
      msgEl.style.display = "block";
      setTimeout(() => window.location.href = "sign-in/index.html", 2000);
    } else {
      msgEl.className = "msg error";
      msgEl.textContent = data.message || "Failed to set password";
      msgEl.style.display = "block";
    }
  } catch (err) {
    msgEl.className = "msg error";
    msgEl.textContent = "Server error. Please try again.";
    msgEl.style.display = "block";
  }
});

/**
 * Toggle password visibility
 */
window.togglePassword = function(id) {
  const input = document.getElementById(id);
  input.type = input.type === "password" ? "text" : "password";
};
