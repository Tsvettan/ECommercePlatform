package bg.softuni.ECommercePlatform.repository;

import bg.softuni.ECommercePlatform.model.OrderPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderPaymentRepository extends JpaRepository<OrderPaymentEntity, Long> {

    Optional<OrderPaymentEntity> findByPaymentIntentId(String paymentIntentId);
}
