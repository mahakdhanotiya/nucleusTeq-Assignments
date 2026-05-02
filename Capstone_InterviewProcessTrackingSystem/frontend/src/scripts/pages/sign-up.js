import { signUp } from "../actions/auth.js";
import { showFieldError, clearErrors } from "../lib/utils/ui.js";

const form = document.getElementById("registerForm");

form.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearErrors("registerForm");

    const msgEl = document.getElementById("registerMsg");
    msgEl.style.display = "none";

    const fullName = document.getElementById("fullName").value.trim();
    const email = document.getElementById("email").value.trim();
    const mobileNumber = document.getElementById("mobileNumber").value.trim();
    const dateOfBirth = document.getElementById("dateOfBirth").value;
    const gender = document.getElementById("gender").value;
    const role = "CANDIDATE";

    let hasError = false;

    // Name Validation: Only alphabets and spaces
    const nameRegex = /^[a-zA-Z\s.-]+$/;
    if (!nameRegex.test(fullName)) {
        showFieldError("fullName", "Name should contain only alphabets and spaces.");
        hasError = true;
    }

    // Mobile Number Validation: Exactly 10 digits, numbers only
    const mobileRegex = /^[0-9]{10}$/;
    if (!mobileRegex.test(mobileNumber)) {
        showFieldError("mobileNumber", "Mobile number must be exactly 10 digits.");
        hasError = true;
    }

    if (hasError) return;

    try {
        const data = await signUp(fullName, email, mobileNumber, dateOfBirth, gender, role);

        if (data.success) {
            msgEl.className = "msg success";
            msgEl.textContent = data.message || "Account created! Check your email to set your password.";
            msgEl.style.display = "block";
            form.reset();
        } else {
            // If backend provides field-specific errors, map them
            if (data.message && data.message.toLowerCase().includes("email")) {
                showFieldError("email", data.message);
            } else if (data.message && data.message.toLowerCase().includes("mobile")) {
                showFieldError("mobileNumber", data.message);
            } else {
                msgEl.className = "msg error";
                msgEl.textContent = data.message || "Registration failed";
                msgEl.style.display = "block";
            }
        }
    } catch (err) {
        msgEl.className = "msg error";
        msgEl.textContent = "Server error. Please make sure the backend is running.";
        msgEl.style.display = "block";
    }
});
