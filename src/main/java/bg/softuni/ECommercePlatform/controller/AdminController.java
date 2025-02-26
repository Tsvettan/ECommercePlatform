package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.enums.Role;
import bg.softuni.ECommercePlatform.model.ProductEntity;
import bg.softuni.ECommercePlatform.service.OrderService;
import bg.softuni.ECommercePlatform.service.ProductService;
import bg.softuni.ECommercePlatform.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
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