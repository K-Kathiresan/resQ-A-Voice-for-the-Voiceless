const reportForm = document.getElementById("reportForm");

const openCameraBtn =
    document.getElementById("openCameraBtn");

const cameraPreview =
    document.getElementById("cameraPreview");

const captureBtn =
    document.getElementById("captureBtn");

const photoCanvas =
    document.getElementById("photoCanvas");

const getLocationBtn =
    document.getElementById("getLocationBtn");

let cameraStream;

openCameraBtn.addEventListener("click", async () => {

    try {

        cameraStream =
            await navigator.mediaDevices.getUserMedia({
                video: true
            });

        cameraPreview.srcObject =
            cameraStream;

        cameraPreview.style.display =
            "block";

        captureBtn.style.display =
            "block";

    } catch (error) {

        console.error(error);

        alert("Unable to access camera");
    }
});

captureBtn.addEventListener("click", () => {

    const context =
        photoCanvas.getContext("2d");

    photoCanvas.width =
        cameraPreview.videoWidth;

    photoCanvas.height =
        cameraPreview.videoHeight;

    context.drawImage(
        cameraPreview,
        0,
        0,
        photoCanvas.width,
        photoCanvas.height
    );

    const imageInput =
        document.getElementById("img");

    photoCanvas.toBlob((blob) => {

        const capturedFile =
            new File(
                [blob],
                "captured-animal.jpg",
                {
                    type: "image/jpeg"
                }
            );

        const dataTransfer =
            new DataTransfer();

        dataTransfer.items.add(capturedFile);

        imageInput.files =
            dataTransfer.files;

        alert("Photo captured successfully");

    }, "image/jpeg");

    cameraStream.getTracks()
        .forEach(track => track.stop());

    cameraPreview.style.display =
        "none";

    captureBtn.style.display =
        "none";
});

getLocationBtn.addEventListener("click", () => {

    if (!navigator.geolocation) {

        alert("Geolocation is not supported");

        return;
    }

    navigator.geolocation.getCurrentPosition(

        (position) => {

            const latitude =
                position.coords.latitude;

            const longitude =
                position.coords.longitude;

            document.getElementById("location").value =
                `${latitude}, ${longitude}`;

            alert("Location captured successfully");
        },

        (error) => {

            console.error(error);

            alert("Unable to fetch location");
        }

    );
});

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

    if (!image) {

        alert("Please capture or select an image");

        return;
    }

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

        window.location.href =
            "dashboard.html";
    }
});