const logoutBtn = document.getElementById("logoutBtn");

const assignedReportsContainer =
document.getElementById("assignedReportsContainer");

const BASE_URL = "http://localhost:8080";

logoutBtn.addEventListener("click", () => {


localStorage.removeItem("token");
localStorage.removeItem("role");

window.location.href = "login.html";


});

async function fetchAssignedReports() {


try {

    const token = localStorage.getItem("token");

    if (!token) {

        alert("Please login first");

        window.location.href = "login.html";

        return;

    }

    const response = await fetch(
        `${BASE_URL}/api/volunteer/reports`,
        {
            method: "GET",

            headers: {
                Authorization: `Bearer ${token}`
            }
        }
    );

    if (!response.ok) {

        throw new Error("Failed to fetch reports");

    }

    const data = await response.json();

    renderReports(data);

} catch (error) {

    console.error(error);

    assignedReportsContainer.innerHTML = `
        <p>Failed to load assigned reports.</p>
    `;

}


}

function renderReports(reports) {

assignedReportsContainer.innerHTML = "";

if (reports.length === 0) {

    assignedReportsContainer.innerHTML = `
        <p>No assigned reports found.</p>
    `;

    return;

}

reports.forEach((report) => {

    assignedReportsContainer.innerHTML += `

        <div class="report-card">

            <img
                src="${report.imageUrl}"
                alt="Animal Image"
            >

            <h3>${report.animalType}</h3>

            <p>${report.description}</p>

            <p>
                <strong>Location:</strong>
                ${report.location}
            </p>

            <p>
                <strong>Status:</strong>
                ${report.status}
            </p>

            <button onclick="updateStatus(${report.id}, '${report.status}')">
                Update Status
            </button>

        </div>

    `;

});


}

function getNextStatus(currentStatus) {


if (currentStatus === "ASSIGNED") {

    return "ON_THE_WAY";

}

if (currentStatus === "ON_THE_WAY") {

    return "RESCUING";

}

if (currentStatus === "RESCUING") {

    return "RESCUED";

}

return currentStatus;


}

async function updateStatus(reportId, currentStatus) {


try {

    const token = localStorage.getItem("token");

    const nextStatus = getNextStatus(currentStatus);

    const response = await fetch(
        `${BASE_URL}/api/volunteer/reports/${reportId}/status`,
        {
            method: "PUT",

            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`
            },

            body: JSON.stringify({
                status: nextStatus
            })
        }
    );

    if (!response.ok) {

        throw new Error("Failed to update status");

    }

    await fetchAssignedReports();

} catch (error) {

    console.error(error);

    alert("Status update failed");

}


}


fetchAssignedReports();
