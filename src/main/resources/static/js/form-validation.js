// Toggle Password Visibility
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    if (input) {
        input.type = input.type === "password" ? "text" : "password";
    }
}

// Register Form Validation
const registerForm = document.getElementById("registerForm");
if (registerForm) {
    registerForm.addEventListener("submit", function (e) {
        const username = document.getElementById("username");
        const email = document.getElementById("email");
        const password = document.getElementById("password");
        let isValid = true;

        // Username Validation
        if (username && (username.value.length < 4 || username.value.length > 20)) {
            username.classList.add("is-invalid");
            isValid = false;
        } else if (username) {
            username.classList.remove("is-invalid");
        }

        // Email Validation
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (email && !emailPattern.test(email.value)) {
            email.classList.add("is-invalid");
            isValid = false;
        } else if (email) {
            email.classList.remove("is-invalid");
        }

        // Password Validation
        if (password && password.value.length < 6) {
            password.classList.add("is-invalid");
            isValid = false;
        } else if (password) {
            password.classList.remove("is-invalid");
        }

        if (!isValid) {
            e.preventDefault(); // Prevent form submission if validation fails
        }
    });

    // Password Strength Indicator
    const passwordField = document.getElementById("password");
    if (passwordField) {
        passwordField.addEventListener("input", function () {
            const password = this.value;
            const strengthText = document.getElementById("passwordStrength");
            if (password.length < 6) {
                strengthText.textContent = "Weak";
                strengthText.style.color = "red";
            } else if (password.length < 10) {
                strengthText.textContent = "Medium";
                strengthText.style.color = "orange";
            } else {
                strengthText.textContent = "Strong";
                strengthText.style.color = "green";
            }
        });
    }
});
