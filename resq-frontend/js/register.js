const registerForm = document.getElementById("registerForm");

registerForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const userData = {

        name: document.getElementById("name").value,

        email: document.getElementById("email").value,

        password: document.getElementById("password").value,

        role: document.getElementById("role").value
    };

    const response = await apiRequest(
        "/auth/register",
        "POST",
        userData
    );

    if (response) {

        alert("Registration successful!");

        window.location.href = "login.html";
    }
});