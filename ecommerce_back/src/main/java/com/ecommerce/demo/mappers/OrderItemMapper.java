package com.ecommerce.demo.mappers;

import com.ecommerce.demo.models.OrderItem;
import com.ecommerce.demo.response.OrderItemResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemResponse toDto(OrderItem orderItem){
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getProduct().getId(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                orderItem.getOrder().getId()
        );
    }
}
