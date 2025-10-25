package com.ecommerce.demo.controller;

import com.ecommerce.demo.dtos.OrderItemDto;
import com.ecommerce.demo.models.Status;
import com.ecommerce.demo.models.User;
import com.ecommerce.demo.repositories.UserRepository;
import com.ecommerce.demo.response.OrderResponse;
import com.ecommerce.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principalUser,
            @RequestBody List<OrderItemDto> items) {

        // Récupérer le User JPA depuis l'email
        User user = userRepository.findByEmail(principalUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(user.getId(), items));
    }


    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.isOrderOwner(#orderId, principal.userId)")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer orderId){
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @DeleteMapping("/{orderId}/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.isOrderOwner(#orderId,principal.userId)")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer orderId,
                                            @PathVariable Integer userId){
        orderService.deleteOrder(orderId,userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders(){
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Integer userId){
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Integer orderId, @RequestParam Status status){
        return ResponseEntity.ok(orderService.updateStatus(orderId,status));
    }

//    @PostMapping("/checkout/{userId}")
//    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
//    public ResponseEntity<OrderResponse> checkout(@PathVariable Integer userId) {
//        OrderResponse order = orderService.checkoutFromCart(userId);
//        return ResponseEntity.status(HttpStatus.CREATED).body(order);
//    }

}
