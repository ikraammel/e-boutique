package com.ecommerce.demo.repositories;

import com.ecommerce.demo.dtos.dashboard.CategorySalesDTO;
import com.ecommerce.demo.dtos.dashboard.RecentOrderDTO;
import com.ecommerce.demo.dtos.dashboard.TopProductDTO;
import com.ecommerce.demo.models.Order;
import com.ecommerce.demo.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findByUserIdAndStatusNot(Integer userId, Status status);

    @Query("SELECT SUM(o.total) FROM Order o")
    Double sumTotalRevenue();

    @Query("SELECT SUM(o.total) FROM Order o WHERE o.createdAt >= :startDate")
    Double sumRevenueFromDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") Status status);

    // ✅ CORRIGÉ: Utiliser requête native pour grouper par DATE
    @Query(value = """
        SELECT DATE(created_at) as date, SUM(total) as total
        FROM orders
        WHERE created_at >= :startDate
        GROUP BY DATE(created_at)
        ORDER BY DATE(created_at)
    """, nativeQuery = true)
    List<Object[]> getSalesByDayNative(@Param("startDate") LocalDateTime startDate);

    @Query("""
        SELECT new com.ecommerce.demo.dtos.dashboard.TopProductDTO(
            p.name, 
            SUM(oi.quantity), 
            SUM(oi.price * oi.quantity)
        )
        FROM OrderItem oi
        JOIN oi.product p
        GROUP BY p.id, p.name
        ORDER BY SUM(oi.quantity) DESC
    """)
    List<TopProductDTO> findTopSellingProducts();

    @Query("""
        SELECT new com.ecommerce.demo.dtos.dashboard.RecentOrderDTO(
            o.id, 
            CONCAT(o.user.firstName, ' ', o.user.lastName), 
            o.user.email, 
            o.total, 
            CAST(o.status AS string), 
            SIZE(o.items)
        )
        FROM Order o 
        ORDER BY o.createdAt DESC
    """)
    List<RecentOrderDTO> findRecentOrders();

    @Query("""
        SELECT new com.ecommerce.demo.dtos.dashboard.CategorySalesDTO(
            CAST(p.category AS string), 
            COUNT(oi), 
            SUM(oi.price * oi.quantity)
        )
        FROM OrderItem oi 
        JOIN oi.product p 
        WHERE p.category IS NOT NULL
        GROUP BY p.category
        ORDER BY SUM(oi.price * oi.quantity) DESC
    """)
    List<CategorySalesDTO> getSalesByCategory();
}