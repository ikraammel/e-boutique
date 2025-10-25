package com.ecommerce.demo.repositories;

import com.ecommerce.demo.models.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant,Integer> {
}
