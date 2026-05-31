const reportsContainer = document.getElementById("reportsContainer");

const token = localStorage.getItem("token");

if (!token) {
    alert("Please login first");
    window.location.href = "login.html";
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

        console.log(result);

        const reports = result.data;

        reportsContainer.innerHTML = "";

        reports.forEach(report => {

            const reportCard = document.createElement("div");

            reportCard.classList.add("report-card");

            // USE DATABASE URL DIRECTLY
            const imageUrl = report.imageUrl;

            console.log(imageUrl);

            reportCard.innerHTML = `
                <img
                    src="${imageUrl}"
                    alt="Animal Image"
                    width="300"
                >

                <h2>${report.animalType}</h2>

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
            `;

            reportsContainer.appendChild(reportCard);
        });

    } catch (error) {

        console.error("Error loading reports:", error);

        reportsContainer.innerHTML =
            "<p>Failed to load reports</p>";
    }
}

loadMyReports();