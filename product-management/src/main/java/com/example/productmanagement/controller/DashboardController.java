package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private StatisticsRepository statisticsRepository;
    
    @GetMapping
    public String showDashboard(Model model) {
        try {
            // 1. Total products count
            Long totalProducts = statisticsRepository.getTotalProducts();
            model.addAttribute("totalProducts", totalProducts != null ? totalProducts : 0);
            
            // 2. Products by category
            List<Object[]> categoryData = statisticsRepository.getProductsByCategory();
            model.addAttribute("categoryData", categoryData);
            
            // 3. Total inventory value
            BigDecimal totalValue = statisticsRepository.getTotalValue();
            model.addAttribute("totalValue", 
                totalValue != null ? totalValue.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            
            // 4. Average product price
            BigDecimal averagePrice = statisticsRepository.getAveragePrice();
            model.addAttribute("averagePrice", 
                averagePrice != null ? averagePrice.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            
            // 5. Low stock alerts (quantity < 10)
            List<Product> lowStockProducts = statisticsRepository.getLowStockProducts(10);
            model.addAttribute("lowStockProducts", lowStockProducts);
            
            // 6. Recent products (last 5 added)
            List<Product> recentProducts = statisticsRepository.getRecentProducts();
            model.addAttribute("recentProducts", recentProducts);
            
        } catch (Exception e) {
            model.addAttribute("error", "Cannot load dashboard data: " + e.getMessage());
        }
        
        return "dashboard";
    }
}