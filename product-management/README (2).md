# LAB 7 REPORT: PRODUCT MANAGEMENT APPLICATION ADVANCED FEATURES

## 📊 OVERVIEW

This report details the implementation of advanced features, including multi-criteria search, validation, sorting, filtering, and a comprehensive statistics dashboard, for the Spring Boot-based Product Management application.

* **Implementation Time:** 4-5 hours
* **Technologies Used:** Spring Boot 3.x, Spring Data JPA, Thymeleaf, MySQL, Chart.js, Bootstrap 5
* **Objectives Achieved:** Completed all advanced features and statistics dashboard functionality.

## 📋 TASK 5: ADVANCED SEARCH (12 points)

### 5.1 Multi-Criteria Search (6 points)

The advanced search allows users to combine multiple criteria (name, category, min price, max price) for flexible filtering.

**How it works:**

``` java
// 1. Repository Method with dynamic @Query
@Query("SELECT p FROM Product p WHERE " +
       "(:name IS NULL OR p.name LIKE %:name%) AND " +
       "(:category IS NULL OR p.category = :category) AND " +
       "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
       "(:maxPrice IS NULL OR p.price <= :maxPrice)")
List<Product> searchAdvanced(@Param("name") String name,
                            @Param("category") String category,
                            @Param("minPrice") BigDecimal minPrice,
                            @Param("maxPrice") BigDecimal maxPrice);
```

Explanation:

Uses a dynamic query with IS NULL OR to make all search parameters optional.

LIKE %:name%: searches for product names containing the keywords (case-insensitive).

Controller Processing: The controller receives all optional parameters via @RequestParam(required = false) and passes them to the service layer.example:
`/products/advanced-search?name=laptop&category=Electronics&minPrice=500&maxPrice=2000`

2.  Controller:

``` java
@GetMapping("/advanced-search")
public String advancedSearch(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        Model model) {

    List<Product> products = productService.searchAdvanced(name, category, minPrice, maxPrice);
    model.addAttribute("searchName", name);
    model.addAttribute("searchCategory", category);
}
```

3.  UI:

``` html
<form th:action="@{/products/advanced-search}" method="get">
    <input type="text" name="name">
    <input type="text" name="category">
    <input type="number" name="minPrice">
    <input type="number" name="maxPrice">
    <button type="submit">Search</button>
</form>
```
Advantages: Users can search flexibly with any combination of conditions.

### 5.2 Category Filter

**How it works**
```java
// Repository: Get all unique categories
@Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
List<String> findAllCategories();
```
- Explaination:
1. Repository queries database for all distinct categories
2. Service passes category list to Controller
3. Controller adds categories to Model
4. View displays dropdown with all categories

***Interface***
```html
<select name="category" onchange="this.form.submit()">
    <option value="">All Categories</option>
    <option th:each="cat : ${categories}" 
            th:value="${cat}" 
            th:text="${cat}"
            th:selected="${cat == selectedCategory}">
    </option>
</select>
```

***Mechanism***
- onchange="this.form.submit()": automatically submits form when category is selected
- th:selected: maintains selection after filtering
`URL: /products?category=Electronics`

### 5.3 Search with Pagination

*** How it works ***
``` java
// Repository with Pageable
Page<Product> findByNameContaining(String keyword, Pageable pageable);

// Service implementation
@Override
public Page<Product> searchProducts(String keyword, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return productRepository.findByNameContaining(keyword, pageable);
}
```
- Explaination
1. Pageable object: contains current page information and page size
2. Spring Data JPA automatically handles pagination
3. Page object returns: current page data + pagination information

*** Controller handing pagination ***
``` java
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
    // ... other attributes
}
```

*** Pagination Interface ***
``` html
<nav>
    <ul class="pagination">
        <li th:class="${currentPage == 0} ? 'page-item disabled' : 'page-item'">
            <a th:href="@{/products/search(keyword=${keyword}, page=${currentPage-1})}">Previous</a>
        </li>
        
        <li th:each="i : ${#numbers.sequence(0, totalPages-1)}" 
            th:class="${i == currentPage} ? 'page-item active' : 'page-item'">
            <a th:href="@{/products/search(keyword=${keyword}, page=${i})}" 
               th:text="${i+1}"></a>
        </li>
        
        <li th:class="${currentPage == totalPages-1} ? 'page-item disabled' : 'page-item'">
            <a th:href="@{/products/search(keyword=${keyword}, page=${currentPage+1})}">Next</a>
        </li>
    </ul>
</nav>
```
- Advantages
+ Fast loading with large datasets
+ Better user experience
+ Easy navigation

