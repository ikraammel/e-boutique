package com.ecommerce.demo.service;

import com.ecommerce.demo.dtos.OrderItemDto;
import com.ecommerce.demo.mappers.OrderMapper;
import com.ecommerce.demo.models.*;
import com.ecommerce.demo.repositories.OrderRepository;
import com.ecommerce.demo.repositories.ProductRepository;
import com.ecommerce.demo.repositories.UserRepository;
import com.ecommerce.demo.response.OrderResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService implements IOrderService{

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;

    @Override
    public OrderResponse createOrder(Integer userId, List<OrderItemDto> items) {
         User user = userRepository.findById(userId)
                 .orElseThrow(() -> new UsernameNotFoundException("User not found with id:"+userId));
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.PENDING);

        List<OrderItem> orderItems = items.stream().map(itemDto -> {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setOrder(order);

            return orderItem;
        }).toList();
        double total = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

        order.setItems(orderItems);
        order.setTotal(total);

        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }


    @Override
    public void deleteOrder(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User user = order.getUser();
        if (!user.getId().equals(userId) && !user.getRole().equals("ADMIN"))
            throw new RuntimeException("You are not allowed to cancel this order");

        order.setStatus(Status.CANCELLED);

        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Integer userId) {
        return orderRepository.findByUserIdAndStatusNot(userId,Status.CANCELLED)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse updateStatus(Integer orderId, Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order not found with id: "+orderId));
        return orderMapper.toDto(order);
    }

    public boolean isOrderOwner(Integer orderId, Integer userId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getId().equals(userId))
                .orElse(false);
    }

}
