document.addEventListener("DOMContentLoaded", async function () {
    try {
        const response = await fetch("/admin/stats");
        const data = await response.json();

        document.getElementById("totalUsers").textContent = data.totalUsers;
        document.getElementById("totalOrders").textContent = data.totalOrders;
        document.getElementById("totalRevenue").textContent = "$" + data.totalRevenue.toFixed(2);

        // Chart.js for Order Analytics
        const ctx = document.getElementById("ordersChart").getContext("2d");
        new Chart(ctx, {
            type: "line",
            data: {
                labels: data.orderStats.dates,
                datasets: [{
                    label: "Orders",
                    data: data.orderStats.counts,
                    borderColor: "#ff6600",
                    backgroundColor: "rgba(255, 102, 0, 0.2)",
                    fill: true
                }]
            }
        });
    } catch (error) {
        console.error("Error fetching admin stats:", error);
    }
});

// Fetch all users and display in the table
function fetchUsers() {
    fetch("/users")
        .then(response => response.json())
        .then(users => {
            const tableBody = document.getElementById("user-table-body");
            tableBody.innerHTML = ""; // Clear existing content

            users.forEach(user => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td>
                        <select class="form-select role-select" data-user-id="${user.id}">
                            <option value="USER" ${user.role === "USER" ? "selected" : ""}>USER</option>
                            <option value="ADMIN" ${user.role === "ADMIN" ? "selected" : ""}>ADMIN</option>
                        </select>
                    </td>
                    <td>
                        <button class="btn btn-danger btn-sm delete-user" data-user-id="${user.id}">Delete</button>
                    </td>
                `;
                tableBody.appendChild(row);
            });

            // Add event listeners for role changes
            document.querySelectorAll(".role-select").forEach(select => {
                select.addEventListener("change", function () {
                    updateUserRole(this.dataset.userId, this.value);
                });
            });

            // Add event listeners for delete buttons
            document.querySelectorAll(".delete-user").forEach(button => {
                button.addEventListener("click", function () {
                    deleteUser(this.dataset.userId);
                });
            });
        })
        .catch(error => console.error("Error fetching users:", error));
}

function updateUserRole(userId, newRole) {
    fetch(`/users/${userId}/role?role=${newRole}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" }
    })
    .then(response => {
        if (response.ok) {
            alert("User role updated successfully!");
        } else {
            alert("Failed to update role.");
        }
    })
    .catch(error => console.error("Error updating role:", error));
}

// Delete user
function deleteUser(userId) {
    if (confirm("Are you sure you want to delete this user?")) {
        fetch(`/users/${userId}`, {
            method: "DELETE"
        })
        .then(response => {
            if (response.ok) {
                alert("User deleted successfully!");
                fetchUsers();
            } else {
                alert("Failed to delete user.");
            }
        })
        .catch(error => console.error("Error deleting user:", error));
    }
}