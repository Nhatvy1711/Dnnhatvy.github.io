// File: src/main/java/com/example/productmanagement/repository/StatisticsRepositoryImpl.java

package com.example.productmanagement.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class StatisticsRepositoryImpl implements StatisticsRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Long getTotalProducts() {
        Query query = entityManager.createQuery("SELECT COUNT(p) FROM Product p");
        return (Long) query.getSingleResult();
    }
    
    @Override
    public Long getTotalQuantity() {
        Query query = entityManager.createQuery("SELECT SUM(p.quantity) FROM Product p");
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }
    
    @Override
    public BigDecimal getTotalValue() {
        Query query = entityManager.createQuery("SELECT SUM(p.price * p.quantity) FROM Product p");
        Object result = query.getSingleResult();
        return result != null ? (BigDecimal) result : BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal getAveragePrice() {
        Query query = entityManager.createQuery("SELECT AVG(p.price) FROM Product p");
        Object result = query.getSingleResult();
        return result != null ? (BigDecimal) result : BigDecimal.ZERO;
    }
    
    @Override
    public List<Object[]> getProductsByCategory() {
        Query query = entityManager.createQuery(
            "SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category ORDER BY COUNT(p) DESC");
        return query.getResultList();
    }
    
    @Override
    public List<com.example.productmanagement.entity.Product> getLowStockProducts(Integer threshold) {
        Query query = entityManager.createQuery(
            "SELECT p FROM Product p WHERE p.quantity < :threshold ORDER BY p.quantity ASC");
        query.setParameter("threshold", threshold);
        query.setMaxResults(10);
        return query.getResultList();
    }
    
    @Override
    public List<com.example.productmanagement.entity.Product> getRecentProducts() {
        Query query = entityManager.createQuery(
            "SELECT p FROM Product p ORDER BY p.createdAt DESC");
        query.setMaxResults(5);
        return query.getResultList();
    }
}