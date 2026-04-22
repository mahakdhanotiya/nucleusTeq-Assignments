const form = document.getElementById("registerForm");

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    
    document.querySelectorAll(".error").forEach(el => el.innerText = "");
    document.getElementById("successMessage").innerText = "";

    const fullName = document.getElementById("fullName").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    let isValid = true;

    // validations
    if (!fullName) {
        document.getElementById("nameError").innerText = "Full name is required";
        isValid = false;
    }

    if (!email) {
        document.getElementById("emailError").innerText = "Email is required";
        isValid = false;
    }

    if (!password) {
        document.getElementById("passwordError").innerText = "Password is required";
        isValid = false;
    } else if (password.length < 6) {
        document.getElementById("passwordError").innerText = "Minimum 6 characters required";
        isValid = false;
    }

    if (!confirmPassword) {
        document.getElementById("confirmError").innerText = "Confirm your password";
        isValid = false;
    } else if (password !== confirmPassword) {
        document.getElementById("confirmError").innerText = "Passwords do not match";
        isValid = false;
    }

    if (!isValid) return;

    try {
        const response = await fetch("http://localhost:8080/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ fullName, email, password })
        });

        const data = await response.json();

        if (data.success) {
            document.getElementById("successMessage").innerText = "Registered successfully ";

            form.reset();
            setTimeout(() => {
        window.location.href = "index.html";
        }, 1500);

        } else {
            document.getElementById("emailError").innerText = data.message;
        }

    } catch (error) {
        document.getElementById("emailError").innerText = "Server not reachable";
    }
});