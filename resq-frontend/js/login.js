const loginForm = document.getElementById("loginForm");

loginForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const email =
        document.getElementById("email").value;

    const password =
        document.getElementById("password").value;

    const loginData = {
        email,
        password
    };

    const response = await apiRequest(
        "/auth/login",
        "POST",
        loginData
    );

    console.log(response);

    if (response && response.success) {

        const token = response.data.token;
        const role = response.data.role;

        localStorage.setItem("token", token);
        localStorage.setItem("role", role);

        alert("Login Successful");

        if (role === "CITIZEN") {

            window.location.href = "my-reports.html";

        } else if (role === "VOLUNTEER") {

            window.location.href = "volunteer-dashboard.html";

        } else if (role === "ADMIN") {

            window.location.href = "admin-dashboard.html";

        }
    }
});