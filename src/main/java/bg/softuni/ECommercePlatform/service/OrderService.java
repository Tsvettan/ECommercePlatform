package bg.softuni.ECommercePlatform.service;

import bg.softuni.ECommercePlatform.dto.OrderDTO;
import bg.softuni.ECommercePlatform.enums.PaymentStatus;
import bg.softuni.ECommercePlatform.enums.Status;
import bg.softuni.ECommercePlatform.model.OrderEntity;
import bg.softuni.ECommercePlatform.model.OrderItemEntity;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public OrderEntity createOrder(UserEntity user, List<OrderItemEntity> items) {
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("User or username cannot be null"); // ✅ Prevent NULL username
        }

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setItems(items);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Status.PENDING);

        return orderRepository.save(order);
    }

    public List<OrderEntity> getOrdersForUser(UserEntity user) {
        return orderRepository.findByUserId(user.getId());
    }

    public OrderEntity getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " not found!"));
    }

    public void updateOrderStatus(Long orderId, String status) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(Status.valueOf(status));
        orderRepository.save(order);
    }

    public Long getTotalOrders() {
        return orderRepository.count();
    }

    public Long getTotalRevenue() {
        return orderRepository.count();
    }

    public Map<String, Integer> getOrderStats() {
        return orderRepository
                .findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().toLocalDate().toString(),
                        Collectors.summingInt(order -> 1)
                ));
    }

    public List<OrderEntity> getOrdersByUsername(String username) {
        return orderRepository.findByUsername(username);
    }
}
