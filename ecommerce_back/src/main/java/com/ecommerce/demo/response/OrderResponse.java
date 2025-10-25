package com.ecommerce.demo.response;

import com.ecommerce.demo.dtos.OrderItemDto;
import com.ecommerce.demo.models.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Integer id;
    private Integer userId;
    private List<OrderItemResponse> items;
    private double total;
    private Status status;
}
