const form = document.getElementById("loginForm");

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    
    document.querySelectorAll(".error").forEach(el => el.innerText = "");
    document.getElementById("loginMessage").innerText = "";

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    let isValid = true;

    // validation
    if (!email) {
        document.getElementById("emailError").innerText = "Email is required";
        isValid = false;
    }

    if (!password) {
        document.getElementById("passwordError").innerText = "Password is required";
        isValid = false;
    }

    if (!isValid) return;

    try {
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        // success
        if (data.success) {
            // store token
            localStorage.setItem("token", data.data.token);

            document.getElementById("loginMessage").className = "success";
            document.getElementById("loginMessage").innerText = "Login successful";

            // redirect (later dashboard)
            setTimeout(() => {
                window.location.href = "index.html";
            }, 1500);

        } else {
            document.getElementById("loginMessage").innerText = data.message;
        }

    } catch (error) {
        document.getElementById("loginMessage").innerText = "Server not reachable";
    }
});