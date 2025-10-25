package com.ecommerce.demo.service;

import com.ecommerce.demo.response.UserResponse;

import java.util.List;

public interface IUserService {
    UserResponse findByEmail(String email);
    UserResponse findById(Integer id);
    List<UserResponse> getAllUsers();
}
