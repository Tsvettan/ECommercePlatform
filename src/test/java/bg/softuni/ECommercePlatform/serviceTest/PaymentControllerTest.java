package bg.softuni.ECommercePlatform.serviceTest;

import bg.softuni.ECommercePlatform.model.OrderEntity;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.service.OrderService;
import bg.softuni.ECommercePlatform.service.PaymentService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootTest
public class PaymentControllerTest {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public PaymentControllerTest(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @PostMapping("/test-payment-intent")
    @ResponseBody
    public String testPaymentIntent(@AuthenticationPrincipal UserEntity user) {
        OrderEntity order = orderService.getOrderById(1L); // Replace with a valid order ID for testing
        try {
            return paymentService.createPaymentIntent(order);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
