package com.ecommerce.demo.repositories;

import com.ecommerce.demo.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Integer>{
}
