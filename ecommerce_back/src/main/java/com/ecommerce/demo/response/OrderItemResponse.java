package com.ecommerce.demo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemResponse {
    private Integer id;
    private Integer productId;
    private Integer quantity;
    private double price;
    private Integer orderId;
}
