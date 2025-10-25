package com.ecommerce.demo.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private Long totalProducts;
    private Long totalOrders;
    private Long totalCustomers;
    private Double totalRevenue;
    private Double revenueChange;
    private Long pendingOrders;
}