## Task 6: VALIDATION

### 6.1 Validation Annotations

*** How it works ***
``` java
@Entity
public class Product {
    @NotBlank(message = "Product code is required")
    @Size(min = 3, max = 20, message = "Product code must be 3-20 characters")
    @Pattern(regexp = "^[A-Z][A-Z0-9]{2,19}$", 
             message = "Product code must start with uppercase letter")
    private String productCode;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}
```
- Important annotations:
+ @NotBlank: cannot be empty (for String)
+ @NotNull: cannot be null (for Object)
+ @Size: length constraints
+ @Pattern: regex pattern validation
+ @DecimalMin/@DecimalMax: min/max values for BigDecimal
+ @Min/@Max: min/max values for integers

### 6.2 Controller Valodation

``` java
@PostMapping("/save")
public String saveProduct(@Valid @ModelAttribute("product") Product product,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
    
    // Custom validation: check duplicate product code
    if (product.getId() == null && productService.existsByProductCode(product.getProductCode())) {
        bindingResult.rejectValue("productCode", "error.product", 
            "Product code already exists");
    }
    
    if (bindingResult.hasErrors()) {
        return "product-form"; // Return to form displaying errors
    }
    
    // Save if no errors
    productService.saveProduct(product);
    redirectAttributes.addFlashAttribute("message", "Product saved successfully!");
    return "redirect:/products";
}
```
- Explaintion 
1. @Valid: activates validation on Product object
2. BindingResult: contains all validation errors
3. hasErrors(): checks if there are errors
4. rejectValue(): adds custom errors

### 6.3 Displaying Errors

``` html
<div class="form-group">
    <label for="productCode">Product Code *</label>
    <input type="text" 
           id="productCode" 
           th:field="*{productCode}"
           th:errorclass="is-invalid" />
    <div th:if="${#fields.hasErrors('productCode')}" 
         th:errors="*{productCode}" 
         class="invalid-feedback"></div>
</div>
```
- Error display mechanism
+ th:errorclass="is-invalid": automatically adds class when error exists
+ th:errors="*{productCode}": displays all errors for the field
+ Bootstrap classes: is-invalid, invalid-feedback

## Task 7: SORTING & FILTERING

### 7.1 Sorting Implementation

*** How it works ***

```java
@GetMapping
public String listProducts(
        @RequestParam(required = false) String sortField,
        @RequestParam(defaultValue = "asc") String sortDir,
        Model model) {
    
    // Create Sort object from parameters
    Sort sort = null;
    if (sortField != null && !sortField.isEmpty()) {
        sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortField).descending() : 
            Sort.by(sortField).ascending();
    }
    
    // Get data with sorting
    List<Product> products = sort != null ? 
        productService.getAllProducts(sort) : 
        productService.getAllProducts();
    
    // Add sorting parameters to model to maintain state
    model.addAttribute("sortField", sortField);
    model.addAttribute("sortDir", sortDir);
    
    return "product-list";
}
```

*** Sorting Links in View ***
``` html
<a th:href="@{/products(sortField='name', sortDir=${(sortField=='name' and sortDir=='asc')?'desc':'asc'})}">
    Name
    <span th:if="${sortField=='name'}" 
          th:text="${sortDir=='asc'?'↑':'↓'}"></span>
</a>
```

- Sorting toggle logic:
+ If currently sorting name asc → click becomes name desc
+ Display arrow ↑/↓ accordingly
+ `URL: /products?sortField=price&sortDir=desc`

### 7.2 Combined Sorting and Filtering
``` java
@GetMapping("/search")
public String searchProducts(
        @RequestParam("keyword") String keyword,
        @RequestParam(required = false) String category,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sortField,
        @RequestParam(defaultValue = "asc") String sortDir,
        Model model) {
    
    // Combine all parameters
    Sort sort = (sortField != null) ? Sort.by(sortField).ascending() : null;
    Pageable pageable = PageRequest.of(page, size, sort);
    
    Page<Product> productPage;
    if (category != null) {
        productPage = productRepository.findByNameContainingAndCategory(keyword, category, pageable);
    } else {
        productPage = productRepository.findByNameContaining(keyword, pageable);
    }
    
    // Maintain all parameters
    model.addAttribute("keyword", keyword);
    model.addAttribute("category", category);
    model.addAttribute("sortField", sortField);
    model.addAttribute("sortDir", sortDir);
    // ... pagination attributes
}
```
- Advantage: Users can combine multiple features simultaneously.

