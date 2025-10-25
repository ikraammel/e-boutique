package com.ecommerce.demo.repositories;

import com.ecommerce.demo.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Integer> {
}
