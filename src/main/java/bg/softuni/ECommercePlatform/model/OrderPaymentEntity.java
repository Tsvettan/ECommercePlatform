package bg.softuni.ECommercePlatform.model;

import bg.softuni.ECommercePlatform.enums.PaymentStatus;
import jakarta.persistence.*;

@Entity
public class OrderPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(nullable = false)
    private String paymentIntentId;     // Stripe Payment Intent ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    public OrderPaymentEntity() {
    }

    public OrderPaymentEntity(OrderEntity order, String paymentIntentId, PaymentStatus status) {
        this.order = order;
        this.paymentIntentId = paymentIntentId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