## Task 8: STATISTICS DASHBOARD 

### 8.1: Statistics Repository

*** How it works ***

```java
@Repository
public class StatisticsRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // Total products count
    public Long getTotalProducts() {
        Query query = entityManager.createQuery("SELECT COUNT(p) FROM Product p");
        return (Long) query.getSingleResult();
    }
    
    // Products by category
    public List<Object[]> getProductsByCategory() {
        Query query = entityManager.createQuery(
            "SELECT p.category, COUNT(p) FROM Product p WHERE p.category IS NOT NULL GROUP BY p.category");
        return query.getResultList();
    }
    
    // Total inventory value
    public BigDecimal getTotalValue() {
        Query query = entityManager.createQuery(
            "SELECT SUM(p.price * p.quantity) FROM Product p");
        return (BigDecimal) query.getSingleResult();
    }
    
    // Low stock products
    public List<Product> getLowStockProducts(Integer threshold) {
        Query query = entityManager.createQuery(
            "SELECT p FROM Product p WHERE p.quantity < :threshold ORDER BY p.quantity ASC");
        query.setParameter("threshold", threshold);
        query.setMaxResults(10);
        return query.getResultList();
    }
    
    // 5 most recent products
    public List<Product> getRecentProducts() {
        Query query = entityManager.createQuery(
            "SELECT p FROM Product p ORDER BY p.createdAt DESC");
        query.setMaxResults(5);
        return query.getResultList();
    }
}
```

- Explaination of important queries
1. COUNT() → count total records
2. GROUP BY → group by category
3. SUM(price * quantity) → calculate total value
4. ORDER BY createdAt DESC → sort newest first
5. MAXRESULTS → limit number of results

### 8.2 Dashboard Controller

``` java
@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private StatisticsRepository statisticsRepository;
    
    @GetMapping
    public String showDashboard(Model model) {
        // 1. Total products count
        model.addAttribute("totalProducts", statisticsRepository.getTotalProducts());
        
        // 2. Products by category (for pie chart)
        model.addAttribute("categoryData", statisticsRepository.getProductsByCategory());
        
        // 3. Total inventory value
        BigDecimal totalValue = statisticsRepository.getTotalValue();
        model.addAttribute("totalValue", totalValue.setScale(2, RoundingMode.HALF_UP));
        
        // 4. Average product price
        BigDecimal avgPrice = statisticsRepository.getAveragePrice();
        model.addAttribute("averagePrice", avgPrice.setScale(2, RoundingMode.HALF_UP));
        
        // 5. Low stock alerts
        model.addAttribute("lowStockProducts", statisticsRepository.getLowStockProducts(10));
        
        // 6. Recent products
        model.addAttribute("recentProducts", statisticsRepository.getRecentProducts());
        
        return "dashboard";
    }
}
```

### 8.3 Dashboard View with Chart.js

*** Chart.js Integration ***

```html
<canvas id="categoryChart"></canvas>

<script th:inline="javascript">
/*<![CDATA[*/
// Prepare data from Thymeleaf
const labels = [];
const data = [];

/*[# th:each="category : ${categoryData}"]*/
labels.push(/*[[${category[0]}]]*/);
data.push(/*[[${category[1]}]]*/);
/*[/]*/

// Create chart
const ctx = document.getElementById('categoryChart').getContext('2d');
new Chart(ctx, {
    type: 'pie',
    data: {
        labels: labels,
        datasets: [{
            data: data,
            backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56']
        }]
    }
});
/*]]>*/
</script>
```

- Important techniques:
+ `th:inline="javascript"`: allows Thymeleaf to process JavaScript
+ `/*<![CDATA[*/ ... /*]]>*/`: protects JavaScript code
+ `/*[[${variable}]]*/`: injects values from server into JavaScript

- Dashboard components:
+ Statistics Cards: Display overview statistics
+ Pie Chart: Product distribution by category
+ Low Stock Table: Alerts for products running low
+ Recent Products: 5 newest products
+ Progress Bars: Display percentages by category