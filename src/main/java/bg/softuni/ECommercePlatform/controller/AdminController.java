package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.model.ProductEntity;
import bg.softuni.ECommercePlatform.service.OrderService;
import bg.softuni.ECommercePlatform.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;

    public AdminController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    // Display the admin dashboard
    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/dashboard";
    }

    // Add Product Form
    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        return "admin/product-form";
    }

    // Update Order Status
    @PostMapping("/orders/update")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/admin";
    }

//    @GetMapping("/analytics")
//    public String adminAnalytics(Model model) {
//        model.addAttribute("totalSales", orderService.calculateTotalSales());
//        model.addAttribute("mostSoldProducts", productService.getMostSoldProducts());
//        model.addAttribute("totalUsers", userService.getTotalUsers());
//        return "admin/analytics";
//    }

    // Save or Update Product
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute ProductEntity product) {
        productService.saveProduct(product);
        return "redirect:/admin";
    }

    // Edit Product
    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "admin/product-form";
    }

    // Delete Product
    @PostMapping("/products/delete")
    public String deleteProduct(@RequestParam Long productId) {
        productService.deleteProduct(productId);
        return "redirect:/admin";
    }
}