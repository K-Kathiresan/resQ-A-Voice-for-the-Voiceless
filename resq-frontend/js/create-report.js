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

// ── UI-only: toggle between upload and camera panels ──────────
const toggleUpload   = document.getElementById("toggleUpload");
const uploadArea     = document.getElementById("uploadArea");
const cameraArea     = document.getElementById("cameraArea");

function setActiveToggle(active) {
    if (active === "upload") {
        toggleUpload.classList.add("toggle-btn--active");
        openCameraBtn.classList.remove("toggle-btn--active");
        uploadArea.style.display  = "block";
        cameraArea.style.display  = "none";
        // stop any running camera stream
        if (cameraStream) {
            cameraStream.getTracks().forEach(track => track.stop());
            cameraStream = null;
        }
    } else {
        openCameraBtn.classList.add("toggle-btn--active");
        toggleUpload.classList.remove("toggle-btn--active");
        uploadArea.style.display  = "none";
        cameraArea.style.display  = "block";
    }
}

toggleUpload.addEventListener("click", () => {
    setActiveToggle("upload");
});

// ── UI-only: image preview on file select ─────────────────────
const imgInput           = document.getElementById("img");
const imagePreviewWrapper = document.getElementById("imagePreviewWrapper");
const imagePreview       = document.getElementById("imagePreview");
const clearImageBtn      = document.getElementById("clearImageBtn");

imgInput.addEventListener("change", () => {
    const file = imgInput.files[0];
    if (file) {
        const url = URL.createObjectURL(file);
        imagePreview.src = url;
        imagePreviewWrapper.style.display = "block";
    }
});

clearImageBtn.addEventListener("click", () => {
    imgInput.value = "";
    imagePreview.src = "";
    imagePreviewWrapper.style.display = "none";
});

// ── Original: open camera ─────────────────────────────────────
openCameraBtn.addEventListener("click", async () => {

    setActiveToggle("camera");

    try {

        cameraStream =
            await navigator.mediaDevices.getUserMedia({
                video: true
            });

        cameraPreview.srcObject =
            cameraStream;

    } catch (error) {

        console.error(error);

        alert("Unable to access camera");

        setActiveToggle("upload");
    }
});

// ── Original: capture photo ───────────────────────────────────
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

        // UI-only: show preview of captured photo
        const url = URL.createObjectURL(blob);
        imagePreview.src = url;
        imagePreviewWrapper.style.display = "block";

        alert("Photo captured successfully");

    }, "image/jpeg");

    cameraStream.getTracks()
        .forEach(track => track.stop());

    cameraStream = null;

    // Return to upload panel so preview is visible
    setActiveToggle("upload");
});

// ── Original: GPS location ────────────────────────────────────
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

// ── Original: form submit ─────────────────────────────────────
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