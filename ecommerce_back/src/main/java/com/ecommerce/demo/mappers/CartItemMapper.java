package com.ecommerce.demo.mappers;

import com.ecommerce.demo.dtos.CartItemDto;
import com.ecommerce.demo.dtos.ProductVariantDto;
import com.ecommerce.demo.models.CartItem;
import com.ecommerce.demo.models.ProductVariant;
import com.ecommerce.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartItemMapper {

    private final ProductService productService;

    public CartItemDto toDto(CartItem item) {
        ProductVariant variant = item.getVariant();

        // ✅ Récupérer toutes les variantes disponibles du même produit via ton service
        List<ProductVariantDto> variants = productService.getAllVariantsByProductDto(
                variant.getProduct().getId()
        );

        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        dto.setVariantId(variant.getId());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());
        dto.setProductName(variant.getProduct().getName());
        dto.setPrice(variant.getPrice());
        dto.setImageUrl("http://localhost:8080/products/variants/"
                + variant.getId() + "/images/0");

        dto.setProductId(item.getVariant().getProduct().getId());
        dto.setAvailableVariants(variants);

        return dto;
    }
}
