const reportForm = document.getElementById("reportForm");

reportForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const animalType =
        document.getElementById("animalType").value;

    const description =
        document.getElementById("description").value;

    const location =
        document.getElementById("location").value;

    const image =
        document.getElementById("img").files[0];

    const formData = new FormData();

    formData.append("animalType", animalType);

    formData.append("description", description);

    formData.append("location", location);

    formData.append("image", image);

    const response = await apiRequest(
        "/reports",
        "POST",
        formData,
        true
    );

    console.log(response);

    if (response && response.success) {

        alert("Report Created Successfully");

        window.location.href = "dashboard.html";
    }
});