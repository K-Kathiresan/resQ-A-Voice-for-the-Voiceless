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

    const assigned =
        reports.filter(
            r => r.status === "ASSIGNED"
        ).length;

    const active =
        reports.filter(
            r =>
                r.status === "ON_THE_WAY" ||
                r.status === "RESCUING"
        ).length;

    const completed =
        reports.filter(
            r =>
                r.status === "RESCUED" ||
                r.status === "FAILED"
        ).length;

    document.getElementById(
        "assignedCount"
    ).innerText = assigned;

    document.getElementById(
        "activeCount"
    ).innerText = active;

    document.getElementById(
        "completedCount"
    ).innerText = completed;

    reports.forEach((report) => {

        const statusClass =
            `status-${report.status.toLowerCase()}`;

        const isCompleted =
            report.status === "RESCUED" ||
            report.status === "FAILED";

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
                    <button onclick="openLocation('${report.location}')">
                        Open Location
                    </button>

                <div class="status-badge ${statusClass}">
                    ${report.status}
                </div>

                ${isCompleted ? `

                <div class="rescue-note-section">

                    <h4>Rescue Note</h4>

                    <textarea
                        id="note-${report.id}"
                        placeholder="Enter rescue summary..."
                    >${report.rescueNote || ""}</textarea>

                    <button
                        onclick="saveRescueNote(${report.id})"
                    >
                        Save Note
                    </button>

                </div>

            ` : ""}

                <div class="button-group">

                    <button
                        class="success-btn"
                        onclick="updateStatus(${report.id}, '${report.status}')"
                        ${isCompleted ? "disabled" : ""}
                    >
                        ${isCompleted ? "Completed" : "Next Status"}
                    </button>

                    ${!isCompleted ? `

                        <button
                            class="failed-btn"
                            onclick="markAsFailed(${report.id})"
                        >
                            Mark Failed
                        </button>

                    ` : ""}

                </div>

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

        const button =
            event.target;

        button.disabled = true;

        button.innerText = "Updating...";

        button.classList.add("loading-btn");

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

async function markAsFailed(reportId) {

    try {

        const token = localStorage.getItem("token");

        const response = await fetch(
            `${BASE_URL}/api/volunteer/reports/${reportId}/status`,
            {
                method: "PUT",

                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },

                body: JSON.stringify({
                    status: "FAILED"
                })
            }
        );

        if (!response.ok) {

            throw new Error("Failed to mark report as FAILED");

        }

        await fetchAssignedReports();

    } catch (error) {

        console.error(error);

        alert("Failed to update report");

    }

}

async function saveRescueNote(reportId) {

    try {

        const token =
            localStorage.getItem("token");

        const rescueNote =
            document.getElementById(
                `note-${reportId}`
            ).value;

        const response = await fetch(
            `${BASE_URL}/api/volunteer/reports/${reportId}/note`,
            {
                method: "PUT",

                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },

                body: JSON.stringify({
                    rescueNote: rescueNote
                })
            }
        );

        if (!response.ok) {

            throw new Error(
                "Failed to save rescue note"
            );
        }

        alert("Rescue note saved successfully");

        await fetchAssignedReports();

    } catch (error) {

        console.error(error);

        alert("Failed to save rescue note");
    }
}

function openLocation(location) {
    const mapsUrl =
        `https://www.google.com/maps/dir/?api=1&destination=${location}`;

    window.open(mapsUrl, "_blank");
}

fetchAssignedReports();