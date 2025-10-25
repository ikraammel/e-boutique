package com.ecommerce.demo.service;

import com.ecommerce.demo.dtos.OrderItemDto;
import com.ecommerce.demo.mappers.OrderItemMapper;
import com.ecommerce.demo.models.Order;
import com.ecommerce.demo.models.OrderItem;
import com.ecommerce.demo.models.Product;
import com.ecommerce.demo.models.Status;
import com.ecommerce.demo.repositories.OrderItemRepository;
import com.ecommerce.demo.repositories.OrderRepository;
import com.ecommerce.demo.repositories.ProductRepository;
import com.ecommerce.demo.response.OrderItemResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderItemService implements IOrderItemService{

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItemResponse> getAllItems() {
        return orderItemRepository.findAll()
                .stream()
                .map(orderItemMapper::toDto)
                .toList();
    }
    @Override
    public OrderItemResponse addItemToOrder(Integer orderId, OrderItemDto orderItemDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        checkModifiable(order);
        Product product = productRepository.findById(orderItemDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setPrice(product.getPrice());

        OrderItem savedItem = orderItemRepository.save(orderItem);
        order.getItems().add(orderItem);
        recalculateTotalOrder(order);

        orderRepository.save(order);
        return orderItemMapper.toDto(orderItem);
    }

    private void recalculateTotalOrder(Order order) {
        double total = order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotal(total);
    }

    private void checkModifiable(Order order) {
        if (order.getStatus() == Status.CANCELLED || order.getStatus() == Status.DELIVERED|| order.getStatus() == Status.SHIPPED)
            throw new RuntimeException("Cannot modify this order");
    }

    @Override
    public OrderItemResponse updateOrderItem(Integer orderId, Integer orderItemId, OrderItemDto orderItemDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        checkModifiable(order);

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));
        if (orderItemDto.getQuantity() !=null)
            orderItem.setQuantity(orderItemDto.getQuantity());

        recalculateTotalOrder(order);
        orderRepository.save(order);
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public void deleteOrderItem(Integer orderId, Integer orderItemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        checkModifiable(order);
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        order.getItems().remove(orderItem);
        orderItemRepository.delete(orderItem);

        recalculateTotalOrder(order);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public List<OrderItemResponse> getItemsByOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return order.getItems().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderItemResponse getOrderItemById(Integer orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public void addItemsToOrder(Integer orderId, List<OrderItemDto> items) {
        for (OrderItemDto dto : items){
            addItemToOrder(orderId,dto);
        }
    }
}
