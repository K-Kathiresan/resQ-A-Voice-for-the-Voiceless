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

const analyzeAiBtn =
    document.getElementById("analyzeAiBtn");

const aiLoading =
    document.getElementById("aiLoading");

const aiLoadingText =
    document.getElementById("aiLoadingText");

const aiResult =
    document.getElementById("aiResult");

const aiUrgency =
    document.getElementById("aiUrgency");

const aiUrgencyLevel =
    document.getElementById("aiUrgencyLevel");

const aiUrgencyMessage =
    document.getElementById("aiUrgencyMessage");

const aiMainActions =
    document.getElementById("aiMainActions");

const aiWarningText =
    document.getElementById("aiWarningText");

const aiDisclaimer =
    document.getElementById("aiDisclaimer");

let cameraStream;
let analyzedUrgencyLevel = null;

// ── UI-only: toggle between upload and camera panels ──────────

const toggleUpload =
    document.getElementById("toggleUpload");

const uploadArea =
    document.getElementById("uploadArea");

const cameraArea =
    document.getElementById("cameraArea");


function setActiveToggle(active) {

    if (active === "upload") {

        toggleUpload.classList.add("toggle-btn--active");

        openCameraBtn.classList.remove(
            "toggle-btn--active"
        );

        uploadArea.style.display = "block";

        cameraArea.style.display = "none";


        if (cameraStream) {

            cameraStream.getTracks().forEach(
                track => track.stop()
            );

            cameraStream = null;
        }

    } else {

        openCameraBtn.classList.add(
            "toggle-btn--active"
        );

        toggleUpload.classList.remove(
            "toggle-btn--active"
        );

        uploadArea.style.display = "none";

        cameraArea.style.display = "block";
    }
}


toggleUpload.addEventListener("click", () => {

    setActiveToggle("upload");

});


// ── Image preview ──────────────────────────────────────────────

const imgInput =
    document.getElementById("img");

const imagePreviewWrapper =
    document.getElementById("imagePreviewWrapper");

const imagePreview =
    document.getElementById("imagePreview");

const clearImageBtn =
    document.getElementById("clearImageBtn");


imgInput.addEventListener("change", () => {

    const file =
        imgInput.files[0];

    if (file) {

        const url =
            URL.createObjectURL(file);

        imagePreview.src = url;

        imagePreviewWrapper.style.display = "block";

        // New image means previous AI guidance may no longer match
        aiResult.style.display = "none";
        analyzedUrgencyLevel = null;
    }
});


clearImageBtn.addEventListener("click", () => {

    imgInput.value = "";

    imagePreview.src = "";

    imagePreviewWrapper.style.display = "none";

    aiResult.style.display = "none";

});


// ── Open camera ────────────────────────────────────────────────

openCameraBtn.addEventListener(
    "click",
    async () => {

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
    }
);


// ── Capture photo ──────────────────────────────────────────────

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

    photoCanvas.toBlob(
        (blob) => {

            if (!blob) {

                alert("Unable to capture photo");

                return;
            }

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

            dataTransfer.items.add(
                capturedFile
            );

            imgInput.files =
                dataTransfer.files;


            const url =
                URL.createObjectURL(blob);

            imagePreview.src = url;

            imagePreviewWrapper.style.display =
                "block";


            // Previous AI result is no longer valid
            aiResult.style.display =
                "none";

            alert("Photo captured successfully");

        },
        "image/jpeg"
    );


    if (cameraStream) {

        cameraStream.getTracks()
            .forEach(track => track.stop());

        cameraStream = null;
    }


    setActiveToggle("upload");

});


// ── GPS location ───────────────────────────────────────────────

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


// ── AI Guidance ────────────────────────────────────────────────

analyzeAiBtn.addEventListener(
    "click",
    async () => {

        const animalType =
            document.getElementById(
                "animalType"
            ).value.trim();

        const description =
            document.getElementById(
                "description"
            ).value.trim();

        const image =
            imgInput.files[0];


        // Required data for AI analysis
        if (!image) {

            alert(
                "Please capture or select an image first"
            );

            return;
        }


        if (!animalType) {

            alert(
                "Please enter the animal type first"
            );

            document.getElementById(
                "animalType"
            ).focus();

            return;
        }


        if (!description) {

            alert(
                "Please describe the animal's condition first"
            );

            document.getElementById(
                "description"
            ).focus();

            return;
        }


        // Reset UI
        aiResult.style.display = "none";

        aiLoading.style.display = "flex";

        aiLoadingText.textContent =
            "Analyzing the animal's condition...";

        analyzeAiBtn.disabled = true;

        analyzeAiBtn.textContent =
            "Analyzing...";


        const formData =
            new FormData();

        formData.append(
            "image",
            image
        );

        formData.append(
            "animalType",
            animalType
        );

        formData.append(
            "description",
            description
        );


        try {

            const response =
                await apiRequest(
                    "/ai/first-aid",
                    "POST",
                    formData,
                    true
                );


            if (
                !response ||
                !response.success ||
                !response.data
            ) {

                throw new Error(
                    response?.message ||
                    "Unable to generate AI guidance"
                );
            }


            displayAiGuidance(
                response.data
            );

        } catch (error) {

            console.error(
                "AI analysis error:",
                error
            );

            alert(
                error.message ||
                "Unable to generate AI guidance. Please try again."
            );

        } finally {

            aiLoading.style.display =
                "none";

            analyzeAiBtn.disabled =
                false;

            analyzeAiBtn.textContent =
                "Analyze for Immediate Guidance";
        }

    }
);


