package com.ecommerce.demo.mappers;

import com.ecommerce.demo.models.Order;
import com.ecommerce.demo.response.OrderItemResponse;
import com.ecommerce.demo.response.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderResponse toDto(Order order){
        List<OrderItemResponse> items = order.getItems().stream().map(item ->
                new OrderItemResponse(item.getId(),
                        item.getProduct().getId(),
                        item.getQuantity(),
                        item.getPrice(),
                        order.getId()  )
        ).collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                items,
                order.getTotal(),
                order.getStatus()
        );
    }
}
