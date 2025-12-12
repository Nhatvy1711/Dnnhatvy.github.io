// File: src/main/java/com/example/customerapi/dto/AdvancedSearchDTO.java

package com.example.customer_api.dto;

public class AdvancedSearchDTO {
    private String name;
    private String email;
    private String status;
    private String phone;
    private String customerCode;
    
    // Constructors
    public AdvancedSearchDTO() {}
    
    public AdvancedSearchDTO(String name, String email, String status, String phone, String customerCode) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.phone = phone;
        this.customerCode = customerCode;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
}