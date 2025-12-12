# Customer API Project - Technical Implementation Report (Exercises 5-8)

## Exercise 5: Search & Filter Endpoints 

### 5.1 Implementation Overview

Enhanced the Customer API with three search capabilities:
- Simple keyword search across multiple fields
- Status-based filtering using enum values
- Advanced multi-criteria search with optional parameters

### 5.2 Technical Implementation

#### 5.2.1 Repository Layer Enhancements
```java
// Advanced JPQL query with dynamic WHERE clauses
@Query("SELECT c FROM Customer c WHERE " +
       "(:name IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
       "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
       "(:status IS NULL OR c.status = :status) AND " +
       "(:phone IS NULL OR c.phone LIKE CONCAT('%', :phone, '%')) AND " +
       "(:customerCode IS NULL OR LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :customerCode, '%')))")
List<Customer> advancedSearch(@Param("name") String name,
                              @Param("email") String email,
                              @Param("status") CustomerStatus status,
                              @Param("phone") String phone,
                              @Param("customerCode") String customerCode);
```
Key Features:
- Case-insensitive search using LOWER() function
- NULL-safe conditions for optional parameters
- Partial matching with LIKE and % wildcards
- Enum type handling for status filtering      

#### 5.2.2 Service Layer Implementation
```java
@Override
public List<CustomerResponseDTO> advancedSearch(AdvancedSearchDTO searchDTO) {
    CustomerStatus statusEnum = null;
    
    // Convert String to Enum with validation
    if (searchDTO.getStatus() != null && !searchDTO.getStatus().trim().isEmpty()) {
        try {
            statusEnum = CustomerStatus.valueOf(searchDTO.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Graceful handling: treat invalid status as no filter
        }
    }
    
    return customerRepository.advancedSearch(
        searchDTO.getName(),
        searchDTO.getEmail(),
        statusEnum,
        searchDTO.getPhone(),
        searchDTO.getCustomerCode()
    ).stream().map(this::convertToResponseDTO).collect(Collectors.toList());
}
```
#### 5.2.3 REST Endpoints

| Endpoint                          | Method | Description                               | Sample Request                 |
|----------------------------------|--------|-------------------------------------------|--------------------------------|
| `/api/customers/search`          | GET    | Keyword search across name, email, code   | `?keyword=john`                |
| `/api/customers/status/{status}` | GET    | Filter by ACTIVE/INACTIVE status          | `/status/ACTIVE`               |
| `/api/customers/advanced-search` | GET    | Multi-criteria search                      | `?name=john&status=ACTIVE`     |

### 5.3 How It Works
1. Request Flow: Client → Controller → Service → Repository → Database
2. Query Building: Dynamic WHERE clauses based on provided parameters
3. Result Mapping: Entity → DTO transformation for clean API responses
4. Error Handling: Graceful handling of invalid status values

## Exercise 6: Pagination & Sorting 

### 6.1 Implementation Strategy
Implemented three levels of data retrieval optimization:
1. Basic Pagination: Page and size parameters
2. Sorting: Single-field asc/desc ordering
3. Combined: Pagination with sorting

### 6.2 Technical Implementation

#### 6.2.1 Repository Integration
```java 
// Spring Data JPA automatically handles pagination and sorting
// No custom repository methods needed - uses findAll(Pageable)
```

#### 6.2.2 Service Layer Pagination
``` java
@Override
public Page<CustomerResponseDTO> getAllCustomersPaginated(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return customerRepository.findAll(pageable)
            .map(this::convertToResponseDTO);
}

@Override
public Page<CustomerResponseDTO> getAllCustomersPaginatedAndSorted(int page, int size, 
                                                                   String sortBy, String sortDir) {
    Sort sort = sortDir.equalsIgnoreCase("desc") 
        ? Sort.by(sortBy).descending() 
        : Sort.by(sortBy).ascending();
    
    Pageable pageable = PageRequest.of(page, size, sort);
    return customerRepository.findAll(pageable)
            .map(this::convertToResponseDTO);
}
```

#### 6.2.3 Controller Endpoints
```java
@GetMapping("/paginated")
public ResponseEntity<Map<String, Object>> getAllCustomersPaginated(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Page<CustomerResponseDTO> customerPage = customerService.getAllCustomersPaginated(page, size);
    
    Map<String, Object> response = new HashMap<>();
    response.put("customers", customerPage.getContent());
    response.put("currentPage", customerPage.getNumber());
    response.put("totalItems", customerPage.getTotalElements());
    response.put("totalPages", customerPage.getTotalPages());
    response.put("pageSize", customerPage.getSize());
    
    return ResponseEntity.ok(response);
}
```

### 6.3 How It Works
1. Pageable Object: Spring's PageRequest creates pagination metadata
2. Database Optimization: Queries use LIMIT and OFFSET for efficiency
3. Response Enrichment: Metadata included with data for client navigation
4. Default Values: Sensible defaults (page=0, size=10) for better UX

### 6.4 Performance Benefits
- Reduced Memory Usage: Only loads requested page of data
- Faster Response Times: Smaller data payloads
- Scalability: Handles large datasets efficiently
- Client Control: Clients decide how much data to request

## Exercise 7: Partial Update with PATCH 

### 7.1 Problem Statement
Traditional PUT requires sending complete resource representation. PATCH allows partial updates, reducing bandwidth and preventing accidental data loss.

### 7.2 Implementation Details

#### 7.2.1 DTO Design
```java 
public class CustomerUpdateDTO {
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    private String fullName;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Invalid phone number format")
    private String phone;
    
    @Size(max = 500, message = "Address too long")
    private String address;
    
    private String status;
    
    // All fields are nullable for partial updates
}
```

