# Project Setup & Entity -- How It Works

## EXERCISE 1 -- PROJECT SETUP & ENTITY

### 1.1 Project Creation -- How It Works

When you use **Spring Initializr**, it automatically:

-   Creates a standard Maven project structure:

        src/main/java
        src/main/resources
        pom.xml

-   Imports the dependencies you selected:

    -   **Spring Web** -- create REST APIs
    -   **Spring Data JPA** -- interact with DB
    -   **MySQL Driver** -- connect to MySQL
    -   **Validation** -- validate DTO requests

When you open the project, Spring Boot looks for:

    src/main/java/.../CustomerApiApplication.java

and runs `main()`.

Spring Boot auto-configures everything based on the starter
dependencies.

➡️ **No XML configuration needed** --- Spring Boot handles it
automatically.

------------------------------------------------------------------------

### 1.2 Database Setup -- How It Works

#### 1 MySQL

You create a database and the `customers` table.

Spring Boot connects to MySQL using:

`application.properties`:

    spring.datasource.url=jdbc:mysql://localhost:3306/customer_management
    spring.datasource.username=root
    spring.datasource.password=your_password

When the app starts:

1.  Spring reads datasource configuration\
2.  Spring creates a DataSource object\
3.  JPA establishes DB connection\
4.  If `hibernate.ddl-auto=update` → Hibernate maps Entity ↔ Table
    automatically

------------------------------------------------------------------------

### 1.3 Customer Entity -- How It Works

You create the class:

    Customer.java

To map it to the `customers` table, use:

  Annotation                       Meaning
  -------------------------------- ---------------------------
  `@Entity`                        Marks class as JPA entity
  `@Table(name="customers")`       Maps to DB table
  `@Id`                            Primary key
  `@GeneratedValue`                Auto-increment
  `@Column`                        Column constraints
  `@Enumerated(EnumType.STRING)`   Stores enum as text
  `@PrePersist`                    Runs before INSERT
  `@PreUpdate`                     Runs before UPDATE

Lifecycle callback example:

``` java
@PrePersist
public void onCreate() {
    this.createdAt = LocalDateTime.now();
}
```

➡️ Runs automatically before the first `.save()` -- similar to a DB
trigger, but in Java.

------------------------------------------------------------------------

## EXERCISE 2 -- DTO LAYER (How It Works)

### 2.1 Request DTO -- Mechanism

Request DTO receives data from the client.

Why not use Entity for request?

-   Avoid exposing internal fields (e.g., id, createdAt)
-   Separate API logic from database logic
-   Validation should occur at request level

When client sends JSON:

``` json
{
  "customerCode": "C001",
  "fullName": "John Doe",
  "email": "john@example.com"
}
```

Spring:

1.  Converts JSON → `CustomerRequestDTO`
2.  Runs validation (`@NotBlank`, `@Email`, etc.)
3.  If invalid → throws `MethodArgumentNotValidException`
4.  GlobalExceptionHandler returns error JSON

------------------------------------------------------------------------

### 2.2 Response DTO -- Mechanism

Response DTO helps:

-   Prevent returning Entity directly
-   Hide sensitive fields
-   Format API response

Flow:

    Entity → Response DTO → JSON

------------------------------------------------------------------------

### 2.3 Error Response DTO -- Mechanism

When an error occurs:

1.  Exception is thrown
2.  Handler creates `ErrorResponseDTO`
3.  Spring converts it → JSON

Example:

``` json
{
  "timestamp": "...",
  "status": 400,
  "error": "BAD REQUEST",
  "message": "Validation failed",
  "path": "/api/customers",
  "details": [
    "Email is invalid",
    "Customer code format is wrong"
  ]
}
```

------------------------------------------------------------------------

## EXERCISE 3 -- REPOSITORY & SERVICE

### 3.1 Repository -- How It Works

When you extend:

``` java
public interface CustomerRepository extends JpaRepository<Customer, Long>
```

Spring Boot:

-   Automatically generates CRUD methods:\
    `save()`, `findAll()`, `findById()`, `delete()`
-   Creates repository instance using Spring Container

Custom methods:

``` java
Optional<Customer> findByEmail(String email);
boolean existsByCustomerCode(String code);
List<Customer> findByStatus(CustomerStatus status);
```

Spring parses method names → generates SQL automatically.

Example:\
`findByEmail` → SQL:

    select * from customers where email = ?

------------------------------------------------------------------------

### 3.2 Service -- How It Works

Service layer handles:

-   Business logic
-   DB validation using repository
-   DTO ↔ Entity conversion

Flow for creating a customer:

    Controller → Service.createCustomer() → Repository.save() → Entity saved

Operations:

-   Check duplicates with repository
-   Convert DTO → Entity
-   Save to DB
-   Convert Entity → Response DTO

------------------------------------------------------------------------

### 3.3 Conversion Helpers

You manually create:

    convertToResponseDTO()
    convertToEntity()

Spring does **not** auto-map --- you handle it.

------------------------------------------------------------------------

## EXERCISE 4 -- REST CONTROLLER

### How It Works

The REST Controller:

-   Receives requests
-   Calls service layer
-   Returns ResponseEntity + status code

Common annotations:

  Annotation                            Meaning
  ------------------------------------- -----------------------
  `@RestController`                     Returns JSON
  `@RequestMapping("/api/customers")`   Base URL
  `@CrossOrigin("*")`                   Allow frontend access
  `@GetMapping`                         GET
  `@PostMapping`                        POST
  `@PutMapping`                         PUT
  `@DeleteMapping`                      DELETE
  `@Valid`                              Activates validation

Example flow (GET):

    /api/customers
    → controller
    → service.getAllCustomers()
    → repository
    → convert Entity → DTO
    → return JSON

------------------------------------------------------------------------

### 4.2 Exception Handling -- Mechanism

When an exception occurs:

1.  Spring checks `@RestControllerAdvice`
2.  Handler formats error JSON
3.  Returns correct HTTP status

Example:

``` json
{
  "status": 404,
  "error": "NOT FOUND",
  "message": "Customer not found"
}
```

------------------------------------------------------------------------

## 🎉 SUMMARY OF THE WHOLE SYSTEM

  Layer               Responsibility     How It Works
  ------------------- ------------------ -------------------------
  Entity              Map to DB table    JPA reads annotations
  DTO                 API input/output   Auto JSON ↔ DTO
  Repository          CRUD operations    Spring generates SQL
  Service             Business logic     Uses repo + handles DTO
  Controller          REST APIs          Routes requests
  Exception Handler   Error formatting   Auto JSON errors
