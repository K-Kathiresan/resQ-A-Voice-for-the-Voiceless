document.addEventListener("DOMContentLoaded", () => {

    const savedTheme =
        localStorage.getItem("theme") || "light";

    document.documentElement.setAttribute(
        "data-theme",
        savedTheme
    );
    updateThemeIcon();

});

function toggleTheme() {

    const currentTheme =
        document.documentElement.getAttribute("data-theme");

    const newTheme =
        currentTheme === "dark"
            ? "light"
            : "dark";

    document.documentElement.setAttribute(
        "data-theme",
        newTheme
    );

    localStorage.setItem(
        "theme",
        newTheme
    );
}
document.addEventListener("DOMContentLoaded", () => {

    const themeToggle =
        document.getElementById("themeToggle");

    if (themeToggle) {

        themeToggle.addEventListener("click", () => {

            toggleTheme();
            updateThemeIcon();

            console.log(
                "Current Theme:",
                document.documentElement.getAttribute("data-theme")
            );

        });

    }

});
function updateThemeIcon() {

    const themeToggle =
        document.getElementById("themeToggle");

    if (!themeToggle) return;

    const currentTheme =
        document.documentElement.getAttribute("data-theme");

    themeToggle.textContent =
        currentTheme === "dark"
            ? "☀️"
            : "🌙";
}