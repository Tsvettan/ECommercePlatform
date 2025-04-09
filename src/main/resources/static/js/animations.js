document.addEventListener("DOMContentLoaded", function () {
    // Fade-in animations for elements
    document.querySelectorAll(".fade-in").forEach(element => {
        element.style.opacity = "1";
        element.style.transform = "translateY(0)";
    });

    // Navbar Scroll Effect
    window.addEventListener("scroll", function () {
        const navbar = document.querySelector(".elite-navbar");
        if (window.scrollY > 50) {
            navbar.classList.add("scrolled");
        } else {
            navbar.classList.remove("scrolled");
        }
    });

    // Smooth Scrolling
    document.querySelectorAll("a[href^='#']").forEach(anchor => {
        anchor.addEventListener("click", function (e) {
            e.preventDefault();
            document.querySelector(this.getAttribute("href")).scrollIntoView({
                behavior: "smooth"
            });
        });
    });
});