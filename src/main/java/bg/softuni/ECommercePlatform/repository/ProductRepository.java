package bg.softuni.ECommercePlatform.repository;

import bg.softuni.ECommercePlatform.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByCategoryName(String categoryName);
}