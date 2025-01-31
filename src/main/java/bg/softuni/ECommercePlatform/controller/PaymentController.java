package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.enums.PaymentStatus;
import bg.softuni.ECommercePlatform.model.OrderEntity;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.service.OrderService;
import bg.softuni.ECommercePlatform.service.PaymentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public PaymentController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping("/payment/{orderId}")
    public String paymentPage(@AuthenticationPrincipal UserEntity user, @PathVariable Long orderId, Model model) {
        OrderEntity order = orderService.getOrderById(orderId);

        System.out.println("Logged-in user ID: " + user.getId());
        System.out.println("Order user ID: " + order.getUser().getId());

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You do not have access to this order");
        }

        try {
            String clientSecret = paymentService.createPaymentIntent(order);
            model.addAttribute("clientSecret", clientSecret);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "payment-page";
    }


    @GetMapping("/payment-success")
    public String paymentSuccess(@RequestParam String paymentIntent, Model model) {
        try {
            paymentService.updatePaymentStatus(paymentIntent, PaymentStatus.SUCCESS);
            model.addAttribute("message", "Payment successful! Your order is confirmed.");
        } catch (Exception e) {
            model.addAttribute("message", "Payment failed or could not be verified.");
        }

        return "payment-success";
    }
}
