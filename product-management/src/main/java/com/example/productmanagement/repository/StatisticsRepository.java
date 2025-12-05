// File: src/main/java/com/example/productmanagement/repository/StatisticsRepository.java

package com.example.productmanagement.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface StatisticsRepository {
    
    @Query("SELECT COUNT(p) FROM Product p")
    Long getTotalProducts();
    
    @Query("SELECT SUM(p.quantity) FROM Product p")
    Long getTotalQuantity();
    
    @Query("SELECT SUM(p.price * p.quantity) FROM Product p")
    BigDecimal getTotalValue();
    
    @Query("SELECT AVG(p.price) FROM Product p")
    BigDecimal getAveragePrice();
    
    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category ORDER BY COUNT(p) DESC")
    List<Object[]> getProductsByCategory();
    
    @Query("SELECT p FROM Product p WHERE p.quantity < :threshold ORDER BY p.quantity ASC")
    List<com.example.productmanagement.entity.Product> getLowStockProducts(@Param("threshold") Integer threshold);
    
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC LIMIT 5")
    List<com.example.productmanagement.entity.Product> getRecentProducts();
}