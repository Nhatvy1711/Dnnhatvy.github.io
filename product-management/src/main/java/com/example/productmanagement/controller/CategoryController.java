// File: src/main/java/com/example/productmanagement/controller/CategoryController.java

package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public String listCategories(Model model) {
        List<String> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);
        return "categories";
    }
    
    @GetMapping("/{category}")
    public String filterByCategory(
            @PathVariable String category,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        
        List<Product> products = productService.getProductsByCategory(category);
        List<String> categories = productService.getAllCategories();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        
        return "product-list";
    }
}