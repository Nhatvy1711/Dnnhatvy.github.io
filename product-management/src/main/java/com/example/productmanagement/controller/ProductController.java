package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import com.example.productmanagement.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository; 
    }
    
    // List all products
    @GetMapping
    public String listProducts(@RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String filterCategory, Model model) {
            // Tạo Sort object nếu có sortField
        Sort sort = null;
        if (sortField != null && !sortField.isEmpty()) {
            sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortField).descending() : 
                Sort.by(sortField).ascending();
        }    
        List<Product> products = productService.getAllProducts();
        if (filterCategory != null && !filterCategory.isEmpty()) {
            products = productService.getProductsByCategory(filterCategory);
            if (sort != null) {
                products = products.stream()
                    .sorted((p1, p2) -> {
                        // Manual sorting based on sortField
                        // Implement based on your needs
                        return 0;
                    })
                    .collect(Collectors.toList());
            }
        } else {
            products = sort != null ? 
                productService.getAllProducts(sort) : 
                productService.getAllProducts();
        }
        // Lấy danh sách categories cho filter
        List<String> categories = productService.getAllCategories();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("filterCategory", filterCategory);
        return "product-list";  // Returns product-list.html
    }
    
    // Show form for new product
    @GetMapping("/new")
    public String showNewForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "product-form";
    }
    
    // Show form for editing product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Product not found");
                    return "redirect:/products";
                });
    }
    
    // Save product (create or update)
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,BindingResult bindingResult, Model model,  RedirectAttributes redirectAttributes) {
        if (product.getId() == null && productRepository.existsByProductCode(product.getProductCode())) {
            bindingResult.rejectValue("productCode", "error.product", 
                "Product code already exists");
        }
        
        if (bindingResult.hasErrors()) {
            return "product-form";
        }
        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("message", 
                    product.getId() == null ? "Product added successfully!" : "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving product: " + e.getMessage());
        }
        return "redirect:/products";
    }
    
    // Delete product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }
    
    // Search products
    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products = productService.searchProducts(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "product-list";
    }

    @GetMapping("/advanced-search")
    public String advancedSearch(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        Model model) {
    
    List<Product> products = productService.searchAdvanced(name, category, minPrice, maxPrice);
    model.addAttribute("products", products);
    model.addAttribute("searchName", name);
    model.addAttribute("searchCategory", category);
    model.addAttribute("minPrice", minPrice);
    model.addAttribute("maxPrice", maxPrice);
    
    return "product-list";
    }
    @GetMapping("/search")
    public String searchProducts(
    @RequestParam("keyword") String keyword,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    Model model) {
    
    Page<Product> productPage = productService.searchProducts(keyword, page, size);
    
    model.addAttribute("products", productPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", productPage.getTotalPages());
    model.addAttribute("totalItems", productPage.getTotalElements());
    model.addAttribute("pageSize", size);
    model.addAttribute("keyword", keyword);
    
    return "product-list";
}

}
