package com.ecommerce.demo.repositories;

import com.ecommerce.demo.models.Cart;
import com.ecommerce.demo.models.CartItem;
import com.ecommerce.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Integer> {
    Optional<Cart> findByUser(User user);
    List<CartItem> findByUserId(Integer userId);
}
