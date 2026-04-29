import { setPassword } from "../actions/auth.js";

const params = new URLSearchParams(window.location.search);
const token = params.get("token");
const msgEl = document.getElementById("setMsg");

if (!token) {
  msgEl.className = "msg error";
  msgEl.textContent = "Invalid link. No token found.";
  msgEl.style.display = "block";
  document.getElementById("setPasswordForm").querySelector("button").disabled = true;
}

document.getElementById("setPasswordForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  msgEl.style.display = "none";

  const password = document.getElementById("password").value;
  const confirmPassword = document.getElementById("confirmPassword").value;

  if (password !== confirmPassword) {
    msgEl.className = "msg error";
    msgEl.textContent = "Passwords do not match";
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
