package com.ecommerce.demo.controller;

import com.ecommerce.demo.dtos.CartItemDto;
import com.ecommerce.demo.models.CartItem;
import com.ecommerce.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}/items")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId ")
    public ResponseEntity<List<CartItemDto>> getCartItems(@PathVariable Integer userId){
        List<CartItemDto> itemsDto = cartService.getAllItemsDto(userId);
        return ResponseEntity.ok(itemsDto);
    }

    @PostMapping("/{userId}/add")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId ")
    public ResponseEntity<CartItemDto> addToCart(@PathVariable Integer userId,
                                                 @RequestParam Integer variantId,
                                                 @RequestParam(defaultValue = "1") int quantity){
        CartItem added = cartService.addVariantToCart(userId, variantId, quantity);
        return ResponseEntity.ok(cartService.toDto(added));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId ")
    public ResponseEntity<CartItemDto> updateQuantity(@RequestParam Integer userId,
                                                      @RequestParam Integer variantId,
                                                      @RequestParam int quantity){
        CartItem updated = cartService.updateVariantQuantity(userId, variantId, quantity);
        return ResponseEntity.ok(cartService.toDto(updated));
    }

    @PutMapping("/change")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId ")
    public ResponseEntity<CartItemDto> changeVariant(@RequestParam Integer userId,
                                                     @RequestParam Integer oldVariantId,
                                                     @RequestParam Integer newVariantId){
        CartItem updated = cartService.updateCartItemVariant(userId, oldVariantId, newVariantId);
        return ResponseEntity.ok(cartService.toDto(updated));
    }

    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId ")
    public ResponseEntity<Void> clearCart(@PathVariable Integer userId){
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId ")
    @DeleteMapping("/{variantId}")
    public ResponseEntity<Void> removeVariantFromCart(@PathVariable Integer variantId,@RequestParam Integer userId){
        cartService.removeVariantFromCart(userId,variantId);
        return ResponseEntity.noContent().build();
    }

}
