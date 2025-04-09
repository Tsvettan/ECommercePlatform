document.addEventListener("DOMContentLoaded", function () {
    // Toggle mobile menu
    document.getElementById("menu-toggle").addEventListener("click", function () {
        document.getElementById("nav-links").classList.toggle("active");
    });

    // Show/hide user dropdown on hover
    const userIcon = document.getElementById("user-icon");
    const userDropdown = document.getElementById("user-dropdown");

    if (userIcon && userDropdown) {
        userIcon.addEventListener("mouseover", function () {
            userDropdown.classList.add("show");
        });

        userDropdown.addEventListener("mouseleave", function () {
            userDropdown.classList.remove("show");
        });
    }
});