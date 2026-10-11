const loginForm = document.getElementById("loginForm");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const loginButton = document.getElementById("loginButton");
const loginMessage = document.getElementById("loginMessage");
const passwordToggle = document.getElementById("passwordToggle");


// ===============================
// SHOW / HIDE PASSWORD
// ===============================

passwordToggle.addEventListener("click", function () {

    const isPassword = passwordInput.type === "password";

    passwordInput.type = isPassword ? "text" : "password";

    passwordToggle.innerHTML = isPassword
        ? '<i class="bi bi-eye-slash"></i>'
        : '<i class="bi bi-eye"></i>';
});


// ===============================
// SHOW MESSAGE
// ===============================

function showMessage(message, type) {

    loginMessage.textContent = message;
    loginMessage.className = "login-message " + type;
}


// ===============================
// RESET BUTTON
// ===============================

function resetButton() {

    loginButton.disabled = false;

    loginButton.innerHTML =
        '<span>Sign In</span>' +
        '<i class="bi bi-arrow-right"></i>';
}


// ===============================
// LOGIN
// ===============================

loginForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const email = emailInput.value.trim();
    const password = passwordInput.value;

    if (email === "" || password === "") {

        showMessage(
            "Please enter your email and password.",
            "error"
        );

        return;
    }


    loginButton.disabled = true;

    loginButton.innerHTML =
        '<span>Signing in...</span>' +
        '<i class="bi bi-arrow-repeat"></i>';


    try {

        const url =
            "/api/auth/login?email=" +
            encodeURIComponent(email) +
            "&password=" +
            encodeURIComponent(password);


        const response = await fetch(url, {
            method: "POST"
        });


        if (!response.ok) {

            const errorText = await response.text();

            throw new Error(
                errorText || "Invalid email or password."
            );
        }


        const user = await response.json();


        // Save user information
        sessionStorage.setItem(
            "loggedInUser",
            JSON.stringify(user)
        );


        showMessage(
            "Login successful. Redirecting...",
            "success"
        );


        // ===============================
        // ROLE BASED REDIRECTION
        // ===============================

        setTimeout(function () {

            if (user.role === "PATIENT") {

                window.location.href =
                    "/patient/dashboard";

            } else if (user.role === "DOCTOR") {

                window.location.href =
                    "/doctor/dashboard";

            } else if (user.role === "ADMIN") {

                window.location.href =
                    "/admin/dashboard";

            } else {

                showMessage(
                    "Unknown user role.",
                    "error"
                );

                resetButton();
            }

        }, 700);


    } catch (error) {

        console.error("Login error:", error);

        showMessage(
            error.message || "Login failed. Please try again.",
            "error"
        );

        resetButton();
    }

});