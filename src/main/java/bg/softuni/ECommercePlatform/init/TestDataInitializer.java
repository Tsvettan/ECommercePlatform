package bg.softuni.ECommercePlatform.init;

import bg.softuni.ECommercePlatform.enums.Role;
import bg.softuni.ECommercePlatform.enums.Status;
import bg.softuni.ECommercePlatform.model.OrderEntity;
import bg.softuni.ECommercePlatform.model.OrderItemEntity;
import bg.softuni.ECommercePlatform.model.ProductEntity;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.repository.OrderItemRepository;
import bg.softuni.ECommercePlatform.repository.OrderRepository;
import bg.softuni.ECommercePlatform.repository.ProductRepository;
import bg.softuni.ECommercePlatform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public TestDataInitializer(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public void run(String... args) {
        // Create a test user
        UserEntity user = userRepository.findByUsername("testuser").orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setUsername("testuser");
            //the encrypted password - "test"
            newUser.setPassword("$2a$12$oUqA3UC2v8iUgbMvxN4tQe5z2ZpQ3/iOPRAnj1JVMmgAqcuqL7D4q");
            newUser.setEmail("test@example.com");
            newUser.setRole(Role.USER);
            return userRepository.save(newUser);
        });

        // Create test products
        ProductEntity product1 = productRepository.findById(1L).orElseGet(() -> {
            ProductEntity newProduct = new ProductEntity();
            newProduct.setName("Vacuum Cleaner");
            newProduct.setPrice(149.90);
            newProduct.setDescription("This is an example product");
            newProduct.setRating(4.5);
            newProduct.setStock(10);
            newProduct.setImageUrl("https://example.com/product.jpg");
            return productRepository.save(newProduct);
        });

        ProductEntity product2 = productRepository.findById(2L).orElseGet(() -> {
            ProductEntity newProduct = new ProductEntity();
            newProduct.setName("Tablet");
            newProduct.setPrice(99.99);
            newProduct.setDescription("This is an example product 2");
            newProduct.setRating(2.5);
            newProduct.setStock(30);
            newProduct.setImageUrl("https://example.com/product2.jpg");
            return productRepository.save(newProduct);
        });

        // Create a test order
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Status.PENDING);
        orderRepository.save(order);

        // Create order items
        OrderItemEntity item1 = new OrderItemEntity(order, product1, 2, product1.getPrice());
        OrderItemEntity item2 = new OrderItemEntity(order, product2, 1, product2.getPrice());
        orderItemRepository.saveAll(List.of(item1, item2));
    }
}
