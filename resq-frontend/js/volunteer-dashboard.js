const logoutBtn = document.getElementById("logoutBtn");

const assignedReportsContainer =
    document.getElementById("assignedReportsContainer");

logoutBtn.addEventListener("click", () => {

    localStorage.removeItem("token");
    localStorage.removeItem("role");

    window.location.href = "login.html";

});

const reports = [

    {
        title: "Dog injured near bus stand",
        description: "Dog unable to walk properly",
        location: "Chennai",
        status: "ASSIGNED",
        imageUrl: "https://placehold.co/300x200"
    },

    {
        title: "Cat stuck in drainage",
        description: "Cat crying continuously",
        location: "Coimbatore",
        status: "ON_THE_WAY",
        imageUrl: "https://placehold.co/300x200"
    }

];

function renderReports() {

    assignedReportsContainer.innerHTML = "";

    reports.forEach((report, index) => {

        assignedReportsContainer.innerHTML += `

            <div class="report-card">

                <img
                    src="${report.imageUrl}"
                    alt="Animal Image"
                >

                <h3>${report.title}</h3>

                <p>${report.description}</p>

                <p>
                    <strong>Location:</strong>
                    ${report.location}
                </p>

                <p>
                    <strong>Status:</strong>
                    ${report.status}
                </p>

                <button onclick="updateStatus(${index})">
                    Update Status
                </button>

            </div>

        `;
    });
}

function updateStatus(index) {

    const currentStatus = reports[index].status;

    if (currentStatus === "ASSIGNED") {

        reports[index].status = "ON_THE_WAY";

    } else if (currentStatus === "ON_THE_WAY") {

        reports[index].status = "RESCUING";

    } else if (currentStatus === "RESCUING") {

        reports[index].status = "RESCUED";

    }

    renderReports();
}

renderReports();