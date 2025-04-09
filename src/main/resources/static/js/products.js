document.addEventListener("DOMContentLoaded", function () {
    // Toggle mobile menu
    const menuToggle = document.getElementById("menu-toggle");
    const navLinks = document.getElementById("nav-links");

    if (menuToggle && navLinks) {
        menuToggle.addEventListener("click", function () {
            navLinks.classList.toggle("active");
        });
    }

    // Show user dropdown on hover
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

    // Handle "Add to Cart" Button Click
    document.querySelectorAll(".add-to-cart").forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault();
            const productId = this.getAttribute("data-product-id");

            fetch(`/cart/add?productId=${productId}&quantity=1`, { method: "POST" })
                .then(response => {
                    if (response.ok) {
                        window.location.href = "/cart"; // Redirect to cart page
                    } else {
                        alert("Failed to add product to cart.");
                    }
                })
                .catch(error => console.error("Error:", error));
        });
    });
});