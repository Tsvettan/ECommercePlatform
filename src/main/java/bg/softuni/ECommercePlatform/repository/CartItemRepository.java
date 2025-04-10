package bg.softuni.ECommercePlatform.repository;

import bg.softuni.ECommercePlatform.model.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByShoppingCart_Id(Long cartId);
}
