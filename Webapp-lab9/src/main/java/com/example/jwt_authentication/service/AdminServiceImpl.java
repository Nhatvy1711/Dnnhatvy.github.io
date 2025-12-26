package com.example.jwt_authentication.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jwt_authentication.dto.UserResponseDTO;
import com.example.jwt_authentication.entity.Role;
import com.example.jwt_authentication.entity.User;
import com.example.jwt_authentication.exception.ResourceNotFoundException;
import com.example.jwt_authentication.repository.UserRepository;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public UserResponseDTO updateUserRole(Long userId, Role role) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(role);
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO toggleUserStatus(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setIsActive(!user.getIsActive());
        return convertToDTO(userRepository.save(user));
    }

    private UserResponseDTO convertToDTO(User user) {
      return new UserResponseDTO(
          user.getId(),
          user.getUsername(),
          user.getEmail(),
          user.getFullName(),
          user.getRole().name(),
          user.getIsActive(),
          user.getCreatedAt()
      );
  }
}

