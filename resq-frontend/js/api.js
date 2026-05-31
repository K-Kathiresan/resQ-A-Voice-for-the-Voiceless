const BASE_URL = "http://localhost:8080/api";

async function apiRequest(
    endpoint,
    method = "GET",
    body = null,
    isFormData = false
) {

    const token = localStorage.getItem("token");

    const headers = {};

    if (!isFormData) {
        headers["Content-Type"] = "application/json";
    }

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const config = {
        method: method,
        headers: headers
    };

    if (body) {
        config.body = isFormData
            ? body
            : JSON.stringify(body);
    }

    try {

        const response = await fetch(
            `${BASE_URL}${endpoint}`,
            config
        );

        // Handle unauthorized
        if (response.status === 401) {

            alert("Session expired. Please login again.");

            localStorage.clear();

            window.location.href = "login.html";

            return;
        }

        // Read response safely
        const text = await response.text();

        // Convert to JSON only if response exists
        const data = text ? JSON.parse(text) : {};

        if (!response.ok) {

            throw new Error(
                data.message || "Something went wrong"
            );
        }

        return data;

    } catch (error) {

        console.error("API Error:", error.message);

        alert(error.message);
    }
}