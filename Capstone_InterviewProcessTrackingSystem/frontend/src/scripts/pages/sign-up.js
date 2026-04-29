import { signUp } from "../actions/auth.js";

const form = document.getElementById("registerForm");

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const msgEl = document.getElementById("registerMsg");
    msgEl.style.display = "none";

    const fullName = document.getElementById("fullName").value.trim();
    const email = document.getElementById("email").value.trim();
    const mobileNumber = document.getElementById("mobileNumber").value.trim();
    const dateOfBirth = document.getElementById("dateOfBirth").value;
    const gender = document.getElementById("gender").value;
    const role = "CANDIDATE";

    try {
        const data = await signUp(fullName, email, mobileNumber, dateOfBirth, gender, role);

        if (data.success) {
            msgEl.className = "msg success";
            msgEl.textContent = data.message || "Account created! Check your email to set your password.";
            msgEl.style.display = "block";
        } else {
            msgEl.className = "msg error";
            msgEl.textContent = data.message || "Registration failed";
            msgEl.style.display = "block";
        }
    } catch (err) {
        msgEl.className = "msg error";
        msgEl.textContent = "Server error. Please make sure the backend is running.";
        msgEl.style.display = "block";
    }
});
