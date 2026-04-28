import { signIn } from "../actions/auth.js";

const form = document.getElementById("loginForm");

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const msgEl = document.getElementById("loginMsg");
    msgEl.style.display = "none";

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const data = await signIn(email, password);

        if (data.success && data.data) {
            localStorage.setItem("token", data.data.token);
            localStorage.setItem("role", data.data.role);
            localStorage.setItem("userId", data.data.userId);

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
            msgEl.className = "msg error";
            msgEl.textContent = data.message || "Login failed";
            msgEl.style.display = "block";
        }
    } catch (err) {
        msgEl.className = "msg error";
        msgEl.textContent = "Server error. Please make sure the backend is running.";
        msgEl.style.display = "block";
    }
});
