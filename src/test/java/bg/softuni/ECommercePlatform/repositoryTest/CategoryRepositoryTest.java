package bg.softuni.ECommercePlatform.repositoryTest;

import bg.softuni.ECommercePlatform.model.CategoryEntity;
import bg.softuni.ECommercePlatform.model.ProductEntity;
import bg.softuni.ECommercePlatform.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @Transactional
    public void testFetchCategoryWithProducts() {
        // Fetch the category
        CategoryEntity electronics = categoryRepository.findAll().stream()
                .filter(category -> category.getName().equals("Electronics"))
                .findFirst()
                .orElseThrow();

        // Validate the category and its products
        assertThat(electronics).isNotNull();
        assertThat(electronics.getProducts()).isNotEmpty();

        List<ProductEntity> products = electronics.getProducts();
        assertThat(products.size()).isGreaterThanOrEqualTo(2);

        // Print products for verification
        products.forEach(product -> System.out.println("- Product: " + product.getName() + ", Price: $" + product.getPrice()));
    }
}