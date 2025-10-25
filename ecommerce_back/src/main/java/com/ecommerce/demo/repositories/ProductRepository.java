package com.ecommerce.demo.repositories;

import com.ecommerce.demo.models.Category;
import com.ecommerce.demo.models.Product;
import com.ecommerce.demo.models.ProductVariant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    @EntityGraph(attributePaths = {"variants", "variants.images"})
    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategory(Category category);

    @Transactional
    @Modifying
    @Query("""
        UPDATE Product p SET 
            p.name = COALESCE(:name, p.name),
            p.description = COALESCE(:description, p.description),
            p.price = COALESCE(:price, p.price),
            p.stock = COALESCE(:stock, p.stock),
            p.category = COALESCE(:category, p.category)
        WHERE p.id = :id
    """)
    int updateProductFields(
            Integer id,
            String name,
            String description,
            Double price,
            Integer stock,
            Category category
    );

}
