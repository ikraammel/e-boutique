package com.ecommerce.demo.repositories;

import com.ecommerce.demo.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage,Integer> {
    List<ProductImage> findByVariant_Product_Id(Integer productId);
}
