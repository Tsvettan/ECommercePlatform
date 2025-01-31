package bg.softuni.ECommercePlatform.config;

import bg.softuni.ECommercePlatform.model.CategoryEntity;
import bg.softuni.ECommercePlatform.model.ProductEntity;
import bg.softuni.ECommercePlatform.repository.CategoryRepository;
import bg.softuni.ECommercePlatform.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(CategoryRepository categoryRepository, ProductRepository productRepository) {
        return args -> {

            CategoryEntity electronics = new CategoryEntity("Electronics");
            categoryRepository.save(electronics);

            ProductEntity phone = new ProductEntity("Smartphone", "Latest model", 699.99, 50, electronics);
            ProductEntity laptop = new ProductEntity("Laptop", "High performance", 1199.99, 20, electronics);
            productRepository.save(phone);
            productRepository.save(laptop);

            // Fetch and print categories and their products to verify
            CategoryEntity fetchedCategory = categoryRepository.findById(electronics.getId()).orElseThrow();
            System.out.println("Category: " + fetchedCategory.getName());
            fetchedCategory.getProducts().forEach(product -> {
                System.out.println("- Product: " + product.getName() + ", Price: $" + product.getPrice());
            });
        };
    }
}
