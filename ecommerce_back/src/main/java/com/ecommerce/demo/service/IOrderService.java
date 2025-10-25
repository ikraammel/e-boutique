package com.ecommerce.demo.service;

import com.ecommerce.demo.dtos.OrderItemDto;
import com.ecommerce.demo.models.Status;
import com.ecommerce.demo.response.OrderResponse;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(Integer userId, List<OrderItemDto> items);
    void deleteOrder(Integer orderId, Integer userId);
    List<OrderResponse> getOrdersByUser(Integer userId);
    List<OrderResponse> getAllOrders();
    OrderResponse updateStatus(Integer orderId, Status status);
    OrderResponse getOrderById(Integer orderId);

}
