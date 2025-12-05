package com.example.productmanagement.service;

import com.example.productmanagement.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    List<Product> getAllProducts();
    
    Optional<Product> getProductById(Long id);
    
    Product saveProduct(Product product);
    
    void deleteProduct(Long id);
    
    List<Product> searchProducts(String keyword);
    
    List<Product> getProductsByCategory(String category);

    //new method
    List<Product> searchAdvanced(String name, String category, BigDecimal minPrice, BigDecimal maxPrice);

    //5.2 
    List<String> getAllCategories();

    //5.3
    Page<Product> searchProducts(String keyword, int page, int size);
    Page<Product> getProductsByCategory(String category, int page, int size);

    List<Product> getAllProducts(Sort sort);
    List<Product> searchProducts(String keyword, Sort sort);

}
