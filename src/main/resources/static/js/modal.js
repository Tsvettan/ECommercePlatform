document.addEventListener("DOMContentLoaded", function () {
    const modalOverlay = document.getElementById("modalOverlay");
    const loginButton = document.getElementById("loginButton");
    const closeModal = document.getElementById("closeModal");

    if (loginButton) {
        loginButton.addEventListener("click", function () {
            modalOverlay.style.display = "flex";
        });
    }

    if (closeModal) {
        closeModal.addEventListener("click", function () {
            modalOverlay.style.display = "none";
        });
    }

    window.addEventListener("click", function (event) {
        if (event.target === modalOverlay) {
            modalOverlay.style.display = "none";
        }
    });
});