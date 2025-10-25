package com.ecommerce.demo.service;

import com.ecommerce.demo.mappers.UserMapper;
import com.ecommerce.demo.models.User;
import com.ecommerce.demo.repositories.UserRepository;
import com.ecommerce.demo.response.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: "+email));
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponse findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toResponseDto(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream().map(userMapper::toResponseDto).toList();
    }

}
