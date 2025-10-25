package com.ecommerce.demo.controller;

import com.ecommerce.demo.response.UserResponse;
import com.ecommerce.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/id/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer userId){
        UserResponse user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> findByEmail(@PathVariable String email){
        UserResponse user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }
}
