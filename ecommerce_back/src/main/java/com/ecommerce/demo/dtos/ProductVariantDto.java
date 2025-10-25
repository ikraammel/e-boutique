package com.ecommerce.demo.dtos;

import com.ecommerce.demo.models.SizeClothing;
import com.ecommerce.demo.models.SizePants;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDto {
    private Integer id;
    private String color;
    private List<String> imageUrls;
    private List<SizeClothing> sizeClothing;
    private List<SizePants> sizePants;
    private double price;
}

