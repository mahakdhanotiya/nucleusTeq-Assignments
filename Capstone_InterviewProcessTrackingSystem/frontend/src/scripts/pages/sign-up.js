import { SITE_CONFIG } from "../config/site-config.js";

const form = document.getElementById("registerForm");

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const msgEl = document.getElementById("registerMsg");
    msgEl.style.display = "none";

    const fullName = document.getElementById("fullName").value;
    const email = document.getElementById("email").value;
    const mobileNumber = document.getElementById("mobileNumber").value;
    const dateOfBirth = document.getElementById("dateOfBirth").value;
    const gender = document.getElementById("gender").value;
    const role = document.getElementById("role").value;

    try {
        const res = await fetch(SITE_CONFIG.apiUrl + "/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                fullName,
                email,
                mobileNumber,
                dateOfBirth: dateOfBirth || null,
                gender: gender || null,
                role: role
            })
        });
        const data = await res.json();

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
