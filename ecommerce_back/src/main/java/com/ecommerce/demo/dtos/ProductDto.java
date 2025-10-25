package com.ecommerce.demo.dtos;

import com.ecommerce.demo.models.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private int stock;
    private Category category;
    private List<ProductVariantDto> variants;
}
