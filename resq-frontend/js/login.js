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

        const token = response.data;

        localStorage.setItem("token", token);

        alert("Login Successful");

        window.location.href = "dashboard.html";
    }
});