// ── Display AI guidance ────────────────────────────────────────

function displayAiGuidance(data) {

    const urgency =
        data.urgencyLevel || "MEDIUM";
    analyzedUrgencyLevel =
    data.urgencyLevel || null;


    // Remove old urgency classes
    aiUrgency.classList.remove(
        "ai-urgency--low",
        "ai-urgency--medium",
        "ai-urgency--high",
        "ai-urgency--critical"
    );


    // Add current urgency class
    aiUrgency.classList.add(
        `ai-urgency--${urgency.toLowerCase()}`
    );


    aiUrgencyLevel.textContent =
        `${urgency} PRIORITY`;


    aiUrgencyMessage.textContent =
        getUrgencyMessage(urgency);


    // Main actions
    aiMainActions.innerHTML = "";


    if (
        Array.isArray(
            data.mainFirstAidActions
        )
    ) {

        data.mainFirstAidActions.forEach(
            action => {

                const item =
                    document.createElement("li");

                item.textContent =
                    action;

                aiMainActions.appendChild(
                    item
                );

            }
        );
    }


    // Show one relevant safety warning
    const warning =
        getSafetyWarning(data);


    aiWarningText.textContent =
        warning;


    // Disclaimer
    aiDisclaimer.textContent =
        data.disclaimer ||
        "AI-generated preliminary guidance only. Not a veterinary diagnosis.";


    // Show result
    aiResult.style.display =
        "flex";


    // Smoothly bring result into view
    aiResult.scrollIntoView({
        behavior: "smooth",
        block: "nearest"
    });

}


// ── Urgency message ────────────────────────────────────────────

function getUrgencyMessage(urgency) {

    switch (urgency) {

        case "LOW":

            return (
                "No obvious severe distress could be identified from the provided information."
            );


        case "MEDIUM":

            return (
                "The animal may need rescue attention. Continue to observe safely and submit the rescue report."
            );


        case "HIGH":

            return (
                "Urgent attention may be needed. Submit the rescue report as soon as possible."
            );


        case "CRITICAL":

            return (
                "The situation may be life-threatening. Seek immediate rescue or emergency veterinary assistance."
            );


        default:

            return (
                "Please follow the guidance below and submit the rescue report if assistance is needed."
            );
    }

}


// ── Choose one safety warning ──────────────────────────────────

function getSafetyWarning(data) {

    if (
        Array.isArray(data.precautions) &&
        data.precautions.length > 0
    ) {

        return data.precautions[0];
    }


    if (
        Array.isArray(data.doNotDo) &&
        data.doNotDo.length > 0
    ) {

        return data.doNotDo[0];
    }


    if (
        Array.isArray(data.immediateActions) &&
        data.immediateActions.length > 0
    ) {

        return data.immediateActions[0];
    }


    return (
        "Keep a safe distance and avoid unnecessary handling of an injured or frightened animal."
    );

}


// ── Submit rescue report ───────────────────────────────────────

reportForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        const animalType =
            document.getElementById(
                "animalType"
            ).value;

        const description =
            document.getElementById(
                "description"
            ).value;

        const location =
            document.getElementById(
                "location"
            ).value;

        const image =
            imgInput.files[0];


        if (!image) {

            alert(
                "Please capture or select an image"
            );

            return;
        }


        const formData =
            new FormData();

        formData.append(
            "animalType",
            animalType
        );

        formData.append(
            "description",
            description
        );

        formData.append(
            "location",
            location
        );

        formData.append(
            "image",
            image
        );
        if (analyzedUrgencyLevel) {
        formData.append(
            "urgencyLevel",
            analyzedUrgencyLevel
        );
    }


        const response =
            await apiRequest(
                "/reports",
                "POST",
                formData,
                true
            );


        console.log(response);


        if (
            response &&
            response.success
        ) {

            alert(
                "Report Created Successfully"
            );

            window.location.href =
                "dashboard.html";
        }

    }
);