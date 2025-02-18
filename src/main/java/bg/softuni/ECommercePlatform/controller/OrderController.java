package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.model.OrderEntity;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.service.OrderService;
import bg.softuni.ECommercePlatform.service.ShoppingCartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final ShoppingCartService shoppingCartService;
    private final OrderService orderService;

    public OrderController(ShoppingCartService shoppingCartService, OrderService orderService) {
        this.shoppingCartService = shoppingCartService;
        this.orderService = orderService;
    }

    @PostMapping("/cart/checkout")
    public String checkout(@AuthenticationPrincipal UserEntity user, Model model) {
        try {
            OrderEntity order = shoppingCartService.placeOrder(user);
            model.addAttribute("order", order);
            return "order-confirmation";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "cart";
        }
    }

    @GetMapping
    public String viewOrders(@AuthenticationPrincipal UserEntity user, Model model) {
        if (user == null) {
            return "redirect:/login?error=Please log in to see your orders";
        }

        List<OrderEntity> orders = orderService.getOrdersForUser(user);
        model.addAttribute("orders", orders);
        return "orders";
    }

//    @PostMapping("/place")
//    public String placeOrder(@AuthenticationPrincipal UserEntity user, RedirectAttributes redirectAttributes) {
//        if (user == null) {
//            return "redirect:/login?error=Please log in to place an order";
//        }
//
//        orderService.placeOrder(user);
//        redirectAttributes.addFlashAttribute("successMessage", "Your order has been placed successfully!");
//        return "redirect:/orders";
//    }

    @GetMapping("/orders/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        return "admin/order-details";
    }
}
