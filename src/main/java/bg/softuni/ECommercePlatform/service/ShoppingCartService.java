package bg.softuni.ECommercePlatform.service;

import bg.softuni.ECommercePlatform.model.*;
import bg.softuni.ECommercePlatform.repository.CartItemRepository;
import bg.softuni.ECommercePlatform.repository.ShoppingCartRepository;
import bg.softuni.ECommercePlatform.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, CartItemRepository cartItemRepository, OrderRepository orderRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
    }

    public ShoppingCartEntity getCartByUser(UserEntity user) {
        return shoppingCartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    ShoppingCartEntity cart = new ShoppingCartEntity(user);
                    return shoppingCartRepository.save(cart);
                });
    }

    public void addToCard(ShoppingCartEntity cart, ProductEntity product, int quantity) {
        Optional<CartItemEntity> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItemEntity item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItemEntity newItem = new CartItemEntity(cart, product, quantity);
            cartItemRepository.save(newItem);
        }

        shoppingCartRepository.save(cart);
    }

    @Transactional
    public OrderEntity placeOrder(UserEntity user) {
        ShoppingCartEntity cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Cart not found for user"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place order with an empty cart");
        }

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setItems(cart.getItems().stream()
                .map(item -> new OrderItemEntity(order, item.getProduct(), item.getQuantity(), item.getProduct().getPrice()))
                .collect(Collectors.toList()));

        orderRepository.save(order);
        cart.getItems().clear();
        shoppingCartRepository.save(cart);

        return order;
    }

    public double calculateTotalCost(UserEntity user) {
        ShoppingCartEntity cart = getCartByUser(user);
        return cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    public void removeFromCart(ShoppingCartEntity cart, ProductEntity product) {
        cart.getItems().removeIf(item -> item.getProduct().equals(product));
        shoppingCartRepository.save(cart);
    }
}