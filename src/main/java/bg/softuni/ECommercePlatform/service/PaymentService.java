package bg.softuni.ECommercePlatform.service;

import bg.softuni.ECommercePlatform.enums.PaymentStatus;
import bg.softuni.ECommercePlatform.model.OrderEntity;
import bg.softuni.ECommercePlatform.model.OrderPaymentEntity;
import bg.softuni.ECommercePlatform.repository.OrderPaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final OrderPaymentRepository orderPaymentRepository;

    public PaymentService(OrderPaymentRepository orderPaymentRepository,
                          @Value("${stripe.secret-key}") String secretKey) {
        this.orderPaymentRepository = orderPaymentRepository;
        Stripe.apiKey = secretKey;
    }

    public String createPaymentIntent(OrderEntity order) throws Exception {
        // Calculate the total amount for the order
        double amount = order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Create the payment intent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100)) // Convert to cents
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Save payment intent details
        OrderPaymentEntity orderPayment = new OrderPaymentEntity(order, paymentIntent.getId(), PaymentStatus.PENDING);
        orderPaymentRepository.save(orderPayment);

        return paymentIntent.getClientSecret();
    }

    public void updatePaymentStatus(String paymentIntentId, PaymentStatus status) {
        OrderPaymentEntity payment = orderPaymentRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        payment.setStatus(status);
        orderPaymentRepository.save(payment);
    }
}
