package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.enums.Role;
import bg.softuni.ECommercePlatform.model.ProductEntity;
import bg.softuni.ECommercePlatform.service.OrderService;
import bg.softuni.ECommercePlatform.service.ProductService;
import bg.softuni.ECommercePlatform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;

    public AdminController(ProductService productService, UserService userService, OrderService orderService) {
        this.productService = productService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getTotalUsers());
        stats.put("totalOrders", orderService.getTotalOrders());
        stats.put("totalRevenue", orderService.getTotalRevenue());

        Map<String, Integer> orderStats = orderService.getOrderStats();
        stats.put("orderStats", Map.of(
                "dates", orderStats.keySet(),
                "counts", orderStats.values()
        ));

        return ResponseEntity.ok(stats);
    }

    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "/admin/users";
    }

    @PostMapping("/update-role")
    public String updateRole(@RequestParam Long userId, @RequestParam Role role) {
        userService.updateUserRole(userId, role);
        return "redirect:/admin/users";
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        return "admin/product-form";
    }

    @PostMapping("/orders/update")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/admin";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute ProductEntity product) {
        productService.saveProduct(product);
        return "redirect:/admin";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "admin/product-form";
    }

    @PostMapping("/products/delete")
    public String deleteProduct(@RequestParam Long productId) {
        productService.deleteProduct(productId);
        return "redirect:/admin";
    }
}