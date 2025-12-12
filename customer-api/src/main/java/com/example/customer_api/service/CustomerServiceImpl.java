package com.example.customer_api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.customer_api.dto.AdvancedSearchDTO;
import com.example.customer_api.dto.CustomerRequestDTO;
import com.example.customer_api.dto.CustomerResponseDTO;
import com.example.customer_api.dto.CustomerUpdateDTO;
import com.example.customer_api.entity.Customer;
import com.example.customer_api.entity.CustomerStatus;
import com.example.customer_api.exception.DuplicateResourceException;
import com.example.customer_api.exception.ResourceNotFoundException;
import com.example.customer_api.repository.CustomerRepository;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    
    private final CustomerRepository customerRepository;
    
    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return convertToResponseDTO(customer);
    }
    
    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO) {
        // Check for duplicates
        if (customerRepository.existsByCustomerCode(requestDTO.getCustomerCode())) {
            throw new DuplicateResourceException("Customer code already exists: " + requestDTO.getCustomerCode());
        }
        
        if (customerRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + requestDTO.getEmail());
        }
        
        // Convert DTO to Entity
        Customer customer = convertToEntity(requestDTO);
        
        // Save to database
        Customer savedCustomer = customerRepository.save(customer);
        
        // Convert Entity to Response DTO
        return convertToResponseDTO(savedCustomer);
    }
    
    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        // Check if email is being changed to an existing one
        if (!existingCustomer.getEmail().equals(requestDTO.getEmail()) 
            && customerRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + requestDTO.getEmail());
        }
        
        // Update fields
        existingCustomer.setFullName(requestDTO.getFullName());
        existingCustomer.setEmail(requestDTO.getEmail());
        existingCustomer.setPhone(requestDTO.getPhone());
        existingCustomer.setAddress(requestDTO.getAddress());
        
        // Don't update customerCode (immutable)
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToResponseDTO(updatedCustomer);
    }
    
    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
    
    @Override
    public List<CustomerResponseDTO> searchCustomers(String keyword) {
        return customerRepository.searchCustomers(keyword)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
public List<CustomerResponseDTO> getCustomersByStatus(String status) {
    try {
        // Convert String to Enum
        CustomerStatus statusEnum = CustomerStatus.valueOf(status.toUpperCase());
        return customerRepository.findByStatus(statusEnum)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    } catch (IllegalArgumentException e) {
        // Trả về danh sách rỗng nếu status không hợp lệ
        return new ArrayList<>();
    }
}
    // Helper Methods for DTO Conversion
    
    private CustomerResponseDTO convertToResponseDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setCustomerCode(customer.getCustomerCode());
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        // dto.setStatus(customer.getStatus().toString());
        dto.setStatus(customer.getStatus().toString());
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }
    
    private Customer convertToEntity(CustomerRequestDTO dto) {
        Customer customer = new Customer();
        customer.setCustomerCode(dto.getCustomerCode());
        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        return customer;
    }

    // CustomerServiceImpl.java - Thêm method này

    @Override
public List<CustomerResponseDTO> advancedSearch(AdvancedSearchDTO searchDTO) {
    CustomerStatus statusEnum = null;
    
    if (searchDTO.getStatus() != null && !searchDTO.getStatus().trim().isEmpty()) {
        try {
            statusEnum = CustomerStatus.valueOf(searchDTO.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Nếu status không hợp lệ, coi như không có filter
            // Hoặc có thể throw exception: throw new IllegalArgumentException("Invalid status");
        }
    }
    
    List<Customer> customers = customerRepository.advancedSearch(
        searchDTO.getName(),
        searchDTO.getEmail(),
        statusEnum,  // Truyền enum, có thể là null
        searchDTO.getPhone(),
        searchDTO.getCustomerCode()
    );
    
    return customers.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
}
    // CustomerServiceImpl.java - Thêm method này

    @Override
    public Page<CustomerResponseDTO> getAllCustomersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        
        return customerPage.map(this::convertToResponseDTO);
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomersSorted(String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        List<Customer> customers = customerRepository.findAll(sort);
        
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CustomerResponseDTO> getAllCustomersPaginatedAndSorted(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        
        return customerPage.map(this::convertToResponseDTO);
    }

    @Override
    public CustomerResponseDTO partialUpdateCustomer(Long id, CustomerUpdateDTO updateDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        // Check if email is being changed to an existing one
        if (updateDTO.getEmail() != null && 
            !existingCustomer.getEmail().equals(updateDTO.getEmail()) && 
            customerRepository.existsByEmail(updateDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + updateDTO.getEmail());
        }
        
        // Only update non-null fields
        if (updateDTO.getFullName() != null) {
            existingCustomer.setFullName(updateDTO.getFullName());
        }
        if (updateDTO.getEmail() != null) {
            existingCustomer.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPhone() != null) {
            existingCustomer.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getAddress() != null) {
            existingCustomer.setAddress(updateDTO.getAddress());
        }
        if (updateDTO.getStatus() != null) {
            try {
                CustomerStatus status = CustomerStatus.valueOf(updateDTO.getStatus().toUpperCase());
                existingCustomer.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value. Must be ACTIVE or INACTIVE");
            }
        }
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToResponseDTO(updatedCustomer);
    }
}

