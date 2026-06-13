package com.hotelreservation.user.service;

import com.hotelreservation.user.dto.*;
import com.hotelreservation.user.model.User;

import java.util.List;

public interface UserService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserDto getUserById(Long id);

    UserDto getUserByUsername(String username);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}
