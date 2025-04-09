document.addEventListener("DOMContentLoaded", function() {
    console.log("Alerts initialized.");

    let alerts = document.querySelectorAll(".alert");
    alerts.forEach((alert) => {
        setTimeout(() => {
            alert.style.opacity = "0";
            setTimeout(() => alert.remove(), 500);
        }, 3000);
    });
});
