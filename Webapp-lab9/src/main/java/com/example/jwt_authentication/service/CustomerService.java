package com.example.jwt_authentication.service;

import com.example.jwt_authentication.dto.CustomerRequestDTO;
import com.example.jwt_authentication.dto.CustomerResponseDTO;
import com.example.jwt_authentication.dto.CustomerUpdateDTO;

import java.util.List;

import org.springframework.data.domain.Page;

public interface CustomerService {
    
    List<CustomerResponseDTO> getAllCustomers();
    
    CustomerResponseDTO getCustomerById(Long id);
    
    CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO);
    
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO);
    
    void deleteCustomer(Long id);
    
    List<CustomerResponseDTO> searchCustomers(String keyword);
    
    List<CustomerResponseDTO> getCustomersByStatus(String status);

    List<CustomerResponseDTO> advancedSearch(String name, String email, String status);

    Page<CustomerResponseDTO> getAllCustomers(int page, int size);

    Page<CustomerResponseDTO> getAllCustomers(int page, int size, String sortBy, String sortDir);

    CustomerResponseDTO partialUpdateCustomer(Long id, CustomerUpdateDTO updateDTO);
}
