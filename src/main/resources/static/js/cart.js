document.addEventListener("DOMContentLoaded", function() {
    console.log("Cart system initialized.");

    let cartButtons = document.querySelectorAll(".add-to-cart-btn");

    cartButtons.forEach(button => {
        button.addEventListener("click", function(event) {
            event.preventDefault(); // Prevent page reload
            let form = this.closest("form");
            let formData = new FormData(form);

            fetch("/cart/add", {
                method: "POST",
                body: formData
            })
            .then(response => response.text())
            .then(data => {
                // Show success message without reloading
                let cartMessage = document.getElementById("cart-message");
                if (cartMessage) {
                    cartMessage.innerText = "✅ Item added to cart!";
                    cartMessage.style.display = "block";
                    setTimeout(() => cartMessage.style.display = "none", 2000);
                }
            })
            .catch(error => console.error("Error:", error));
        });
    });
});