#### 7.2.2 Service Implementation
```java
@Override
public CustomerResponseDTO partialUpdateCustomer(Long id, CustomerUpdateDTO updateDTO) {
    Customer existingCustomer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    
    // Check email uniqueness if being changed
    if (updateDTO.getEmail() != null && 
        !existingCustomer.getEmail().equals(updateDTO.getEmail()) && 
        customerRepository.existsByEmail(updateDTO.getEmail())) {
        throw new DuplicateResourceException("Email already exists");
    }
    
    // Update only non-null fields (partial update)
    if (updateDTO.getFullName() != null) {
        existingCustomer.setFullName(updateDTO.getFullName());
    }
    if (updateDTO.getEmail() != null) {
        existingCustomer.setEmail(updateDTO.getEmail());
    }
    // ... similar for other fields
    
    return convertToResponseDTO(customerRepository.save(existingCustomer));
}
```

#### 7.2.3 HTTP Method Comparison

| Aspect         | PUT                          | PATCH                                   |
|----------------|------------------------------|------------------------------------------|
| Semantics      | Replace entire resource       | Modify part of resource                  |
| Idempotent     | Yes                           | No (can be made idempotent)              |
| Request Body   | Complete representation       | Only changed fields                       |
| Bandwidth      | Higher                        | Lower                                     |
| Use Case       | Complete updates              | Partial modifications                     |

### 7.4 How It Works
1. Null Checking: Only updates fields with non-null values
2. Validation: Applies validation only to provided fields
3. Business Rules: Still enforces constraints (unique email)
4. Audit Trail: @PreUpdate triggers update timestamp

## Exercise 8: API Documentation 

### 8.1 Documentation Strategy
Implemented dual documentation approach:
1. Static Documentation: Comprehensive `API_DOCUMENTATION.md`
2.Interactive Testing: Postman collection for hands-on testing

### 8.2 API Documentation File Structure
```text
API_DOCUMENTATION.md
├── Base URL & Authentication
├── Endpoints (12 total)
│   ├── Request/Response examples
│   ├── Validation rules
│   └── Error responses
├── Status Codes Summary
└── Testing Examples
```

### 8.3 Key Documentation Elements

#### 8.3.1 Endpoint Documentation
Each endpoint includes:
- HTTP Method and URL
- Path/Query parameters
- Request body schema (for POST/PUT/PATCH)
- Success response with example
- Error responses with all possible status codes
- Validation rules reference

#### 8.3.2 Error Response Examples
```json
{
    "timestamp": "2025-12-13T00:42:34.9824156",
    "status": 400,
    "error": "Validation Failed",
    "message": "Invalid input data",
    "path": "/api/customers",
    "details": [
        "email: Invalid email format",
        "customerCode: Must start with C followed by numbers"
    ]
}
```

#### 8.3.3 Status Code Reference

| Code | Meaning        | Typical Scenarios                         |
|------|----------------|--------------------------------------------|
| 200  | OK             | Successful GET, PUT, PATCH, DELETE         |
| 201  | Created        | Resource creation successful               |
| 400  | Bad Request    | Validation errors, malformed JSON          |
| 404  | Not Found      | Resource doesn't exist                     |
| 409  | Conflict       | Duplicate email/customer code              |
| 500  | Server Error   | Unhandled exceptions                       |

### 8.4 Postman Collection
Created comprehensive collection with:
- Environment variables for base URL
- Pre-request scripts for setup
- Test scripts for validation
- Folder organization by resource type
- Example requests for all endpoints

#### Layered Architecture
```text
┌─────────────────────────────────────┐
│         Presentation Layer          │
│  ┌─────────────────────────────┐  │
│  │    CustomerRestController    │  │
│  └─────────────────────────────┘  │
└─────────────────────────────────────┘
                ↓
┌─────────────────────────────────────┐
│          Business Logic Layer       │
│  ┌─────────────────────────────┐  │
│  │      CustomerService        │  │
│  │   CustomerServiceImpl       │  │
│  └─────────────────────────────┘  │
└─────────────────────────────────────┘
                ↓
┌─────────────────────────────────────┐
│          Data Access Layer          │
│  ┌─────────────────────────────┐  │
│  │     CustomerRepository      │  │
│  └─────────────────────────────┘  │
└─────────────────────────────────────┘
                ↓
┌─────────────────────────────────────┐
│            Database Layer           │
│        (H2 / MySQL / PostgreSQL)    │
└─────────────────────────────────────┘
```
Key Design Patterns:
1. DTO Pattern: Separation of API contracts from entity models
2. Repository Pattern: Abstract data access layer
3. Dependency Injection: Loose coupling between components
4. Exception Handling: Global exception handler with consistent responses
5. Validation: Bean Validation with custom messages

#### Successful Test Cases

| Endpoint                        | Status       | Notes                                      |
|---------------------------------|--------------|---------------------------------------------|
| GET /api/customers              | ✅ 200 OK    | Returns empty array or customer list        |
| POST /api/customers             | ✅ 201 Created | Validates input, returns created resource  |
| GET /api/customers/{id}         | ✅ 200 OK    | Returns customer by ID                      |
| PUT /api/customers/{id}         | ✅ 200 OK    | Full update with validation                 |
| PATCH /api/customers/{id}       | ✅ 200 OK    | Partial update working                      |
| DELETE /api/customers/{id}      | ✅ 200 OK    | Successful deletion                         |
| GET /api/customers/search       | ✅ 200 OK    | Keyword search functional                   |
| GET /api/customers/paginated    | ✅ 200 OK    | Pagination with metadata                    |
| GET /api/customers/sorted       | ✅ 200 OK    | Sorting by various fields                   |









