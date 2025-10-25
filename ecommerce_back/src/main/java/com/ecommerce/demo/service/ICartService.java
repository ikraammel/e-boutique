package com.ecommerce.demo.service;

import com.ecommerce.demo.dtos.CartItemDto;
import com.ecommerce.demo.models.Cart;
import com.ecommerce.demo.models.CartItem;

import java.util.List;

public interface ICartService {

    List<CartItem> getAllItems(Integer userId);

    CartItem addVariantToCart(Integer userId, Integer variantId, int quantity);

    void removeVariantFromCart(Integer userId, Integer variantId);

    CartItem updateVariantQuantity(Integer userId, Integer variantId, int quantity);

    Cart getCartByUser(Integer userId);

    void clearCart(Integer userId);

    CartItem updateCartItemVariant(Integer userId, Integer oldVariantId,Integer newVariantId);
}
