document.addEventListener("DOMContentLoaded", function () {
    // Hero section fade-in effect
    const heroSection = document.querySelector(".full-page-hero");
    if (heroSection) {
        heroSection.style.opacity = "1";
        heroSection.style.transform = "translateY(0)";
    }
});