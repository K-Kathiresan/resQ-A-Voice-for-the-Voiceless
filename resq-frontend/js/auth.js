function saveUserData(data) {

    localStorage.setItem("token", data.token);
    localStorage.setItem("role", data.role);
    localStorage.setItem("email", data.email);
    localStorage.setItem("name", data.name);
}

function logout() {

    localStorage.clear();

    window.location.href = "login.html";
}

function getToken() {
    return localStorage.getItem("token");
}

function getRole() {
    return localStorage.getItem("role");
}

function isLoggedIn() {
    return !!getToken();
}