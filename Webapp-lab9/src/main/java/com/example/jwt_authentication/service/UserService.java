package com.example.jwt_authentication.service;

import com.example.jwt_authentication.dto.ChangePasswordDTO;
import com.example.jwt_authentication.dto.LoginRequestDTO;
import com.example.jwt_authentication.dto.LoginResponseDTO;
import com.example.jwt_authentication.dto.RefreshTokenDTO;
import com.example.jwt_authentication.dto.RegisterRequestDTO;
import com.example.jwt_authentication.dto.ResetPasswordDTO;
import com.example.jwt_authentication.dto.UpdateProfileDTO;
import com.example.jwt_authentication.dto.UserResponseDTO;

public interface UserService {
    
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    UserResponseDTO register(RegisterRequestDTO registerRequest);
    
    UserResponseDTO getCurrentUser(String username);

    void changePassword(String username, ChangePasswordDTO dto);

    String forgotPassword(String email);

    void resetPassword(ResetPasswordDTO dto);

    UserResponseDTO updateProfile(String username, UpdateProfileDTO dto);

    void deleteAccount(String username, String password);

    LoginResponseDTO refreshAccessToken(RefreshTokenDTO dto);
}

