const reportsContainer = document.getElementById("reportsContainer");

const logoutBtn = document.getElementById("logoutBtn");

const BASE_URL = "http://localhost:8080";

const token = localStorage.getItem("token");

let volunteers = [];


let reportsData = [];

const reportModal =
    document.getElementById("reportModal");

const modalBody =
    document.getElementById("modalBody");

const closeModal =
    document.getElementById("closeModal");

if (!token) {

    window.location.href = "login.html";
}

logoutBtn.addEventListener("click", () => {

    localStorage.removeItem("token");

    window.location.href = "login.html";
});

async function fetchVolunteers() {

    try {

        const response = await fetch(
            `${BASE_URL}/api/admin/volunteers`,
            {
                method: "GET",

                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        if (!response.ok) {

            throw new Error(`HTTP Error: ${response.status}`);
        }

        const data = await response.json();

        console.log("Volunteer Response:", data);

        volunteers = data;

    } catch (error) {

        console.error("Error fetching volunteers:", error);
    }
}

async function fetchReports() {

    try {

        const response = await fetch(
            `${BASE_URL}/api/admin/reports`,
            {
                method: "GET",

                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        if (!response.ok) {

            throw new Error(`HTTP Error: ${response.status}`);
        }

        const data = await response.json();

        reportsData = data;

        console.log("Reports Response:", data);

        renderReports(data);

    } catch (error) {

        console.error("Error fetching reports:", error);
    }
}

function renderReports(reports) {

    reportsContainer.innerHTML = "";

    reports.forEach(report => {

        const isRescued =
        report.status === "RESCUED";

        const isClosedCase =
        report.status === "RESCUED" ||
        report.status === "FAILED";

        const reportCard = document.createElement("div");

        const statusLabels = {
            PENDING: "Pending Review",
            ASSIGNED: "Volunteer Assigned",
            ON_THE_WAY: "Volunteer En Route",
            RESCUING: "Rescue In Progress",
            RESCUED: "Rescue Successful",
            FAILED: "Rescue Failed"
        };

        reportCard.classList.add("report-card");

        reportCard.innerHTML = `
        
            <img
                src="${report.imageUrl}"
                alt="Animal"
                class="report-image"
            >

            <div class="report-content">

                <h3>${report.animalType}</h3>

                <span class="case-badge">
                ${isClosedCase ? "CLOSED CASE" : "ACTIVE CASE"}
                </span>

                <p>
                    ${report.description}
                </p>

                <p>
                    <strong>Location:</strong>
                    ${report.location}
                </p>

                <span class="status-badge ${report.status.toLowerCase()}">
                    ${statusLabels[report.status]}
                </span>

                <p class="assigned-volunteer">

                    Assigned Volunteer:

                    <strong>
                        ${report.assignedVolunteer?.name || "Not Assigned"}
                    </strong>

                </p>
                                ${report.rescueNote ? `

                    <p class="rescue-note">

                        <strong>Rescue Note:</strong>

                        ${report.rescueNote}

                    </p>

                ` : ""}

                <div class="assignment-section">

                    <select
                        class="volunteer-select"
                        ${isRescued ? "disabled" : ""}
                    >

                        <option value="">
                            Select Volunteer
                        </option>

                        ${(volunteers || []).map(volunteer => `

                            <option
                                value="${volunteer.id}"
                                ${report.assignedVolunteer?.id === volunteer.id
                                    ? "selected"
                                    : ""}
                            >

                                ${volunteer.name}

                            </option>

                        `).join("")}

                    </select>

                    <button
                        onclick="assignVolunteer(${report.id}, this)"
                        ${isRescued ? "disabled" : ""}
                    >
                        ${isRescued ? "Rescue Completed" : "Assign"}
                    </button>

                    <button
                        class="details-btn"
                        onclick="openReportModal(${report.id})"
                    >
                        View Details
                    </button>

                </div>

            </div>
        `;

        reportsContainer.appendChild(reportCard);
    });
}

function openReportModal(reportId) {

    const report =
        reportsData.find(r => r.id === reportId);

    if (!report) {
        return;
    }

    modalBody.innerHTML = `
        <h2>${report.animalType}</h2>

        <img
            src="${report.imageUrl}"
            alt="Animal"
            style="width:100%; max-height:300px; object-fit:cover;"
        >

        <p>
            <strong>Description:</strong>
            ${report.description}
        </p>

        <p>
            <strong>Location:</strong>
            ${report.location}
        </p>

        <p>
            <strong>Status:</strong>
            ${report.status}
        </p>

        <p>
            <strong>Assigned Volunteer:</strong>
            ${report.assignedVolunteer?.name || "Not Assigned"}
        </p>
                ${report.rescueNote ? `

        <p>
            <strong>Rescue Note:</strong>
            ${report.rescueNote}
        </p>

        ` : ""}
    `;

    reportModal.style.display = "block";
}

async function assignVolunteer(reportId, buttonElement) {

    try {

        const parent =
            buttonElement.parentElement;

        const select =
            parent.querySelector(".volunteer-select");

        const volunteerId = select.value;

        if (!volunteerId) {

            alert("Please select a volunteer");

            return;
        }

        buttonElement.disabled = true;

        buttonElement.innerText = "Assigning...";

        const response = await fetch(
            `${BASE_URL}/api/admin/reports/${reportId}/assign/${volunteerId}`,
            {
                method: "PUT",

                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        if (!response.ok) {

            throw new Error(`HTTP Error: ${response.status}`);
        }

        alert("Volunteer assigned successfully");

        await fetchReports();

    } catch (error) {

        console.error("Assignment error:", error);

        alert("Failed to assign volunteer");
    }
}

async function init() {

    await fetchVolunteers();

    await fetchReports();
}
closeModal.addEventListener("click", () => {

    reportModal.style.display = "none";
});

window.addEventListener("click", (event) => {

    if (event.target === reportModal) {

        reportModal.style.display = "none";
    }
});
init();