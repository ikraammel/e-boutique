package com.ecommerce.demo.mappers;

import com.ecommerce.demo.dtos.ProductDto;
import com.ecommerce.demo.dtos.ProductVariantDto;
import com.ecommerce.demo.models.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        List<ProductVariantDto> variantDtos = null;
        if (product.getVariants() != null) {
            variantDtos = product.getVariants().stream().map(variant -> {
                List<String> imageUrls = null;
                if (variant.getImages() != null && !variant.getImages().isEmpty()) {
                    int size = variant.getImages().size();
                    imageUrls = java.util.stream.IntStream.range(0, size)
                            .mapToObj(index -> "/products/variants/" + variant.getId() + "/images/" + index)
                            .toList();
                }
                return new ProductVariantDto(
                        variant.getId(),
                        variant.getColor(),
                        imageUrls,
                        variant.getSizeClothings(),
                        variant.getSizePants(),
                        variant.getPrice()
                );
            }).toList();
        }

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                variantDtos
        );
    }

}
