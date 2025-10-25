package com.ecommerce.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Integer id;
    private Integer variantId;
    private int quantity;
    private double subtotal;
    private String productName;
    private double price;
    private String imageUrl;
    private Integer productId;
    private List<ProductVariantDto> availableVariants;
}

