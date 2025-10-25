package com.ecommerce.demo.controller;

import com.ecommerce.demo.dtos.OrderItemDto;
import com.ecommerce.demo.response.OrderItemResponse;
import com.ecommerce.demo.service.OrderItemService;
import com.ecommerce.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @PostMapping("/{orderId}/item")
    @PreAuthorize("hasRole('ADMIN') or @orderService.isOrderOwner(#orderId, principal.userId)")
    public ResponseEntity<OrderItemResponse> addItem(@PathVariable Integer orderId,
                                                     @RequestBody OrderItemDto orderItemDto){
        OrderItemResponse item = orderItemService.addItemToOrder(orderId,orderItemDto);
        return ResponseEntity.ok(item);
    }

    @PostMapping("/{orderId}/items")
    @PreAuthorize("hasRole('ADMIN') or @orderService.isOrderOwner(#orderId, principal.userId)")
    public ResponseEntity<Void> addItems(@PathVariable Integer orderId,
                                              @RequestBody List<OrderItemDto> orderItemDtos){
        orderItemService.addItemsToOrder(orderId,orderItemDtos);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{orderId}/{orderItemId}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.isOrderOwner(#orderId, principal.userId)")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Integer orderItemId,@PathVariable Integer orderId){
        orderItemService.deleteOrderItem(orderId,orderItemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.isOrderOwner(#orderId, principal.userId)")
    public ResponseEntity<List<OrderItemResponse>> getItemsByOrder(@PathVariable Integer orderId){
        return ResponseEntity.ok(orderItemService.getItemsByOrder(orderId));
    }

    @GetMapping("/{orderItemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Integer orderItemId){
        return ResponseEntity.ok(orderItemService.getOrderItemById(orderItemId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderItemResponse>> getAllItems(){
        return ResponseEntity.ok(orderItemService.getAllItems());
    }


}
