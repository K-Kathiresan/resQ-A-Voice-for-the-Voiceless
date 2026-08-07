const reportsContainer = document.getElementById("reportsContainer");

const totalReports = document.getElementById("totalReports");
const pendingReports = document.getElementById("pendingReports");
const rescuedReports = document.getElementById("rescuedReports");
const failedReports = document.getElementById("failedReports");

const token = localStorage.getItem("token");

if (!token) {

    alert("Please login first");

    window.location.href = "login.html";

}

function getStatusClass(status) {

    return `status-${status.toLowerCase()}`;

}

async function loadMyReports() {

    try {

        const response = await fetch(
            "http://localhost:8080/api/reports/my-reports",
            {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        const result = await response.json();

        const reports = result.data || [];

        reportsContainer.innerHTML = "";

        totalReports.textContent = reports.length;

        pendingReports.textContent =
            reports.filter(r => r.status === "PENDING").length;

        rescuedReports.textContent =
            reports.filter(r => r.status === "RESCUED").length;

        failedReports.textContent =
            reports.filter(r => r.status === "FAILED").length;

        reports.forEach(report => {

            const reportCard = document.createElement("div");

            reportCard.classList.add("report-card");

            const statusClass =
                getStatusClass(report.status);

            reportCard.innerHTML = `

                <img
                    src="${report.imageUrl}"
                    alt="Animal Image"
                >

                <div class="report-content">

                    <h3>${report.animalType}</h3>

                    <div class="status-badge ${statusClass}">
                        ${report.status}
                    </div>

                    <p>
                        <strong>Description:</strong>
                        ${report.description}
                    </p>

                    <p>
                        <strong>Location:</strong>
                        ${report.location}
                    </p>

                    ${report.assignedVolunteerName ? `

                        <p>
                            <strong>Volunteer:</strong>
                            ${report.assignedVolunteerName}
                        </p>

                    ` : ""}

                    ${report.rescueNote ? `

                        <div class="outcome-card">

                            <h4>Rescue Note</h4>

                            <p>${report.rescueNote}</p>

                        </div>

                    ` : ""}

                </div>

            `;

            reportsContainer.appendChild(reportCard);
        });

    } catch (error) {

        console.error(error);

        reportsContainer.innerHTML =
            "<p>Failed to load reports</p>";
    }

}

loadMyReports();