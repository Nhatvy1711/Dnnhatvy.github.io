package com.example.jwt_authentication.service;

import java.util.List;

import com.example.jwt_authentication.dto.UserResponseDTO;
import com.example.jwt_authentication.entity.Role;

public interface AdminService {

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUserRole(Long userId, Role role);

    UserResponseDTO toggleUserStatus(Long userId);
}
