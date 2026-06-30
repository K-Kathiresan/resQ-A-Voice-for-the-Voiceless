const reportsContainer = document.getElementById("reportsContainer");

const logoutBtn = document.getElementById("logoutBtn");

const BASE_URL = "http://localhost:8080";

const token = localStorage.getItem("token");

const totalReports =
    document.getElementById("totalReports");

const activeCases =
    document.getElementById("activeCases");

const closedCases =
    document.getElementById("closedCases");

const successRate =
    document.getElementById("successRate");

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

        const total = data.length;

        const active =
            data.filter(r =>
                r.status === "PENDING" ||
                r.status === "ASSIGNED" ||
                r.status === "ON_THE_WAY" ||
                r.status === "RESCUING"
            ).length;

        const closed =
            data.filter(r =>
                r.status === "RESCUED" ||
                r.status === "FAILED"
            ).length;

        const rescued =
            data.filter(r =>
                r.status === "RESCUED"
            ).length;

        const failed =
            data.filter(r =>
                r.status === "FAILED"
            ).length;

        const rate =
            (rescued + failed) === 0
                ? 0
                : Math.round((rescued / (rescued + failed)) * 100);

        totalReports.textContent = total;
        activeCases.textContent = active;
        closedCases.textContent = closed;
        successRate.textContent = `${rate}%`;

        console.log("Reports Response:", data);
        renderReports(data);

    } catch (error) {
        console.error("Error fetching reports:", error);
    }
}

function updateReportsCountBadge(count) {
    const badge = document.getElementById("reportsCountBadge");
    if (badge) {
        badge.textContent = `${count} report${count !== 1 ? "s" : ""}`;
    }
}

function renderReports(reports) {
    reportsContainer.innerHTML = "";
    updateReportsCountBadge(reports.length);

    reports.forEach(report => {

        const isRescued =
            report.status === "RESCUED";

        const isClosedCase =
            report.status === "RESCUED" ||
            report.status === "FAILED";

        const reportCard = document.createElement("div");

        const statusLabels = {
            PENDING:    "Pending Review",
            ASSIGNED:   "Volunteer Assigned",
            ON_THE_WAY: "Volunteer En Route",
            RESCUING:   "Rescue In Progress",
            RESCUED:    "Rescue Successful",
            FAILED:     "Rescue Failed"
        };

        reportCard.classList.add("report-card");

        reportCard.innerHTML = `
            <img
                src="${report.imageUrl}"
                alt="Animal"
                class="report-image"
            >

            <div class="report-content">

                <div class="report-header">
                    <h3>${report.animalType}</h3>
                    <span class="case-badge ${isClosedCase ? "closed-case" : "active-case"}">
                        ${isClosedCase ? "Closed Case" : "Active Case"}
                    </span>
                </div>

                <span class="status-badge ${report.status.toLowerCase()}">
                    ${statusLabels[report.status]}
                </span>

                <p class="report-description">
                    ${report.description}
                </p>

                <div class="report-location">
                    <svg class="location-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                        <circle cx="12" cy="10" r="3"></circle>
                    </svg>
                    <span>${report.location}</span>
                </div>

                <div class="action-row">
                    <button
                        class="map-btn"
                        onclick="openMap('${report.location}')"
                    >
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polygon points="3 11 22 2 13 21 11 13 3 11"></polygon>
                        </svg>
                        Open Map
                    </button>
                </div>

                <div class="assigned-volunteer">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                    <span>Assigned Volunteer: <strong>${report.assignedVolunteer?.name || "Not Assigned"}</strong></span>
                </div>

                ${report.rescueNote ? `
                    <div class="rescue-note">
                        <strong>Rescue Note</strong>
                        ${report.rescueNote}
                    </div>
                ` : ""}

                <div class="assignment-section">

                    <select
                        class="volunteer-select"
                        ${isRescued ? "disabled" : ""}
                    >
                        <option value="">Select Volunteer</option>
                        ${(volunteers || []).map(volunteer => `
                            <option
                                value="${volunteer.id}"
                                ${report.assignedVolunteer?.id === volunteer.id ? "selected" : ""}
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

function applyFilters() {
    const searchValue =
        searchInput.value.toLowerCase();

    const selectedStatus =
        statusFilter.value;

    const filteredReports =
        reportsData.filter(report => {

            const animalMatch =
                report.animalType
                    .toLowerCase()
                    .includes(searchValue);

            const statusMatch =
                selectedStatus === "ALL" ||
                report.status === selectedStatus;

            return animalMatch && statusMatch;
        });

    renderReports(filteredReports);
}

function openReportModal(reportId) {
    const report =
        reportsData.find(r => r.id === reportId);

    if (!report) return;

    modalBody.innerHTML = `
        <h2>${report.animalType}</h2>

        <img
            src="${report.imageUrl}"
            alt="Animal"
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

    searchInput.addEventListener("input", applyFilters);
    statusFilter.addEventListener("change", applyFilters);
}

closeModal.addEventListener("click", () => {
    reportModal.style.display = "none";
});

window.addEventListener("click", (event) => {
    if (event.target === reportModal) {
        reportModal.style.display = "none";
    }
});

function openMap(location) {
    const url =
        `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(location)}`;
    window.open(url, "_blank");
}

init();