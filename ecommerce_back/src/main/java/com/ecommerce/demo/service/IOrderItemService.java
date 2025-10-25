package com.ecommerce.demo.service;

import com.ecommerce.demo.dtos.OrderItemDto;
import com.ecommerce.demo.response.OrderItemResponse;

import java.util.List;

public interface IOrderItemService {
    OrderItemResponse addItemToOrder(Integer orderId, OrderItemDto orderItemDto);
    OrderItemResponse updateOrderItem(Integer orderId,  Integer orderItemId,OrderItemDto orderItemDto);
    void deleteOrderItem(Integer orderId, Integer orderItemId);
    List<OrderItemResponse> getItemsByOrder(Integer orderId);
    OrderItemResponse getOrderItemById(Integer orderItemId);
    void addItemsToOrder(Integer orderId, List<OrderItemDto> items);
    List<OrderItemResponse> getAllItems();
}
