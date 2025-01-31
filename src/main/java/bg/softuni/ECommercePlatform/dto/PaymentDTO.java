package bg.softuni.ECommercePlatform.dto;

import java.time.LocalDateTime;

public class PaymentDTO {

    private Long id;
    private Long orderId;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private boolean success;

    public PaymentDTO() {
    }

    public PaymentDTO(Long id, Long orderId, String paymentMethod, LocalDateTime paymentDate, boolean success) {
        this.id = id;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.success = success;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
