package com.ecommerce.demo.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySalesDTO {
    private String category;
    private Long count;
    private Double revenue;
}
