# Detailed Report on System Operation

## Question 1: Create Project & Configure Database

### 1.1. Create Spring Boot Project (5 points)

How it works:

-   Spring Initializr creates project framework with standard Maven structure

- Key Dependencies

-   **Spring Web**: Handles HTTP requests/responses\
-   **Spring Data JPA**: Provides ORM and repository pattern\
-   **MySQL Driver**: Connects to MySQL database\
-   **Thymeleaf**: Template engine for the view layer\
-   **DevTools**: Auto-reload when code changes

Project structure:

    product-management/
    ├── src/main/java/
    │   └── com/example/productmanagement/
    ├── src/main/resources/
    │   ├── templates/          # Thymeleaf files
    │   └── application.properties
    └── pom.xml                 # Maven dependencies

### 1.2. Database Setup (5 points)

How it works:

``` sql
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT DEFAULT 0,
    category VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 1.3. application.properties Configuration (5 points)

Explanation:

    spring.datasource.url=jdbc:mysql://localhost:3306/product_management?useSSL=false&serverTimezone=UTC

Connects to MySQL database.

    spring.jpa.hibernate.ddl-auto=update

Updates schema automatically based on entities.

Values:\
- `create` -- Drops and recreates tables\
- `update` -- Updates schema without losing data\
- `validate` -- Only checks schema\
- `create-drop` -- Creates on start, drops on shutdown

    spring.jpa.show-sql=true

Shows SQL queries for debugging.

    spring.thymeleaf.cache=false

Disables template caching (for development).

------------------------------------------------------------------------

## Question 2: Entity & Repository Layers

### 2.1. Product Entity (10 points)

JPA annotations mechanism:

``` java
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="product_code", unique=true, nullable=false)
    private String productCode;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
```

Entity lifecycle:

1.  New: `Product product = new Product();`\
2.  Before saving: `@PrePersist` sets `createdAt`\
3.  Saving: `repository.save(product)`\
4.  Becomes managed entity: Can track changes

### 2.2. Product Repository (5 points)

How Spring Data JPA works:

``` java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    List<Product> findByNameContaining(String keyword);

    boolean existsByProductCode(String productCode);
}
```

Method Naming Magic
- Pattern: findBy + Property + Condition
- Example: findByPriceBetween(BigDecimal min, BigDecimal max)
- Spring automatically converts to SQL quer

### 2.3. Test Repository (5 points)

`CommandLineRunner` mechanism:

``` java
@Bean
CommandLineRunner test(ProductRepository repository) {
    return args -> {
        System.out.println("Total products: " + repository.count());
    };
}
```

------------------------------------------------------------------------

## Question 3: Service Layer (10 points)

### 3.1. Service Interface & Implementation

Dependency Injection:

``` java
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}
```

Transaction mechanism:

- Start Transaction: When service method is called
- Execute Database Operations
- Commit: If successful
- Rollback: If exception occurs

Example:

``` java
@Transactional
public Product saveProduct(Product product) {
    return productRepository.save(product);
}
```

------------------------------------------------------------------------

## Question 4: Controller & Views (15 points)

### 4.1. Product Controller (8 points)

HTTP request flow:

- Client sends request → DispatcherServlet receives request
- Find appropriate Controller based on @RequestMapping
- Call corresponding method with parameter binding
- Process business logic through Service layer
- Return view name or redirect

Example endpoint:

``` java
@GetMapping
public String listProducts(Model model) {
    model.addAttribute("products", productService.getAllProducts());
    return "product-list";
}
```

Parameter binding & flash attributes:

``` java
@GetMapping("/edit/{id}")
public String showEditForm(@PathVariable Long id,
                           Model model,
                           RedirectAttributes redirectAttributes) {

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
```
Flash Attributes Mechanism:
- Exist only during one redirect request
- Automatically deleted after reading
- Perfect for success/error messages

### 4.2. Thymeleaf Views (7 points)

Template rendering:

`product-list.html` -- Flash messages, search form, product table
``` html
<!-- Display flash messages -->
<div th:if="${message}" class="alert alert-success">
    <span th:text="${message}">Success message</span>
</div>

<!-- Search form with GET method -->
<form th:action="@{/products/search}" method="get">
    <input type="text" name="keyword" th:value="${keyword}" />
    <button type="submit">Search</button>
</form>

<!-- Loop through products -->
<tr th:each="product : ${products}">
    <td th:text="${product.id}">1</td>
    <td th:text="${product.name}">Product Name</td>
    <!-- Format currency -->
    <td th:text="'$' + ${#numbers.formatDecimal(product.price, 1, 2)}">$99.99</td>
    
    <!-- Links with path parameters -->
    <td>
        <a th:href="@{/products/edit/{id}(id=${product.id})}">Edit</a>
        <a th:href="@{/products/delete/{id}(id=${product.id})}" 
           onclick="return confirm('Delete?')">Delete</a>
    </td>
</tr>
```

`product-form.html` -- Form binding with product object
``` html
<!-- Form binding with Product object -->
<form th:action="@{/products/save}" th:object="${product}" method="post">
    
    <!-- Hidden field for ID (for update) -->
    <input type="hidden" th:field="*{id}" />
    
    <!-- Input fields with th:field -->
    <input type="text" th:field="*{name}" />
    <!-- Equivalent to: -->
    <input type="text" id="name" name="name" th:value="*{name}" />
    
    <!-- Dynamic title -->
    <h1 th:text="${product.id != null} ? 'Edit Product' : 'Add Product'">
        Product Form
    </h1>
</form>
```

Form binding mechanism:

1.  GET request: Controller adds empty Product to Model
2. Form display: Thymeleaf renders form with th:object
3. User submit: Browser sends form data via POST
4. Spring binding: Automatically maps form fields to Product object
5. Validation: Can add @Valid for validation

------------------------------------------------------------------------

## Full Data Flow (Add New Product)

1.  User clicks **Add New Product**
2.  GET `/products/new` → form displayed
3.  User fills and submits form
4.  Spring binds form to Product object
5.  Service saves inside a transaction
6.  Repository inserts into DB
7.  Flash message added
8.  Redirect to product list

Example INSERT:

``` sql
INSERT INTO products (product_code, name, price, quantity, category, created_at)
VALUES ('P006', 'New Laptop', 999.99, 10, 'Electronics', '2024-11-03 10:00:00');
```
## Output
<!-- <img src="C:\Users\ACER\OneDrive - VietNam National University - HCM INTERNATIONAL UNIVERSITY\Documents\Sound Recordings.jpg"> -->
