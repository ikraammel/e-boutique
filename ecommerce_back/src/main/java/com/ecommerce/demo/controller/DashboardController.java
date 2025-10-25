package com.ecommerce.demo.controller;

import com.ecommerce.demo.dtos.dashboard.*;
import com.ecommerce.demo.models.Role;
import com.ecommerce.demo.models.Status;
import com.ecommerce.demo.repositories.OrderRepository;
import com.ecommerce.demo.repositories.ProductRepository;
import com.ecommerce.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class DashboardController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    // -------------------- STATS GLOBALES --------------------
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(
            @RequestParam(defaultValue = "7") int days
    ) {
        Long totalProducts = productRepository.count();
        Long totalOrders = orderRepository.count();
        Long totalCustomers = userRepository.countByRole(Role.USER);
        Double totalRevenue = orderRepository.sumTotalRevenue();
        if (totalRevenue == null) totalRevenue = 0.0;

        LocalDateTime periodStart = LocalDateTime.now().minusDays(days);
        LocalDateTime previousPeriodStart = LocalDateTime.now().minusDays(days * 2);

        Double recentRevenue = orderRepository.sumRevenueFromDate(periodStart);
        Double previousRevenue = orderRepository.sumRevenueFromDate(previousPeriodStart);
        if (recentRevenue == null) recentRevenue = 0.0;
        if (previousRevenue == null) previousRevenue = 0.0;

        Double revenueChange = previousRevenue > 0 ? ((recentRevenue - previousRevenue) / previousRevenue) * 100 : 0.0;

        Long pendingOrders = orderRepository.countByStatus(Status.PENDING);

        DashboardStatsDTO stats = new DashboardStatsDTO(
                totalProducts, totalOrders, totalCustomers, totalRevenue, revenueChange, pendingOrders
        );

        log.info("Dashboard Stats: {}", stats);
        return ResponseEntity.ok(stats);
    }

    // -------------------- VENTES PAR JOUR --------------------
    @GetMapping("/sales-chart")
    public ResponseEntity<List<SalesDataDTO>> getSalesChart(@RequestParam(defaultValue = "7") int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        List<Object[]> rawData = orderRepository.getSalesByDayNative(startDate);
        log.info("Raw sales data ({} days): {}", days, rawData);

        List<SalesDataDTO> salesData = rawData.stream()
                .map(row -> {
                    LocalDateTime dateTime;
                    if (row[0] instanceof java.sql.Timestamp ts) {
                        dateTime = ts.toLocalDateTime();
                    } else if (row[0] instanceof java.sql.Date d) {
                        dateTime = d.toLocalDate().atStartOfDay();
                    } else {
                        dateTime = LocalDateTime.now();
                    }
                    Double total = ((Number) row[1]).doubleValue();
                    return new SalesDataDTO(dateTime, total);
                })
                .collect(Collectors.toList());

        if (salesData.isEmpty()) log.warn("Sales data is empty for the last {} days.", days);
        return ResponseEntity.ok(salesData);
    }

    // -------------------- TOP PRODUITS --------------------
    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDTO>> getTopProducts(@RequestParam(defaultValue = "5") int limit) {
        List<TopProductDTO> topProducts = orderRepository.findTopSellingProducts();
        log.info("Top products: {}", topProducts);
        if (topProducts.isEmpty()) log.warn("No top products found.");
        return ResponseEntity.ok(topProducts.stream().limit(limit).toList());
    }

    // -------------------- COMMANDES RÉCENTES --------------------
    @GetMapping("/recent-orders")
    public ResponseEntity<List<RecentOrderDTO>> getRecentOrders(@RequestParam(defaultValue = "5") int limit) {
        List<RecentOrderDTO> recentOrders = orderRepository.findRecentOrders();
        log.info("Recent orders: {}", recentOrders);
        if (recentOrders.isEmpty()) log.warn("No recent orders found.");
        return ResponseEntity.ok(recentOrders.stream().limit(limit).toList());
    }

    // -------------------- VENTES PAR CATÉGORIE --------------------
    @GetMapping("/category-sales")
    public ResponseEntity<List<CategorySalesDTO>> getCategorySales() {
        List<CategorySalesDTO> categorySales = orderRepository.getSalesByCategory();
        log.info("Category sales: {}", categorySales);
        if (categorySales.isEmpty()) log.warn("No category sales found.");
        return ResponseEntity.ok(categorySales);
    }
}
