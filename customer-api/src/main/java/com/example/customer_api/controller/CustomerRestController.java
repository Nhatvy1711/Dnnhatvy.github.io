package com.example.customer_api.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.customer_api.dto.AdvancedSearchDTO;
import com.example.customer_api.dto.CustomerRequestDTO;
import com.example.customer_api.dto.CustomerResponseDTO;
import com.example.customer_api.dto.CustomerUpdateDTO;
import com.example.customer_api.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")  // Allow CORS for frontend
public class CustomerRestController {
    
    private final CustomerService customerService;
    
    @Autowired
    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    // GET all customers
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
    
    // GET customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
    
    // POST create new customer
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO createdCustomer = customerService.createCustomer(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }
    
    // PUT update customer
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO updatedCustomer = customerService.updateCustomer(id, requestDTO);
        return ResponseEntity.ok(updatedCustomer);
    }
    
    // DELETE customer
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Customer deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    // GET search customers
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomers(@RequestParam String keyword) {
        List<CustomerResponseDTO> customers = customerService.searchCustomers(keyword);
        return ResponseEntity.ok(customers);
    }
    
    // GET customers by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CustomerResponseDTO>> getCustomersByStatus(@PathVariable String status) {
        List<CustomerResponseDTO> customers = customerService.getCustomersByStatus(status);
        return ResponseEntity.ok(customers);
    }

    // CustomerRestController.java - Thêm vào cuối class

    // GET advanced search with multiple criteria
    @GetMapping("/advanced-search")
    public ResponseEntity<List<CustomerResponseDTO>> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String customerCode) {
        
        AdvancedSearchDTO searchDTO = new AdvancedSearchDTO(name, email, status, phone, customerCode);
        List<CustomerResponseDTO> customers = customerService.advancedSearch(searchDTO);
        return ResponseEntity.ok(customers);
    }

    // GET all customers with pagination
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

    // GET all customers with sorting
    @GetMapping("/sorted")
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomersSorted(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        // Validate sortBy field
        String[] allowedSortFields = {"id", "customerCode", "fullName", "email", "createdAt"};
        if (!Arrays.asList(allowedSortFields).contains(sortBy)) {
            sortBy = "id";
        }
        
        // Validate sort direction
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            sortDir = "asc";
        }
        
        List<CustomerResponseDTO> customers = customerService.getAllCustomersSorted(sortBy, sortDir);
        return ResponseEntity.ok(customers);
    }

    // GET all customers with pagination AND sorting
    @GetMapping("/paginated-sorted")
    public ResponseEntity<Map<String, Object>> getAllCustomersPaginatedAndSorted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        // Validate sortBy field
        String[] allowedSortFields = {"id", "customerCode", "fullName", "email", "createdAt"};
        if (!Arrays.asList(allowedSortFields).contains(sortBy)) {
            sortBy = "id";
        }
        
        // Validate sort direction
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            sortDir = "asc";
        }
        
        Page<CustomerResponseDTO> customerPage = customerService.getAllCustomersPaginatedAndSorted(page, size, sortBy, sortDir);
        
        Map<String, Object> response = new HashMap<>();
        response.put("customers", customerPage.getContent());
        response.put("currentPage", customerPage.getNumber());
        response.put("totalItems", customerPage.getTotalElements());
        response.put("totalPages", customerPage.getTotalPages());
        response.put("pageSize", customerPage.getSize());
        response.put("sortBy", sortBy);
        response.put("sortDir", sortDir);
        
        return ResponseEntity.ok(response);
    }

    // PATCH partial update customer
    @PatchMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> partialUpdateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateDTO updateDTO) {
        
        CustomerResponseDTO updatedCustomer = customerService.partialUpdateCustomer(id, updateDTO);
        return ResponseEntity.ok(updatedCustomer);
    }
}

