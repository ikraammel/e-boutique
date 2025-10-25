package com.ecommerce.demo.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesDataDTO {
    private java.time.LocalDateTime date;
    private Double total;
}


