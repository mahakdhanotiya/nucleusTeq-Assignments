import { signIn } from "../actions/auth.js";
import { showFieldError, clearErrors } from "../lib/utils/ui.js";

const form = document.getElementById("loginForm");

form.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearErrors("loginForm");

    const msgEl = document.getElementById("loginMsg");
    msgEl.style.display = "none";

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    try {
        const data = await signIn(email, password);

        if (data.success && data.data) {
            localStorage.setItem("token", data.data.token);
            localStorage.setItem("role", data.data.role);
            localStorage.setItem("userId", data.data.userId);
            localStorage.setItem("userName", data.data.fullName);
            localStorage.setItem("email", email);
            localStorage.setItem("mobileNumber", data.data.mobileNumber); // Store for autofill
            if (data.data.profileId) {
                localStorage.setItem("profileId", data.data.profileId);
            }

            const role = data.data.role;
            const redirect = localStorage.getItem('redirectAfterLogin');

            if (role === "HR") window.location.href = "../hr-dashboard.html";
            else if (role === "PANEL") window.location.href = "../panel-dashboard.html";
            else {
                if (redirect) {
                    localStorage.removeItem('redirectAfterLogin');
                    window.location.href = "../" + redirect;
                } else {
                    window.location.href = "../dashboard.html";
                }
            }
        } else {
            // Mapping auth errors to fields
            if (data.message && data.message.toLowerCase().includes("user")) {
                showFieldError("email", data.message);
            } else if (data.message && data.message.toLowerCase().includes("password")) {
                showFieldError("password", data.message);
            } else {
                msgEl.className = "msg error";
                msgEl.textContent = data.message || "Login failed";
                msgEl.style.display = "block";
            }
        }
    } catch (err) {
        msgEl.className = "msg error";
        msgEl.textContent = "Server error. Please make sure the backend is running.";
        msgEl.style.display = "block";
    }
});
window.togglePassword = function(id, el) {
    const input = document.getElementById(id);
    if (input) {
        input.type = input.type === "password" ? "text" : "password";
    }
};
