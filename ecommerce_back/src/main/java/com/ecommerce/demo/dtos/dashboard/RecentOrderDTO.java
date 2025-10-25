package com.ecommerce.demo.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentOrderDTO {
    private Integer orderId;
    private String customerName;
    private String customerEmail;
    private Double total;
    private String status;
    private Integer itemsCount;
}
